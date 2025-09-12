package payment.structure;

public class PayPalProcessor extends PaymentProcessor {

	@Override
	protected boolean transferAmount(Account sender, Account receiver, CurrencyAmount amount) {
		System.out.println("Step 2/3 (PayPal): Transferring " + amount + "...");

		// Simuliert die eigentliche Transaktion
		if (sender.debit(amount.getAmount())) {
			receiver.credit(amount.getAmount());
			System.out.println("=> PayPal transfer successful.");
			return true;
		} else {
			System.out.println("=> PayPal transfer failed: Insufficient funds.");
			return false;
		}
	}

}
