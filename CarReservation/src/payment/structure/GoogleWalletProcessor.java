package payment.structure;

public class GoogleWalletProcessor extends PaymentProcessor {

	@Override
	protected boolean transferAmount(Account sender, Account receiver, CurrencyAmount amount) {
		System.out.println("Step 2/3 (Google Wallet): Transferring " + amount + "...");

		// Simuliert die eigentliche Transaktion
		if (sender.debit(amount.getAmount())) {
			receiver.credit(amount.getAmount());
			System.out.println("=> Google Wallet transfer successful.");
			return true;
		} else {
			System.out.println("=> Google Wallet transfer failed: Insufficient funds.");
			return false;
		}
	}
}
