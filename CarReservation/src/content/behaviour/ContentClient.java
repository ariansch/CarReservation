package content.behaviour;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.Scanner;

import content.structure.File;


public class ContentClient {


    private interface Command {
        void execute();
        void undo();
    }

    private static final class History {
        private final Command[] undo = new Command[200];
        private final Command[] redo = new Command[200];
        private int uTop = 0, rTop = 0;

        void execute(Command cmd) {
            cmd.execute();
            undo[uTop++] = cmd;
            rTop = 0; 
        }
        boolean canUndo() { return uTop > 0; }
        boolean canRedo() { return rTop > 0; }
        void undo() {
            if (uTop > 0) {
                Command cmd = undo[--uTop];
                cmd.undo();
                redo[rTop++] = cmd;
            }
        }
        void redo() {
            if (rTop > 0) {
                Command cmd = redo[--rTop];
                cmd.execute();
                undo[uTop++] = cmd;
            }
        }
    }



    private static final class CreateCommand implements Command {
        private final ContentService svc;
        private final Year year;
        private final Month month;
        private final String name;
        private final boolean isBooking; // true=Booking, false=Payment
        private final BigDecimal amount;

        public CreateCommand(ContentService svc, Year year, Month month, String name, boolean isBooking, BigDecimal amount) {
            this.svc = svc;
            this.year = year;
            this.month = month;
            this.name = name;
            this.isBooking = isBooking;
            this.amount = amount;
        }

        @Override public void execute() {
            if (isBooking) {
                svc.addBookingRecord(year, month, name, amount);
            } else {
                svc.addPaymentRecord(year, month, name, amount);
            }
        }

        @Override public void undo() {
            svc.removeRecord(year, month, name);
        }
    }

    private static final class DeleteCommand implements Command {
        private final ContentService svc;
        private final Year year;
        private final Month month;
        private final String name;

        private File snapshot; // für Undo

        public DeleteCommand(ContentService svc, Year year, Month month, String name) {
            this.svc = svc;
            this.year = year;
            this.month = month;
            this.name = name;
        }

        @Override public void execute() {
            snapshot = svc.removeRecord(year, month, name);
            if (snapshot == null) {
                System.out.println("Keine Datei \"" + name + "\" gefunden in " + year + "-" + month + ".");
            }
        }

        @Override public void undo() {
            if (snapshot == null) return;
            if (snapshot.getBookingTotal().signum() > 0) {
                svc.addBookingRecord(year, month, snapshot.getName(), snapshot.getBookingTotal());
            } else if (snapshot.getPaymentTotal().signum() > 0) {
                svc.addPaymentRecord(year, month, snapshot.getName(), snapshot.getPaymentTotal());
            } else {
                svc.addBookingRecord(year, month, snapshot.getName(), BigDecimal.ZERO);
            }
        }
    }

    /* ===================== Client State ===================== */

    private final ContentService contentService;
    private final History history = new History();
    private final Scanner scanner = new Scanner(System.in);

    public ContentClient(ContentService contentService) {
        this.contentService = contentService;
    }

    public void start() {
        while (true) {
            System.out.println("1. Daten eingeben");
            System.out.println("2. Daten löschen");
            System.out.println("3. Daten ausgeben");
            System.out.println("4. Undo");
            System.out.println("5. Redo");
            System.out.println("6. Exit");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createRecord();
                    case "2" -> deleteRecord();
                    case "3" -> System.out.println(contentService.printTree());
                    case "4" -> { if (history.canUndo()) history.undo(); else System.out.println("Nothing to undo."); }
                    case "5" -> { if (history.canRedo()) history.redo(); else System.out.println("Nothing to redo."); }
                    case "6" -> { System.out.println("Exiting..."); return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void createRecord() {
        Year year = readYear("Jahr (z.B. 2025): ");
        Month month = readMonth("Monat (1-12): ");
        System.out.print("Typ (B=Booking, P=Payment): ");
        String type = scanner.nextLine().trim().toUpperCase();
        boolean isBooking = type.startsWith("B");

        System.out.print("Name der Datei: ");
        String name = scanner.nextLine().trim();

        BigDecimal amount = readBigDecimal("Betrag (z.B. 99.90): ");

        history.execute(new CreateCommand(contentService, year, month, name, isBooking, amount));
    }

    private void deleteRecord() {
        Year year = readYear("Jahr der Datei: ");
        Month month = readMonth("Monat der Datei (1-12): ");
        System.out.print("Name der zu löschenden Datei: ");
        String name = scanner.nextLine().trim();

        history.execute(new DeleteCommand(contentService, year, month, name));
    }

    private Year readYear(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return Year.of(Integer.parseInt(s));
            } catch (Exception ignored) {
                System.out.println("Bitte eine gültige Jahreszahl eingeben.");
            }
        }
    }

    private Month readMonth(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int m = Integer.parseInt(s);
                if (m < 1 || m > 12) throw new IllegalArgumentException();
                return Month.of(m);
            } catch (Exception ignored) {
                System.out.println("Bitte eine Zahl von 1 bis 12 eingeben.");
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim().replace(',', '.');
            try {
                return new BigDecimal(s);
            } catch (Exception ignored) {
                System.out.println("Bitte einen gültigen Dezimalbetrag eingeben (z.B. 99.90).");
            }
        }
    }

    public static void main(String[] args) {
        new ContentClient(new ContentService()).start();
    }
}
