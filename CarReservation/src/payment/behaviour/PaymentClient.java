package payment.behaviour;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import booking.behaviour.BookingService;
import payment.structure.Account;
import payment.structure.PaymentType;
import person.structure.Person;
import person.structure.NaturalPerson;
import person.structure.LegalPerson;
import resource.behaviour.ResourceService;
import person.behaviour.PersonService;

public class PaymentClient {

    private final Scanner scanner = new Scanner(System.in);

    // In-Memory "Repository" für Accounts (nur für Anzeige/Command)
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    // Command-History
    private final Deque<Command> undo = new ArrayDeque<>();
    private final Deque<Command> redo = new ArrayDeque<>();
=======
	/**
	 * Defines the common interface for all command objects.
	 */
	private interface Command {
		void execute();
		void undo();
	}
>>>>>>> branch 'main' of https://github.com/ariansch/CarReservation.git

    private final PaymentService paymentService;

    public PaymentClient(PaymentService paymentService, Account companyAccount) {
        this.paymentService = paymentService;
        // Firmenkonto sichtbar machen
        if (companyAccount != null) {
            accounts.put(companyAccount.getAccountNumber(), companyAccount);
        }
    }

    // ===== Menü =====
    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createAccount();
                case "2" -> deleteAccount();
                case "3" -> listAccounts();
                case "4" -> payBooking();
                case "5" -> doUndo();
                case "6" -> doRedo();
                case "0" -> {
                    System.out.println("Zurück zum Hauptmenü.");
                    running = false;
                }
                default -> System.out.println("Ungültige Eingabe.");
            }
        }
    }

<<<<<<< HEAD
    private void printMenu() {
        System.out.println("\n--- Payment Menu ---");
        System.out.println("1) Daten eingeben (Account anlegen)");
        System.out.println("2) Daten löschen (Account entfernen)");
        System.out.println("3) Daten ausgeben (Accounts anzeigen)");
        System.out.println("4) Payment ausführen (per Booking-ID)");
        System.out.println("5) Undo");
        System.out.println("6) Redo");
        System.out.println("0) Zurück");
        System.out.print("Auswahl: ");
    }
=======
				this.sender = (bookingToPay.getPerson() != null) ? bookingToPay.getPerson().getAccount() : null;
				if (this.sender == null) {
					System.out.println("Payment failed: Sender account not found for '" + bookingToPay.getPerson().getName() + "'.");
					return;
				}

				this.amount = new CurrencyAmount(bookingToPay.getPrice(), "EUR");
>>>>>>> branch 'main' of https://github.com/ariansch/CarReservation.git

    // ===== Command-Infrastruktur =====
    private interface Command {
        void execute();
        void undo();
    }

    private class CreateAccountCommand implements Command {
        private final Account account;
        CreateAccountCommand(Account acc) { this.account = acc; }
        @Override public void execute() { accounts.put(account.getAccountNumber(), account); }
        @Override public void undo() { accounts.remove(account.getAccountNumber()); }
    }

    private class DeleteAccountCommand implements Command {
        private final String accountNo;
        private Account removed;
        DeleteAccountCommand(String accountNo) { this.accountNo = accountNo; }
        @Override public void execute() { removed = accounts.remove(accountNo); }
        @Override public void undo() { if (removed != null) accounts.put(accountNo, removed); }
    }

<<<<<<< HEAD
    private void doCmd(Command c) {
        c.execute();
        undo.push(c);
        redo.clear();
    }
