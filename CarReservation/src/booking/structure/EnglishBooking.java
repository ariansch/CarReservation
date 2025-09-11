package booking.structure;

import person.structure.Person;
import resource.structure.Resource;

public final class EnglishBooking extends Booking {
	public EnglishBooking(String bookingId, Person person, Resource resource, double price) {
		super(bookingId, person, resource, price);
	}

	@Override
	public String header() {
		return "Booking Confirmation: " + getBookingId();
	}

	@Override
	public String body() {
		return "Person: " + getPerson() + ", Car: " + getResource() + ", Price: " + getPrice() + " USD";
	}

	@Override
	public String footer() {
		return "Thank you for your booking!";
	}

}
