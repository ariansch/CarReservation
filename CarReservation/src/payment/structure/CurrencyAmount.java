package payment.structure;

public class CurrencyAmount {
	private final double amount;
	private final String currency;

	public CurrencyAmount(double amount, String currency) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be negative.");
		}
		if (currency == null || currency.trim().isEmpty()) {
			throw new IllegalArgumentException("Currency cannot be null or empty.");
		}
		this.amount = amount;
		this.currency = currency;
	}

	public double getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	@Override
	public String toString() {
		return amount + " " + currency;
		//return String.format("%.2f %s", amount, currency);, wenn man immer 2 Nachkommastellen will
	}

}
