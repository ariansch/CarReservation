package statistics.structure;

import booking.structure.EnglishBooking;
import booking.structure.GermanBooking;

public interface BookingVisitor {
	
	void visit(GermanBooking booking);
	void visit(EnglishBooking booking);
}
