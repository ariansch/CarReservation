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
		if (amount > 0 && balance >= amount) {
			balance -= amount;
			return true;
		}
		return false;
	}

	public void credit(double amount) {
		if (amount > 0) {
			balance += amount;
		}
	}

	@Override
	public String toString() {
		return "Account No: " + accountNumber + " (Owner: " + owner.getName() + ", Balance: "
				+ String.format("%.2f", balance) + ")";
	}
}