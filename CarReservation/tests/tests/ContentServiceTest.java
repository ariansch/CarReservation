package tests;

import content.behaviour.ContentService;
import content.structure.Content;
import content.structure.File;
import content.structure.Folder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class ContentServiceTest {

    @Test
    void createRecords_createsFoldersAndSummariesWithExpectedValues() {
        ContentService svc = new ContentService();

        Year y = Year.of(2025);
        Month m = Month.JANUARY;

        //  1 Buchung und 1 Zahlung
        BigDecimal booking = new BigDecimal("99.90");
        BigDecimal payment = new BigDecimal("49.50");
        svc.addBookingRecord(y, m, "B_001", booking);
        svc.addPaymentRecord(y, m, "P_001", payment);

        // Jahr-/Monatsordner vorhanden
        Folder yearFolder = findYearFolder(svc, y);
        assertNotNull(yearFolder, "Jahresordner fehlt");
        Folder monthFolder = findMonthFolder(yearFolder, y, m);
        assertNotNull(monthFolder, "Monatsordner fehlt");

        // Monats-Summary vorhanden und korrekt
        String mSummaryName = YearMonth.of(y.getValue(), m) + "-SUMMARY.txt";
        File mSummary = findFile(monthFolder, mSummaryName);
        assertNotNull(mSummary, "Monats-Summary fehlt");
        assertBigDecimalEquals("99.90", mSummary.getBookingTotal());
        assertBigDecimalEquals("49.50", mSummary.getPaymentTotal());

        String ySummaryName = y + "-SUMMARY.txt";
        File ySummary = findFile(yearFolder, ySummaryName);
        assertNotNull(ySummary, "Jahres-Summary fehlt");
        assertEquals(0, ySummary.getBookingTotal().compareTo(yearFolder.getBookingTotal()));
        assertEquals(0, ySummary.getPaymentTotal().compareTo(yearFolder.getPaymentTotal()));

        // Ausgabe sollte nicht leer sein
        String tree = svc.printTree();
        assertNotNull(tree);
        assertFalse(tree.isBlank());
    }

    @Test
    void removeRecord_updatesMonthlyAndYearlySummaries_andReturnsSnapshot() {
        ContentService svc = new ContentService();

        Year y = Year.of(2025);
        Month m = Month.FEBRUARY;

        // zwei Buchungen
        BigDecimal b1 = new BigDecimal("100.00");
        BigDecimal b2 = new BigDecimal("40.00");
        svc.addBookingRecord(y, m, "B_100", b1);
        svc.addBookingRecord(y, m, "B_040", b2);

        // eine Datei löschen
        File snapshot = svc.removeRecord(y, m, "B_040");
        assertNotNull(snapshot, "Snapshot sollte bei erfolgreichem Löschen vorhanden sein");
        assertEquals("B_040", snapshot.getName());
        assertBigDecimalEquals("40.00", snapshot.getBookingTotal());

        // Monats-Summary nur noch 100.00
        Folder yearFolder = findYearFolder(svc, y);
        Folder monthFolder = findMonthFolder(yearFolder, y, m);
        String mSummaryName = YearMonth.of(y.getValue(), m) + "-SUMMARY.txt";
        File mSummary = findFile(monthFolder, mSummaryName);
        assertNotNull(mSummary, "Monats-Summary fehlt nach Löschen");
        assertBigDecimalEquals("100.00", mSummary.getBookingTotal());

        // Jahres-Summary existiert und ist konsistent zum Year-Ordner
        String ySummaryName = y + "-SUMMARY.txt";
        File ySummary = findFile(yearFolder, ySummaryName);
        assertNotNull(ySummary, "Jahres-Summary fehlt nach Löschen");
        assertEquals(0, ySummary.getBookingTotal().compareTo(yearFolder.getBookingTotal()));
        assertEquals(0, ySummary.getPaymentTotal().compareTo(yearFolder.getPaymentTotal()));
    }

    private static Folder findYearFolder(ContentService svc, Year year) {
        for (Content c : svc.getRoot().getChildren()) {
            if (c instanceof Folder f && f.getName().equals(String.valueOf(year.getValue()))) {
                return f;
            }
        }
        return null;
    }

    private static Folder findMonthFolder(Folder yearFolder, Year year, Month month) {
        String m = String.format("%02d-%s", month.getValue(), month.name());
        for (Content c : yearFolder.getChildren()) {
            if (c instanceof Folder f && f.getName().equals(m)) {
                return f;
            }
        }
        return null;
    }

    private static File findFile(Folder folder, String name) {
        for (Content c : folder.getChildren()) {
            if (c instanceof File f && f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    private static void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, actual.compareTo(new BigDecimal(expected)),
                "Expected: " + expected + " but was: " + actual);
    }
}
