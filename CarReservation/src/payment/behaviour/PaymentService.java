package payment.behaviour;

import booking.behaviour.BookingService;
import payment.structure.Account;
import payment.structure.CurrencyAmount;
import payment.structure.GoogleWalletProcessor;
import payment.structure.MobileMoneyWalletProcessor;
import payment.structure.PaymentProcessor;
import payment.structure.PaymentType;
import payment.structure.PaypalProcessor;
import person.behaviour.PersonService;

public class PaymentService {

	public PaymentService() {
	}

	public boolean payAmount(PaymentType type, Account sender, Account receiver, CurrencyAmount amount) {
		// Wählt die passende Implementierung basierend auf dem PaymentType aus
		PaymentProcessor processor = createProcessor(type);

		System.out.println("PaymentService is processing a payment of " + amount + " from "
				+ sender.getOwner().getName() + " using " + type);
		// Führt den gesamten 3-Schritte-Zahlungsprozess aus
		return processor.processPayment(sender, receiver, amount);
	}

	// Factory-Methode zur Erstellung des passenden PaymentProcessor
	private PaymentProcessor createProcessor(PaymentType type) {
		switch (type) {
		case PAYPAL:
			return new PaypalProcessor();
		case GOOGLE_WALLET:
			return new GoogleWalletProcessor();
		case MOBILE_MONEY_WALLET:
			return new MobileMoneyWalletProcessor();
		default:
			throw new IllegalArgumentException("Unsupported payment type: " + type);
		}
	}
}