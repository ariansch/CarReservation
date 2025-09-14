package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import booking.behaviour.BookingService;
import booking.structure.Booking;
import person.behaviour.PersonService;
import resource.behaviour.ResourceService;
import resource.structure.Resource;

public class BookingServiceTest {
	
	private BookingService bookingService;
	private PersonService personService;
	private ResourceService resourceService;
	
	@BeforeEach
	public void setUp() {
		personService = new PersonService();
		resourceService = new ResourceService();
		bookingService = new BookingService(personService, resourceService);
		
		// Testdaten
		personService.createPerson("legal", "person1");
		personService.createPerson("natural", "person2");
		
		resourceService.addResource(new Resource("Resource1", 100.0));
		resourceService.addResource(new Resource("Resource2", 200.0));
	}
	
	@Test
	// Testen, ob bei einer deutschen Buchung Header, Body und Footer die erwarteten Informationen enthalten
	public void isGermanBookingRendered() {
		// Sprache Deutsch
		bookingService.createBooking("DE", "1", "person1", "Resource1");
		
		Booking b = bookingService.getBookingById("1");
		assertNotNull(b, "Booking should exist after creation");
		
		// Header
		assertTrue(b.header().contains("Buchungsbestätigung"), "Header should be in German");
		assertTrue(b.header().contains("1"), "Header should contain booking ID");
		
		// Body
		String body = b.body();
		assertTrue(body.contains("100.0"), "Body should contain price");
		assertTrue(body.toUpperCase().contains("EUR"), "Body should contain currency EUR");
		
		// Footer
		assertTrue(b.footer().contains("Vielen Dank"), "Footer should be in German");
	}
	
	@Test
	// Testen, ob bei einer englischen Buchung Header, Body und Footer die erwarteten Informationen enthalten
	public void isEnglishBookingRendered() {
		bookingService.createBooking("EN", "2", "person2", "Resource2");
		
		Booking b = bookingService.getBookingById("2");
		assertNotNull(b, "Booking should exist after creation");
		
		// Header
		assertTrue(b.header().contains("Booking Confirmation"), "Header should be in English");
		assertTrue(b.header().contains("2"), "Header should contain booking ID");
		
		// Body
		String body = b.body();
		assertTrue(body.contains("200.0"), "Body should contain price");
		assertTrue(body.toUpperCase().contains("USD"), "Body should contain currency USD");
		
		// Footer
		assertTrue(b.footer().contains("Thank you"), "Footer should be in English");
	}
	
	@Test
	// Testen, ob eine Buchung gelöscht werden kann und danach nicht mehr gefunden wird
	public void isBookingDeletedAndLookupFails() {
		bookingService.createBooking("DE", "3", "person1", "Resource1");
		bookingService.createBooking("EN", "4", "person2", "Resource2");
		
		bookingService.deleteBooking("3");
		String msg = bookingService.getBookingBodyById("3");
		assertEquals("Booking with ID: 3 not found.", msg);
		// Booking "4" sollte noch existieren
		assertNotNull(bookingService.getBookingById("4"));
	}
	
	@Test
	// Testen, ob der Footer einer Buchung korrekt lokalisiert wird
	public void isBookingFooterLocalized() {
		bookingService.createBooking("EN", "5", "person2", "Resource2");
		String footer = bookingService.getBookingFooterById("5");
		assertTrue(footer.contains("Thank you"), "English Footer expected");
	}
	
	@Test
	// Testen, ob das Erstellen einer Buchung mit doppelter ID eine Exception wirft
	public void isDuplicateBookingIdRejected() {
		bookingService.createBooking("DE", "6", "person1", "Resource1");
		try {
			bookingService.createBooking("EN", "6", "person2", "Resource2");
		} catch (IllegalArgumentException e) {
			assertEquals("Booking with id 6 already exists", e.getMessage());
		}
	}

}
