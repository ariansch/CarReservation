package content.behaviour;

import content.structure.Content;
import content.structure.File;
import content.structure.Folder;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Objects;

public class ContentService {

    private final Folder root = new Folder("Content");

    public Folder getRoot() { return root; }

    public void addBookingRecord(Year year, Month month, String name, BigDecimal amount) {
        Objects.requireNonNull(year, "year");
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(amount, "amount");

        Folder monthFolder = getOrCreateMonthFolder(year, month);
        monthFolder.add(File.bookingRecord(name, amount, "Booking: " + name + " = " + amount));
        rebuildMonthlySummary(year, month);
        rebuildYearlySummary(year);
    }

    public void addPaymentRecord(Year year, Month month, String name, BigDecimal amount) {
        Objects.requireNonNull(year, "year");
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(amount, "amount");

        Folder monthFolder = getOrCreateMonthFolder(year, month);
        monthFolder.add(File.paymentRecord(name, amount, "Payment: " + name + " = " + amount));
        rebuildMonthlySummary(year, month);
        rebuildYearlySummary(year);
    }

    public File removeRecord(Year year, Month month, String name) {
        Objects.requireNonNull(year, "year");
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(name, "name");

        Folder monthFolder = getOrCreateMonthFolder(year, month);
        File target = findFileByName(monthFolder, name);
        if (target == null) {
            return null;
        }
        monthFolder.remove(target);
        rebuildMonthlySummary(year, month);
        rebuildYearlySummary(year);

        return new File(
                target.getName(),
                "text/plain",
                target.getPayload(),
                target.getBookingTotal(),
                target.getPaymentTotal()
        );
    }


    public String printTree() {
        return root.printTree();
    }



    private Folder getOrCreateYearFolder(Year year) {
        String y = String.valueOf(year.getValue());
        return root.getChildren().stream()
                .filter(Folder.class::isInstance)
                .map(Folder.class::cast)
                .filter(f -> f.getName().equals(y))
                .findFirst()
                .orElseGet(() -> {
                    Folder f = new Folder(y);
                    root.add(f);
                    return f;
                });
    }

    private Folder getOrCreateMonthFolder(Year year, Month month) {
        Folder yearFolder = getOrCreateYearFolder(year);
        String m = String.format("%02d-%s", month.getValue(), month.name());
        return yearFolder.getChildren().stream()
                .filter(Folder.class::isInstance)
                .map(Folder.class::cast)
                .filter(f -> f.getName().equals(m))
                .findFirst()
                .orElseGet(() -> {
                    Folder f = new Folder(m);
                    yearFolder.add(f);
                    return f;
                });
    }

    private static File findFileByName(Folder monthFolder, String name) {
        for (Content c : monthFolder.getChildren()) {
            if (c instanceof File f && f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    private void rebuildMonthlySummary(Year year, Month month) {
        Folder monthFolder = getOrCreateMonthFolder(year, month);
        YearMonth ym = YearMonth.of(year.getValue(), month);
        String summaryName = ym + "-SUMMARY.txt";

        File old = findFileByName(monthFolder, summaryName);
        if (old != null) {
            monthFolder.remove(old);
        }

        BigDecimal sumB = monthFolder.getBookingTotal();
        BigDecimal sumP = monthFolder.getPaymentTotal();

        File summary = File.summary(
                summaryName,
                sumB, sumP,
                "Summary " + ym + " | Bookings=" + sumB + ", Payments=" + sumP
        );
        monthFolder.add(summary);
    }

    private void rebuildYearlySummary(Year year) {
        Folder yearFolder = getOrCreateYearFolder(year);
        String summaryName = year + "-SUMMARY.txt";

        File toRemove = null;
        for (Content c : yearFolder.getChildren()) {
            if (c instanceof File f && f.getName().equals(summaryName)) {
                toRemove = f; break;
            }
        }
        if (toRemove != null) {
            yearFolder.remove(toRemove);
        }

        BigDecimal sumB = yearFolder.getBookingTotal();
        BigDecimal sumP = yearFolder.getPaymentTotal();

        File summary = File.summary(
                summaryName,
                sumB, sumP,
                "Yearly summary " + year + " | Bookings=" + sumB + ", Payments=" + sumP
        );
        yearFolder.add(summary);
    }
}
