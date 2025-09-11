package payment.behaviour;

import booking.behaviour.BookingService;
import booking.structure.Booking;
import payment.structure.Account;
import payment.structure.CurrencyAmount;
import payment.structure.PaymentProcessor;
import person.behaviour.PersonService;

public class PaymentService {

	private final PersonService personService;
	private final BookingService bookingService;

	public PaymentService(PersonService personService, BookingService bookingService) {
		this.personService = personService;
		this.bookingService = bookingService;
	}

	public void payAmount(PaymentProcessor processor, String bookingId) {
		try {
			// Schritt 1: Die zugehörige Buchung über den BookingService finden
			Booking bookingToPay = bookingService.getBookingById(bookingId);
			if (bookingToPay == null) {
				System.out.println("Payment failed: Booking with ID '" + bookingId + "' not found.");
				return;
			}

			// Schritt 2: Benötigte Daten aus der Buchung extrahieren
			Account senderAccount = bookingToPay.getPerson().getAccount(); // Annahme: Person hat ein Konto
			CurrencyAmount amount = new CurrencyAmount(bookingToPay.getPrice(), "EUR"); // Währung hier ggf. anpassen

			// Ein Empfängerkonto (z.B. das der Firma)
			Account receiverAccount = new Account("COMPANY_ACCOUNT", new person.structure.LegalPerson("Car Reservation Inc."), 100000);

			System.out.println("Initiating payment for booking " + bookingId + "...");

			// Schritt 3: Die Template Method ausführen
			processor.processPayment(senderAccount, receiverAccount, amount);

		} catch (Exception e) {
			System.out.println("Payment failed: An unexpected error occurred. " + e.getMessage());
		}
	}
}