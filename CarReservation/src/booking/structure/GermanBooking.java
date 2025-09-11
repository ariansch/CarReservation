package booking.structure;

import person.structure.Person;
import resource.structure.Resource;

public final class GermanBooking extends Booking {
	public GermanBooking(String bookingId, Person person, Resource resource, double price) {
		super(bookingId, person, resource, price);
	}
	
	@Override
	public String header() {
		return "Buchungsbestätigung:" + getBookingId();
	}
	
	@Override
	public String body() {
		return "Person: " + getPerson() + ", Auto: " + getResource() + ", Preis: " + getPrice() + " EUR";
	}
	
	@Override
	public String footer() {
		return "Vielen Dank für Ihre Buchung!";
	}
}
