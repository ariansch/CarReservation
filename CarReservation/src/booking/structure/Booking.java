package booking.structure;

import person.structure.Person;
import resource.structure.Resource;

public abstract class Booking {
	private final String bookingId;
	private final Person person;
	private final Resource resource;
	private final double price;
	
	protected Booking(String bookingId, Person person, Resource resource, double price) {
		this.bookingId = bookingId;
		this.person = person;
		this.resource = resource;
		this.price = price;
	}
	
	public abstract String header();
	public abstract String body();
	public abstract String footer();
	
	public final String render() {
		return header() + "\n" + body() + "\n" + footer();
	}
	
	public final String getBookingId() {
		return bookingId;
	}
	
	public final Person getPerson() {
		return person;
	}
	
	public final Resource getResource() {
		return resource;
	}
	
	public final double getPrice() {
		return price;
	}

}
