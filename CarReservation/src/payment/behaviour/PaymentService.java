package payment.behaviour;

import booking.behaviour.BookingService; // falls ihr das so nutzt
import booking.structure.Booking; // besser nur structure importieren
import payment.structure.*;
import person.structure.LegalPerson;

public class PaymentService {

	private final BookingService bookingService;
	private final Account companyAccount; // Account der Firma, die Zahlungen empfängt

	public PaymentService(BookingService bookingService) {
		this.bookingService = bookingService;
		this.companyAccount = new Account("COMPANY_ACC_001", new LegalPerson("CarReservation GmbH"), 0.0);
	}

	public boolean payBookingById(String bookingId, PaymentType type) {
		Booking b = bookingService.getBookingById(bookingId);
		if (b == null) {
			System.out.println("Payment failed: booking not found: " + bookingId);
			return false;
		}

		Account sender = b.getPerson().getAccount();
		Account receiver = companyAccount;
		CurrencyAmount amount = new CurrencyAmount(b.getPrice(), "EUR"); // ggf. aus Sprache ableiten

		PaymentProcessor p = switch (type) {
		case PAYPAL -> new PayPalProcessor();
		case GOOGLE_WALLET -> new GoogleWalletProcessor();
		case MOBILE_MONEY_WALLET -> new MobileMoneyWalletProcessor();
		};

		return p.processPayment(sender, receiver, amount);
	}

	public Account getCompanyAccount() {
		return companyAccount;
	}
}
