package payment.structure;

import person.structure.Person;

public class Account {
	private final String accountNumber;
	private final Person owner;
	private double balance;

	public Account(String accountNumber, Person owner, double initialBalance) {
		if (accountNumber == null || accountNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("Account number cannot be null or empty.");
		}
		if (owner == null) {
			throw new IllegalArgumentException("Account must have an owner.");
		}
		this.accountNumber = accountNumber;
		this.owner = owner;
		this.balance = initialBalance;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public Person getOwner() {
		return owner;
	}

	public double getBalance() {
		return balance;
	}

	public boolean debit(double amount) {
		if (amount <= 0)
			return false;
		if (balance + 1e-9 < amount)
			return false; // kleine Toleranz gg. Rundungsfehler
		balance -= amount;
		return true;
	}

	public void credit(double amount) {
		if (amount > 0) {
			balance += amount;
		}
	}

	@Override
	public String toString() {
		return "Account No: " + accountNumber + " (Owner: " + owner.getName() + ", Balance: "
				+ String.format(java.util.Locale.ROOT, "%.2f", balance) + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Account a))
			return false;
		return accountNumber.equals(a.accountNumber);
	}

	@Override
	public int hashCode() {
		return accountNumber.hashCode();
	}
}