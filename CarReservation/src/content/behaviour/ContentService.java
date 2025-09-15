package content.behaviour;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Objects;

import content.structure.Content;
import content.structure.File;
import content.structure.Folder;

public class ContentService {

    private final Folder root = new Folder("Content");

    public Folder getRoot() { return root; }

  
    public File addBookingRecord(Year year, Month month, String name, BigDecimal amount) {
        Objects.requireNonNull(year, "year");
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(amount, "amount");

        Folder monthFolder = getOrCreateMonthFolder(year, month);
        File f = File.bookingRecord(name, amount, "Booking: " + name + " = " + fmt(amount));
        monthFolder.add(f);

        rebuildMonthlySummary(year, month);
        rebuildYearlySummary(year);
        return f;
    }


    public File addPaymentRecord(Year year, Month month, String name, BigDecimal amount) {
        Objects.requireNonNull(year, "year");
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(amount, "amount");

        Folder monthFolder = getOrCreateMonthFolder(year, month);
        File f = File.paymentRecord(name, amount, "Payment: " + name + " = " + fmt(amount));
        monthFolder.add(f);

        rebuildMonthlySummary(year, month);
        rebuildYearlySummary(year);
        return f;
    }

   
    public File removeRecord(Year year, Month month, String name) {
        Objects.requireNonNull(year, "year");
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(name, "name");

        Folder monthFolder = getOrCreateMonthFolder(year, month);

        Content toRemove = monthFolder.getChildren().stream()
                .filter(c -> c instanceof File && name.equals(c.getName()))
                .findFirst()
                .orElse(null);

        if (toRemove == null) {
            throw new IllegalArgumentException("No file named '" + name + "' found in " + year + "/" + month);
        }

        monthFolder.remove(toRemove);

        rebuildMonthlySummary(year, month);
        rebuildYearlySummary(year);
        return (File) toRemove;
    }

    public String printTree() {
        return root.printTree();
    }



    private Folder getOrCreateYearFolder(Year year) {
        String y = String.valueOf(year.getValue());
        return root.getChildren().stream()
                .filter(Folder.class::isInstance).map(Folder.class::cast)
                .filter(f -> f.getName().equals(y))
                .findFirst()
                .orElseGet(() -> { Folder f = new Folder(y); root.add(f); return f; });
    }

    private Folder getOrCreateMonthFolder(Year year, Month month) {
        Folder yearFolder = getOrCreateYearFolder(year);
        String m = String.format("%02d-%s", month.getValue(), month.name());
        return yearFolder.getChildren().stream()
                .filter(Folder.class::isInstance).map(Folder.class::cast)
                .filter(f -> f.getName().equals(m))
                .findFirst()
                .orElseGet(() -> { Folder f = new Folder(m); yearFolder.add(f); return f; });
    }

    private void rebuildMonthlySummary(Year year, Month month) {
        Folder monthFolder = getOrCreateMonthFolder(year, month);
        YearMonth ym = YearMonth.of(year.getValue(), month);
        String summaryName = ym + "-SUMMARY.txt";

        // Alte Monats-Summary entfernen
        monthFolder.getChildren().stream()
                .filter(c -> c instanceof File && c.getName().equals(summaryName))
                .findFirst()
                .ifPresent(monthFolder::remove);

        // Summen nach Entfernen berechnen
        BigDecimal sumB = monthFolder.getBookingTotal();
        BigDecimal sumP = monthFolder.getPaymentTotal();

      
        String payload = "Monthly summary " + ym + " — Bookings =" + fmt(sumB) + " | Payments =" + fmt(sumP);

        File summary = File.summary(summaryName, sumB, sumP, payload);
        monthFolder.add(summary);
    }

    private void rebuildYearlySummary(Year year) {
        Folder yearFolder = getOrCreateYearFolder(year);
        String summaryName = year + "-SUMMARY.txt";

        // Alte Jahres-Summary entfernen
        yearFolder.getChildren().stream()
                .filter(c -> c instanceof File && c.getName().equals(summaryName))
                .findFirst()
                .ifPresent(yearFolder::remove);

        BigDecimal sumB = yearFolder.getBookingTotal();
        BigDecimal sumP = yearFolder.getPaymentTotal();

      
        String payload = "Yearly summary " + year + " — Bookings =" + fmt(sumB) + " | Payments =" + fmt(sumP);

        File summary = File.summary(summaryName, sumB, sumP, payload);
        yearFolder.add(summary);
    }

    private static String fmt(BigDecimal x) {
        return (x == null ? "0.00" : x.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }
}
