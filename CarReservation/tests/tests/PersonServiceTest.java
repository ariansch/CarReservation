package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import person.behaviour.PersonService;
import person.structure.LegalPerson;
import person.structure.NaturalPerson;
import person.structure.Person;

class PersonServiceTest {
	
	private PersonService personService;
	
	@BeforeEach
	public void setUp() {
		personService = new PersonService();
	}
	
	@Test
	// Testen, ob natürliche und juristische Personen korrekt erstellt werden
	public void isPersonCreationByTypeCorrect() {
		personService.createPerson("natural", "person1");
		personService.createPerson("legal", "person2");
		
		Person p1 = personService.findPersonByName("person1");
		Person p2 = personService.findPersonByName("person2");
		
		assertNotNull(p1);
		assertNotNull(p2);
		assertTrue(p1 instanceof NaturalPerson, "person1 should be a NaturalPerson");
		assertTrue(p2 instanceof LegalPerson, "person2 should be a LegalPerson");
	}
	
	@Test
	// Testen, ob die Namenssuche case-insensitive ist und ob toString den Namen zurückgibt
	public void isLookupCaseInsensitiveAndToStringReturnsName() {
		personService.createPerson("natural", "person1");
		Person p = personService.findPersonByName("PERSON1"); // Name in Großbuchstaben
		
		assertEquals("person1", p.getName());
		assertEquals("person1", p.toString());
	}
	
	@Test
	// Testen, ob das Löschen einer Person funktioniert und danach die Person nicht mehr gefunden wird
	public void isPersonDeletedAndLookupFails() {
		personService.createPerson("legal", "person3");
		personService.deletePerson("person3");
		
		IllegalArgumentException exception = 
			assertThrows(IllegalArgumentException.class, () -> {
				personService.findPersonByName("person3");
			});
		assertTrue(exception.getMessage().contains("Person not found"));
	}
	
	@Test
	// Testen, ob das Erstellen einer Person mit leerem Namen eine Exception wirft
	public void isEmptyNameRejectedOnCreate() {
		assertThrows(IllegalArgumentException.class, () -> {
			personService.createPerson("natural", "");
		});
	}
	
	@Test
	// Testen, ob das Erstellen einer Person mit doppeltem Namen ignoriert wird
	public void isDuplicatePersonIgnored() {
		personService.createPerson("natural", "person4");
		personService.createPerson("legal", "person4"); // Duplikat
		
		Person p = personService.findPersonByName("person4");
		assertNotNull(p);
		assertEquals("person4", p.getName());
		assertTrue(p instanceof NaturalPerson, "Original person4 should remain a NaturalPerson");
	}
}
