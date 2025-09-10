package person.structure;

import authentication.structure.Subject;
import authentication.structure.Credential;

public abstract class Person implements Subject{
	protected final String name;
	
	private Credential credential;

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

	@Override
	public String toString() {
		return name;
	}
}
