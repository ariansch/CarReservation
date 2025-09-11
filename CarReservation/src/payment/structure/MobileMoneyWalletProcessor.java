package payment.structure;

public class MobileMoneyWalletProcessor extends PaymentProcessor {

	@Override
	protected boolean transferAmount(Account sender, Account receiver, CurrencyAmount amount) {
		System.out.println("Step 2/3 (Mobile Money Wallet): Transferring " + amount + "...");

		// Simuliert die eigentliche Transaktion
		if (sender.debit(amount.getAmount())) {
			receiver.credit(amount.getAmount());
			System.out.println("=> Mobile Money Wallet transfer successful.");
			return true;
		} else {
			System.out.println("=> Mobile Money Wallet transfer failed: Insufficient funds.");
			return false;
		}
	}
}
