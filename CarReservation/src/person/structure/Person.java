package person.structure;

import authentication.structure.Subject;
import payment.structure.Account;
import authentication.structure.Credential;

public abstract class Person implements Subject {
	protected final String name;
	private Credential credential;
	private Account account;

	public Credential getCredential() {
		return this.credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	protected Person(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return name;
	}
}