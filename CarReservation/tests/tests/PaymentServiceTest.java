package tests;

import booking.behaviour.BookingService;
import payment.behaviour.PaymentService;
import payment.structure.Account;
import payment.structure.PaymentType;
import person.behaviour.PersonService;
import resource.behaviour.ResourceService;
import resource.structure.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

	private PersonService personService;
	private ResourceService resourceService;
	private BookingService bookingService;
	private PaymentService paymentService;

	@BeforeEach
	void setup() {
		personService = new PersonService();
		resourceService = new ResourceService();
		bookingService = new BookingService(personService, resourceService);
		paymentService = new PaymentService(bookingService);

		// Testdaten
		resourceService.addResource(new Resource("Audi", 945.0));
	}

	// Person mit Account anlegen
	private Account addPersonWithAccount(String name, double balance) {
		personService.createPerson("natural", name);
		var p = personService.findPersonByName(name);
		var acc = new Account("ACC_" + name, p, balance);
		p.setAccount(acc);
		return acc;
	}

	// Booking anlegen und Preis zurück
	private double createBookingAndReturnPrice(String id, String personName, String carName) {
		// German Booking
		bookingService.createBooking("DE", id, personName, carName);
		return bookingService.getBookingById(id).getPrice();
	}

	@Test
	void payBookingById_success_paypal_updatesBalances() {
		var senderAcc = addPersonWithAccount("Ari", 1000.0);
		var companyAcc = paymentService.getCompanyAccount();

		double price = createBookingAndReturnPrice("1", "Ari", "Audi");

		assertEquals(1000.0, senderAcc.getBalance(), 1e-9);
		assertEquals(0.0, companyAcc.getBalance(), 1e-9);

		boolean ok = paymentService.payBookingById("1", PaymentType.PAYPAL);

		assertTrue(ok);
		assertEquals(1000.0 - price, senderAcc.getBalance(), 1e-9);
		assertEquals(price, companyAcc.getBalance(), 1e-9);
	}

	@Test
	void payBookingById_bookingNotFound_returnsFalse_noBalanceChange() {
		var companyAcc = paymentService.getCompanyAccount();
		assertEquals(0.0, companyAcc.getBalance(), 1e-9);

		boolean ok = paymentService.payBookingById("does-not-exist", PaymentType.PAYPAL);

		assertFalse(ok);
		assertEquals(0.0, companyAcc.getBalance(), 1e-9);
	}

	@Test
	void payBookingById_personWithoutAccount_throwsNPE() {
		// Person ohne Account
		personService.createPerson("natural", "Eve");
		createBookingAndReturnPrice("2", "Eve", "Audi");

		assertThrows(NullPointerException.class, () -> paymentService.payBookingById("2", PaymentType.PAYPAL));
	}
}
