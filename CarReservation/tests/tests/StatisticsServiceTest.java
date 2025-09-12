package tests;

import booking.structure.Booking;
import booking.structure.EnglishBooking;
import booking.structure.GermanBooking;
import org.junit.jupiter.api.Test;
import statistics.behaviour.StatisticsService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsServiceTest {

    @Test
    void compute_aggregatesCountsAndTotalsPerLanguage() {
   
        List<Booking> bookings = Arrays.asList(
                new GermanBooking("DE-1", null, null, 10.0),
                new GermanBooking("DE-2", null, null, 20.5),
                new EnglishBooking("EN-1", null, null, 5.0),
                new EnglishBooking("EN-2", null, null, 7.5),
                new EnglishBooking("EN-3", null, null, 2.0)
        );

        StatisticsService svc = new StatisticsService();

     
        StatisticsService.Result r = svc.compute(bookings);

   
        assertEquals(2, r.getGermanCount());
        assertEquals(30.5, r.getGermanTotal(), 1e-9);

        assertEquals(3, r.getEnglishCount());
        assertEquals(14.5, r.getEnglishTotal(), 1e-9);
    }

    @Test
    void filters_returnOnlyRequestedLanguage() {
        
        Booking de1 = new GermanBooking("DE-1", null, null, 10.0);
        Booking en1 = new EnglishBooking("EN-1", null, null, 5.0);
        List<Booking> bookings = Arrays.asList(de1, en1);

        StatisticsService svc = new StatisticsService();

        // Act
        List<Booking> onlyDE = svc.filterGerman(bookings);
        List<Booking> onlyEN = svc.filterEnglish(bookings);

        // Assert
        assertEquals(1, onlyDE.size());
        assertSame(de1, onlyDE.get(0));

        assertEquals(1, onlyEN.size());
        assertSame(en1, onlyEN.get(0));
    }

    @Test
    void compute_withEmptyOrNull_returnsZeros() {
        StatisticsService svc = new StatisticsService();

        // empty
        StatisticsService.Result r1 = svc.compute(Collections.emptyList());
        assertEquals(0, r1.getGermanCount());
        assertEquals(0.0, r1.getGermanTotal(), 1e-9);
        assertEquals(0, r1.getEnglishCount());
        assertEquals(0.0, r1.getEnglishTotal(), 1e-9);

        // null
        StatisticsService.Result r2 = svc.compute(null);
        assertEquals(0, r2.getGermanCount());
        assertEquals(0.0, r2.getGermanTotal(), 1e-9);
        assertEquals(0, r2.getEnglishCount());
        assertEquals(0.0, r2.getEnglishTotal(), 1e-9);
    }
}
