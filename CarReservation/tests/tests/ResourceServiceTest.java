package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import resource.behaviour.ResourceService;
import resource.structure.Car;
import resource.structure.ChildSeat;
import resource.structure.Resource;
import resource.structure.SetTopBox;

class ResourceServiceTest {
	
	private ResourceService resourceService;
	
	@BeforeEach
	public void setUp() {
		resourceService = new ResourceService();
	}
	
	@Test
	// Testen, ob ein Auto hinzugefügt und ausgewählt werden kann
	public void isBaseCarAddedAndSelectable() {
		resourceService.addResource(new Car("BMW i7", 500));
		
		Resource sel = resourceService.getSelectedResource("BMW i7");
		assertNotNull(sel);
		assertEquals("BMW i7", sel.getName());
		assertEquals(500, sel.getPrice());
	}
	
	@Test
	// Testen, ob der Preis eines Autos mit 1 Extra korrekt berechnet wird
	public void isSetTopBoxPriceApplied() {
		resourceService.addResource(new SetTopBox(new Car("Audi A8", 400)));
		
		Resource sel = resourceService.getSelectedResource("Audi A8");
		assertEquals(430, sel.getPrice()); // 400 + 30 für SetTopBox
		assertTrue(sel.toString().contains("SetTopBox"));
	}
	
	@Test
	// Testen, ob der Preis eines Autos mit mehreren Extras korrekt berechnet wird
	public void isMultipleDecoratorsPriceApplied() {
		resourceService.addResource(new SetTopBox(new ChildSeat(new Car("Mercedes EQE", 450))));
		
		Resource sel = resourceService.getSelectedResource("Mercedes EQE");
		assertEquals(495, sel.getPrice()); // 450 + 30 + 15 für SetTopBox und ChildSeat
		assertTrue(sel.toString().contains("SetTopBox"));
		assertTrue(sel.toString().contains("Child Seat"));
	}
	
	@Test
	// Testen, ob das Entfernen eines Autos funktioniert und danach die Auswahl eine Exception wirft
	public void isResourceRemovedAndLookupFails() {
		resourceService.addResource(new Car("VW ID.4", 350));
		resourceService.removeResource("VW ID.4");
		
		assertThrows(IllegalArgumentException.class, () -> {
			resourceService.getSelectedResource("VW ID.4");
		});
	}
	
	@Test
	// Testen, ob das Hinzufügen eines Duplikats ignoriert wird und das Original bestehen bleibt
	public void isDuplicateResourceIgnored() {
		resourceService.addResource(new Car("Tesla Model 3", 450));
		resourceService.addResource(new Car("Tesla Model 3", 500)); // Duplikat mit anderem Preis
		
		Resource sel = resourceService.getSelectedResource("Tesla Model 3");
		assertEquals(450, sel.getPrice()); // Originalpreis sollte bleiben
		assertEquals("Tesla Model 3", sel.getName());
	}
	
	@Test
	// Testen, ob die Auswahl des Autos case-insensitive ist
	public void isResourceLookupCaseInsensitive() {
		resourceService.addResource(new Car("Nissan Leaf", 300));
		
		Resource sel = resourceService.getSelectedResource("nissan leaf"); // Name in Kleinbuchstaben
		assertEquals(300, sel.getPrice());
	}

}
