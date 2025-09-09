package person.structure;

public abstract class Person {
	protected final String name;

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
