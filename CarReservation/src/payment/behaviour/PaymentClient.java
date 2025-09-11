package payment.behaviour;

import java.util.Scanner;
import java.util.Stack;
import booking.behaviour.BookingService;
import booking.structure.Booking;
import payment.structure.Account;
import payment.structure.CurrencyAmount;
import payment.structure.PaymentType;
import person.structure.Person; // Import für das Firmenkonto

/**
 * Stellt die Benutzeroberfläche für das Payment-Modul bereit.
 * Verwendet das Command-Pattern für Aktionen mit Undo/Redo-Funktionalität.
 */
public class PaymentClient {

    private final PaymentService paymentService;
    private final BookingService bookingService; // Benötigt, um Buchungen für die Zahlung zu finden
    private final History history = new History();
    private final Scanner scanner = new Scanner(System.in);

    // Annahme: Es gibt ein zentrales Konto des Anbieters, auf das alle Zahlungen gehen.
    private final Account companyAccount;

    public PaymentClient(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        
        // Erstellen einer Dummy-Person und eines Kontos für den Empfänger der Zahlungen
        Person companyOwner = person.structure.Person.create("legal", "CarReservation Inc.");
        this.companyAccount = new Account("DE00123456789", companyOwner, 100000.0);
    }
    
    // --- Command Pattern Implementierung ---
    private interface Command {
        void execute();
        void undo();
    }
    
    private class CreatePaymentCommand implements Command {
        private Booking bookingToPay; // Merken für Undo
        private boolean wasSuccessfullyExecuted = false;

        @Override
        public void execute() {
            System.out.print("Enter Booking ID to pay for: ");
            String bookingId = scanner.nextLine();
            
            try {
                this.bookingToPay = bookingService.getBookingById(bookingId);
                if (bookingToPay == null) {
                    System.out.println("Error: Booking with ID '" + bookingId + "' not found.");
                    return;
                }
                if (bookingToPay.isPaid()) {
                    System.out.println("Info: This booking has already been paid.");
                    this.bookingToPay = null; // Sorgt dafür, dass Command nicht im Stack landet
                    return;
                }

                System.out.println("Select Payment Type: 1=PAYPAL, 2=GOOGLE_WALLET, 3=MOBILE_MONEY_WALLET");
                System.out.print("Your choice: ");
                int typeChoice = Integer.parseInt(scanner.nextLine());
                
                PaymentType type;
                switch(typeChoice) {
                    case 1: type = PaymentType.PAYPAL; break;
                    case 2: type = PaymentType.GOOGLE_WALLET; break;
                    case 3: type = PaymentType.MOBILE_MONEY_WALLET; break;
                    default: System.out.println("Invalid type. Aborting."); return;
                }

                Account sender = bookingToPay.getPerson().getAccount();
                if (sender == null) {
                    System.out.println("Error: The person '" + bookingToPay.getPerson().getName() + "' has no account assigned.");
                    return;
                }
                
                CurrencyAmount amount = new CurrencyAmount(bookingToPay.getPrice(), "EUR");

                boolean success = paymentService.payAmount(type, sender, companyAccount, amount);
                
                if (success) {
                    bookingToPay.setPaid(true);
                    this.wasSuccessfullyExecuted = true;
                }
            } catch (Exception e) {
                System.out.println("An error occurred during payment: " + e.getMessage());
                this.bookingToPay = null;
            }
        }

        @Override
        public void undo() {
            if (wasSuccessfullyExecuted && bookingToPay != null) {
                // Simuliert eine Rückbuchung durch Statusänderung und Gutschrift
                Account sender = bookingToPay.getPerson().getAccount();
                sender.credit(bookingToPay.getPrice());
                companyAccount.debit(bookingToPay.getPrice());
                
                bookingToPay.setPaid(false);
                System.out.println("Undo: Payment for booking '" + bookingToPay.getBookingId() + "' has been reverted.");
            }
        }
    }

    private static class History {
        private final Stack<Command> undoStack = new Stack<>();
        // Redo-Stack könnte hier ebenfalls implementiert werden

        public void execute(Command cmd) {
            cmd.execute();
            // Nur erfolgreich initialisierte Commands zur History hinzufügen
            if (cmd instanceof CreatePaymentCommand) {
                if (((CreatePaymentCommand) cmd).bookingToPay != null) {
                    undoStack.push(cmd);
                }
            }
        }

        public void undo() {
            if (!undoStack.isEmpty()) {
                Command cmd = undoStack.pop();
                cmd.undo();
            } else {
                System.out.println("Nothing to undo.");
            }
        }
    }

    // --- Menüführung ---
    public void start() {
        while (true) {
            System.out.println("\n--- Payment Menu ---");
            System.out.println("1. Make a Payment for a Booking");
            System.out.println("2. List Bookings (to see payment status)");
            System.out.println("3. Undo last payment");
            System.out.println("4. Back to Main Menu");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        history.execute(new CreatePaymentCommand());
                        break;
                    case 2:
                        bookingService.listBookings();
                        break;
                    case 3:
                        history.undo();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}