=======
	/**
	 * Command: Create or top up an account for a person (undoable).
	 * - If no account exists: create with given id and initial balance.
	 * - If account exists: credit the given amount.
	 * Undo:
	 * - If created: remove account (set to null).
	 * - If topped up: debit the credited amount.
	 */
	private class CreateOrTopUpAccountCommand implements Command {
		private final String personName;
		private final String accountId;
		private final double amount;
		private boolean createdNew = false;

		public CreateOrTopUpAccountCommand(String personName, String accountId, double amount) {
			this.personName = personName;
			this.accountId = accountId;
			this.amount = amount;
		}

		@Override
		public void execute() {
			Person p = personService.findPersonByName(personName);
			if (p == null) {
				System.out.println("Person not found: " + personName);
				return;
			}
			Account acc = p.getAccount();
			if (acc == null) {
				acc = new Account(accountId, p, amount);
				p.setAccount(acc);
				createdNew = true;
			} else {
				if (amount > 0) acc.credit(amount);
			}
		}

		@Override
		public void undo() {
			Person p = personService.findPersonByName(personName);
			if (p == null) return;
			Account acc = p.getAccount();
			if (createdNew) {
				// remove created account
				p.setAccount(null);
			} else if (acc != null && amount > 0) {
				// revert top-up
				acc.debit(amount);
			}
		}
	}

	/**
	 * Manages a history of executed commands to support undo and redo.
	 */
	private static class History {
		private final Stack<Command> undoStack = new Stack<>();
		private final Stack<Command> redoStack = new Stack<>();
>>>>>>> branch 'main' of https://github.com/ariansch/CarReservation.git

    private void doUndo() {
        if (undo.isEmpty()) {
            System.out.println("Nichts zum Rückgängig machen.");
            return;
        }
        Command c = undo.pop();
        c.undo();
        redo.push(c);
        System.out.println("Undo ausgeführt.");
    }

    private void doRedo() {
        if (redo.isEmpty()) {
            System.out.println("Nichts zum Wiederholen.");
            return;
        }
        Command c = redo.pop();
        c.execute();
        undo.push(c);
        System.out.println("Redo ausgeführt.");
    }

    // ===== Menü-Aktionen =====
    private void createAccount() {
        System.out.print("Neue Account-Nr: ");
        String no = scanner.nextLine().trim();
        if (no.isEmpty() || accounts.containsKey(no)) {
            System.out.println("Abgebrochen (leer oder existiert).");
            return;
        }

        System.out.print("Owner-Typ [N=Natural, L=Legal]: ");
        String t = scanner.nextLine().trim().toUpperCase();
        System.out.print("Owner-Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Abgebrochen (leerer Name).");
            return;
        }

        Person owner = "L".equals(t) ? new LegalPerson(name) : new NaturalPerson(name);

<<<<<<< HEAD
        System.out.print("Startguthaben: ");
        double bal;
        try {
            bal = Double.parseDouble(scanner.nextLine().trim());
            if (bal < 0) {
                System.out.println("Negatives Guthaben nicht erlaubt.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ungültiger Betrag.");
            return;
        }
=======
	private void printMenu() {
		System.out.println("\n--- Payment Paket-Menü ---");
		System.out.println("1. Daten eingeben (Make a Payment)");
		System.out.println("2. Daten löschen");
		System.out.println("3. Daten ausgeben (List Accounts)");
		System.out.println("4. Undo");
		System.out.println("5. Redo");
		System.out.println("6. Exit");
		// zusätzliche Option, aber bestehende Strings 1–6 bleiben unverändert
		System.out.println("7. Create or Top up Account");
		System.out.print("Ihre Wahl: ");
	}
>>>>>>> branch 'main' of https://github.com/ariansch/CarReservation.git

<<<<<<< HEAD
        Account acc = new Account(no, owner, bal);
        doCmd(new CreateAccountCommand(acc));
        System.out.println("Account angelegt: " + acc);
    }
=======
	private void handleMenuChoice(int choice) {
		switch (choice) {
		case 1:
			createPayment();
			break;
		case 2:
			System.out.println("Payments cannot be deleted directly. Please use 'Undo' to revert a payment.");
			break;
		case 3:
			listAccounts();
			break;
		case 4:
			history.undo();
			break;
		case 5:
			history.redo();
			break;
		case 7:
			createOrTopUpAccount();
			break;
		default:
			System.out.println("Invalid input. Please try again.");
			break;
		}
	}
>>>>>>> branch 'main' of https://github.com/ariansch/CarReservation.git

    private void deleteAccount() {
        System.out.print("Account-Nr löschen: ");
        String no = scanner.nextLine().trim();
        if (!accounts.containsKey(no)) {
            System.out.println("Nicht gefunden.");
            return;
        }
        doCmd(new DeleteAccountCommand(no));
        System.out.println("Account gelöscht: " + no);
    }

    private void listAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("(keine Accounts)");
            return;
        }
        accounts.values().forEach(a -> System.out.println(" - " + a));
    }

    private void payBooking() {
        System.out.print("Booking-ID: ");
        String bookingId = scanner.nextLine().trim();
        PaymentType type = askPaymentType();
        boolean ok = paymentService.payBookingById(bookingId, type);
        System.out.println(ok ? "Zahlung erfolgreich." : "Zahlung fehlgeschlagen.");
    }

    private PaymentType askPaymentType() {
        System.out.println("Zahlmethode:");
        System.out.println(" 1) PAYPAL");
        System.out.println(" 2) GOOGLE_WALLET");
        System.out.println(" 3) MOBILE_MONEY_WALLET");
        System.out.print("Auswahl: ");
        String c = scanner.nextLine().trim();
        return switch (c) {
            case "1" -> PaymentType.PAYPAL;
            case "2" -> PaymentType.GOOGLE_WALLET;
            case "3" -> PaymentType.MOBILE_MONEY_WALLET;
            default -> {
                System.out.println("Ungültig – nehme PAYPAL.");
                yield PaymentType.PAYPAL;
            }
        };
    }

