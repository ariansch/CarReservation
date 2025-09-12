package payment.structure;

public class CurrencyAmount {
	private final double amount; 
	private final String currency;

	public CurrencyAmount(double amount, String currency) {
		if (amount < 0)
			throw new IllegalArgumentException("Amount cannot be negative.");
		if (currency == null || currency.trim().isEmpty())
			throw new IllegalArgumentException("Currency cannot be null or empty.");
		this.amount = amount;
		this.currency = currency.trim().toUpperCase(java.util.Locale.ROOT); // "EUR"
	}

	public double getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	@Override
	public String toString() {
		return String.format(java.util.Locale.ROOT, "%.2f %s", amount, currency);
	}
}
