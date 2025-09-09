package person.structure;

public final class PersonFactory {
	private PersonFactory() {
	}

	public static Person create(String type, String name) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Name cannot be null or empty");
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");

		String t = type.trim().toLowerCase();
		String n = name.trim();

		if (t.startsWith("nat"))
			return new NaturalPerson(n);
		if (t.startsWith("leg"))
			return new LegalPerson(n);

		throw new IllegalArgumentException("Unknown person type: " + type);
	}
}