package booking.behaviour;

import booking.structure.Booking;
import booking.structure.BookingBuilder;
import booking.structure.Lang;
import person.behaviour.PersonService;
import resource.behaviour.ResourceService;

public class BookingService {
	// einfacher Speicher für Bookings
	private final Booking[] store = new Booking[200];
	private int size = 0;
	
	private final PersonService personService;
	private final ResourceService resourceService;
	
	public BookingService(PersonService personService, ResourceService resourceService) {
		this.personService = personService;
		this.resourceService = resourceService;
	}
	
	public void createBooking(String language, String bookingId, String personName, String resourceName, double price) {
		var person = personService.findPersonByName(personName);
		var resource = resourceService.getSelectedResource(resourceName);
		
		var lang = "EN".equalsIgnoreCase(language) ? Lang.EN : Lang.DE;
		
		BookingBuilder builder = new BookingBuilder()
				.id(bookingId)
				.person(person)
				.resource(resource)
				.price(price)
				.lang(lang);
		
		Booking booking = builder.build();
		// speichern des Bookings
		if (findIndexById(booking.getBookingId()) >= 0)
			throw new IllegalArgumentException("Booking with id " + booking.getBookingId() + " already exists");
		if (size >= store.length)
			throw new IllegalStateException("Booking store is full");
		store[size++] = booking;
		
		System.out.println("Booking created with ID: " + bookingId);
	}
	
	public void deleteBooking(String bookingId) {
		int idx = findIndexById(bookingId);
		if (idx < 0)
			throw new IllegalArgumentException("Booking with id " + bookingId + " not found");
		// löschen des Bookings
		for (int i = idx; i < size - 1; i++)
			store[i] = store[i + 1];
		size--;
		store[size] = null;
		System.out.println("Booking with ID: " + bookingId + " has been removed.");
	}
	
	public String getBookingFooterById(String bookingId) {
		Booking b = getBookingById(bookingId);
		return (b != null) ? b.footer() : "Booking with ID: " + bookingId + " not found.";
	}
	
	public String getBookingBodyById(String bookingId) {
		Booking b = getBookingById(bookingId);
		return (b != null) ? b.body() : "Booking with ID: " + bookingId + " not found.";
	}
	
	public void listBookings() {
		if (size == 0) {
			System.out.println("No bookings available.");
			return;
		}
		for (int i = 0; i < size; i++) {
			Booking b = store[i];
			System.out.println("Booking ID: " + b.getBookingId() + "\n" +
					"Header: " + b.header() + "\n" +
					"Body: " + b.body() + "\n" +
					"Footer: " + b.footer() + "\n");
		}
	}
	
	public Booking getBookingById(String bookingId) {
		int idx = findIndexById(bookingId);
		return (idx >= 0) ? store[idx] : null;
	}
	
	private int findIndexById(String id) {
		for (int i = 0; i < size; i++)
			if (store[i].getBookingId().equals(id))
				return i;
				return -1;
	}

}
