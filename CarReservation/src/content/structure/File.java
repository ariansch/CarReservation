package content.structure;

import java.math.BigDecimal;
import java.util.Objects;

public class File extends Content {

    private final String mimeType;
    private final String payload;
    private final BigDecimal bookingTotal;
    private final BigDecimal paymentTotal;
    private final boolean summary; 


    private File(String name, String mimeType, String payload,
                 BigDecimal bookingTotal, BigDecimal paymentTotal, boolean summary) {
        super(Objects.requireNonNull(name, "name"));
        this.mimeType = (mimeType == null || mimeType.isBlank()) ? "text/plain" : mimeType;
        this.payload = payload == null ? "" : payload;
        this.bookingTotal = bookingTotal == null ? BigDecimal.ZERO : bookingTotal;
        this.paymentTotal = paymentTotal == null ? BigDecimal.ZERO : paymentTotal;
        this.summary = summary;
    }


    public File(String name, String mimeType, String payload,
                BigDecimal bookingTotal, BigDecimal paymentTotal) {
        this(name, mimeType, payload, bookingTotal, paymentTotal, false);
    }

    public File(String name) {
        this(name, "text/plain", "", BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    public File(String name, String payload) {
        this(name, "text/plain", payload, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    public static File bookingRecord(String name, BigDecimal amount, String payload) {
        return new File(name, "text/plain", payload, amount, BigDecimal.ZERO, false);
    }

    public static File paymentRecord(String name, BigDecimal amount, String payload) {
        return new File(name, "text/plain", payload, BigDecimal.ZERO, amount, false);
    }

    public static File summary(String name, BigDecimal bookingSum, BigDecimal paymentSum, String payload) {
        return new File(name, "text/plain", payload, bookingSum, paymentSum, true);
    }

    public String getMimeType() { return mimeType; }
    public String getPayload()  { return payload;  }

    @Override public BigDecimal getBookingTotal()  { return bookingTotal; }
    @Override public BigDecimal getPaymentTotal()  { return paymentTotal; }

    @Override public BigDecimal getBookingContribution() { return summary ? BigDecimal.ZERO : bookingTotal; }
    @Override public BigDecimal getPaymentContribution() { return summary ? BigDecimal.ZERO : paymentTotal; }

    @Override
    public void print(StringBuilder out, int indent) {
        out.append(" ".repeat(Math.max(0, indent)))
           .append("File ").append(getName());

        boolean hasSums = bookingTotal.signum() != 0 || paymentTotal.signum() != 0;
        if (hasSums) {
            out.append("  [Bookings =").append(bookingTotal)
               .append(", (Payments =").append(paymentTotal)
               .append("]");
        }
        if (!payload.isBlank()) {
            out.append("  - ").append(payload);
        }
        out.append(System.lineSeparator());
    }
}
