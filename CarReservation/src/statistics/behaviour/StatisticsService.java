package statistics.behaviour;

import java.util.ArrayList;
import java.util.List;

import booking.structure.Booking;
import booking.structure.EnglishBooking;
import booking.structure.GermanBooking;
import statistics.structure.BookingVisitor;

public class StatisticsService {

	public static final class Result {
		private final int germanCount;
		private final int englishCount;
		private final double germanTotal;
		private final double englishTotal;

		public Result(int germanCount, double germanTotal, int englishCount, double englishTotal) {
			this.germanCount = germanCount;
			this.germanTotal = germanTotal;
			this.englishCount = englishCount;
			this.englishTotal = englishTotal;
		}

		public int getGermanCount() {
			return germanCount;
		}

		public double getGermanTotal() {
			return germanTotal;
		}

		public int getEnglishCount() {
			return englishCount;
		}

		public double getEnglishTotal() {
			return englishTotal;
		}

		@Override
		public String toString() {
			return " Statistics { DE count= " + germanCount + ", total = " + germanTotal + "; EN: count = "
					+ englishCount + ",total = " + englishTotal + ")";

		}
	}

	private static void dispatch(BookingVisitor v, Booking b) {
		if (b instanceof GermanBooking) {
			v.visit((GermanBooking) b);
		} else if (b instanceof EnglishBooking) {
			v.visit((EnglishBooking) b);
		} else {
			throw new IllegalArgumentException("Unknown booking type: " + b.getClass().getName());
		}
	}

	private static final class LangStatsVisitor implements BookingVisitor {
		int gCount = 0;
		double gTotal = 0d;
		int eCount = 0;
		double eTotal = 0d;

		@Override
		public void visit(GermanBooking booking) {
			gCount++;
			gTotal += booking.getPrice();
		}

		@Override
		public void visit(EnglishBooking booking) {
			eCount++;
			eTotal += booking.getPrice();
		}

		Result toResult() {
			return new Result(gCount, gTotal, eCount, eTotal);
		}
	}

	private static final class LanguageCollectVisitor implements BookingVisitor {
		enum Target {
			DE, EN
		}

		private final Target target;
		private final List<Booking> result = new ArrayList<>();

		LanguageCollectVisitor(Target target) {
			this.target = target;
		}

		@Override
		public void visit(GermanBooking booking) {
			if (target == Target.DE)
				result.add(booking);
		}

		@Override
		public void visit(EnglishBooking booking) {
			if (target == Target.EN)
				result.add(booking);
		}

		List<Booking> getResult() {
			return result;
		}
		
	}

		// Über die Iterable durchläuft die Anzahl und Summe und gerechnet
		public Result compute(Iterable<Booking> bookings) {
			LangStatsVisitor v = new LangStatsVisitor();
			if (bookings != null) {
				for (Booking b : bookings) {
					if (b != null)
						dispatch(v, b);
				}
			}
			return v.toResult();
		}

	// Buchungen in deutsch
	public List<Booking> filterGerman(Iterable<Booking> bookings) {
		return collect(bookings, LanguageCollectVisitor.Target.DE);
	}

	// Äquivalent hier english
	public List<Booking> filterEnglish(Iterable<Booking> bookings) {
		return collect(bookings, LanguageCollectVisitor.Target.EN);
	}

	private List<Booking> collect(Iterable<Booking> bookings, LanguageCollectVisitor.Target target) {
		LanguageCollectVisitor v = new LanguageCollectVisitor(target);
		if (bookings != null) {
			for (Booking b : bookings) {
				if (b != null)
					dispatch(v, b);
			}
		}

		return v.getResult();
	}

}
