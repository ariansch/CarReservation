package booking.structure;

import person.structure.Person;
import resource.structure.Resource;

public final class BookingBuilder {
	
	private String bookingId;
	private Person person;
	private Resource resource;
	private double price;
	private Lang lang = Lang.DE; // default language

	public BookingBuilder id(String id)
	{
		this.bookingId = id;
		return this;
	}
	
	public BookingBuilder person(Person p)
	{
		this.person = p;
		return this;
	}
	
	public BookingBuilder resource(Resource r)
	{
		this.resource = r;
		return this;
	}
	
	public BookingBuilder price(double p)
	{
		this.price = p;
		return this;
	}
	
	public BookingBuilder lang(Lang l)
	{
		this.lang = l;
		return this;
	}
	
	public boolean hasId() { return bookingId != null && bookingId.length() > 0; }
	
	public Booking build() {
		if (bookingId == null || bookingId.length() == 0)
			throw new IllegalStateException("Booking must have an id");
		if (person == null)
			throw new IllegalStateException("Booking must have a person");
		if (resource == null)
			throw new IllegalStateException("Booking must have a resource");
		if (price <= 0)
			throw new IllegalStateException("Booking must have a positive price");
		if (lang == Lang.DE)
			return new GermanBooking(bookingId, person, resource, price);
		else
			return new EnglishBooking(bookingId, person, resource, price);
	}
}
