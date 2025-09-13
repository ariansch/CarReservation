package statistics.behaviour;

import java.util.concurrent.ConcurrentHashMap;
import payment.structure.PaymentType;

public final class StatisticsRepository {
    private static final ConcurrentHashMap<String, PaymentType> paymentByBookingId = new ConcurrentHashMap<>();
    private StatisticsRepository() {}

    public static void recordPayment(String bookingId, PaymentType type) {
        if (bookingId != null && !bookingId.isBlank() && type != null)
            paymentByBookingId.put(bookingId, type);
    }

    public static PaymentType getPaymentType(String bookingId) {
        return paymentByBookingId.get(bookingId);
    }
}