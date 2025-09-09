package person.behaviour;

import person.structure.Person;
import person.structure.PersonFactory;

public class PersonService {
	// Speicher
	private final Person[] store = new Person[200];
	private int size = 0;
	
	public void createPerson(String type, String name) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Name cannot be null or empty");
		if (findIndexByName(name) >= 0) {
			System.out.println("Person with name " + name + " already exists.");
			return;
		}
		if (size >= store.length)
			throw new IllegalStateException("Person store is full");
	
		Person p = PersonFactory.create(type, name);
		store[size++] = p;
		System.out.println("Person added: " + p);
	}
	
	// Liste aller Personen ausgeben
	public void listPersons() {
		if (size == 0) {
			System.out.println("No persons available.");
			return;
		}
		for (int i = 0; i < size; i++)
			System.out.println("- " + store[i]);
	}
	
	public void deletePerson(String name) {
		int idx = findIndexByName(name);
		if (idx < 0) {
			System.out.println("Person not found: " + name);
			return;
		}
		// Löschen der Person
		for (int i = idx; i < size - 1; i++)
			store[i] = store[i + 1];
		size--;
		store[size] = null;
		System.out.println("Person deleted: " + name);
	}
	
	// Benötigt für BookingService
	public Person findPersonByName(String name) {
		int idx = findIndexByName(name);
		if (idx < 0) throw new IllegalArgumentException("Person not found: " + name);
		return store[idx];
	}
	
	private int findIndexByName(String name) {
		if (name == null)
			return -1;
		for (int i = 0; i < size; i++) {
			if (store[i].getName().equalsIgnoreCase(name))
				return i;
		}
		return -1;
	}
}