<<<<<<< HEAD
    public static void main(String[] args) {
        BookingService bookingService = new BookingService(new PersonService(), new ResourceService());
        PaymentService paymentService = new PaymentService(bookingService);
        Account companyAcc = paymentService.getCompanyAccount(); // Konto direkt aus dem Service
        new PaymentClient(paymentService, companyAcc).start();
    }
=======
	private void createOrTopUpAccount() {
		System.out.println("\n--- Create or Top Up Account ---");
		System.out.print("Enter person name: ");
		String personName = scanner.nextLine().trim();

		Person p = personService.findPersonByName(personName);
		if (p == null) {
			System.out.println("Person not found: " + personName);
			return;
		}

		if (p.getAccount() == null) {
			System.out.print("Enter new account id: ");
			String accId = scanner.nextLine().trim();
			System.out.print("Enter initial balance (>=0): ");
			double amount = Double.parseDouble(scanner.nextLine());
			history.execute(new CreateOrTopUpAccountCommand(personName, accId, amount));
		} else {
			System.out.print("Enter top-up amount (>=0): ");
			double amount = Double.parseDouble(scanner.nextLine());
			history.execute(new CreateOrTopUpAccountCommand(personName, p.getAccount().getAccountNumber(), amount));
		}
	}

	private void listAccounts() {
		System.out.println("\n--- Account Balances ---");
		List<Person> persons = personService.getAllPersons();
		if (persons.isEmpty()) {
			System.out.println("No persons available to display accounts for.");
			return;
		}
		for (Person p : persons) {
			if (p.getAccount() != null) {
				System.out.println("- " + p.getAccount().toString());
			} else {
				System.out.println("- " + p.getName() + " (No account created yet)");
			}
		}
	}

	public static void main(String[] args) {
		// Setup for standalone testing
		PersonService personService = new PersonService();
		ResourceService resourceService = new ResourceService();
		BookingService bookingService = new BookingService(personService, resourceService);

		// Create dummy data
		personService.createPerson("natural", "Ari");
		personService.findPersonByName("Ari")
				.setAccount(new Account("A001", personService.findPersonByName("Ari"), 2000));
		resourceService.addResource(new resource.structure.Car("Tesla Model 3", 45000));
		bookingService.createBooking("DE", "B01", "Ari", "Tesla Model 3", 150.50);

		new PaymentClient(personService, bookingService).start();
	}

}
