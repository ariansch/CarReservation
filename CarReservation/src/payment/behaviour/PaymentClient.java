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

	private final Map<String, Account> accounts = new LinkedHashMap<>();
	private final Deque<Command> undo = new ArrayDeque<>();
	private final Deque<Command> redo = new ArrayDeque<>();

	private final PaymentService paymentService;
	private final PersonService personService;

	public PaymentClient(PaymentService paymentService, Account companyAccount, PersonService personService) {
		this.paymentService = paymentService;
		this.personService = personService; // << store it
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
				System.out.println("Back to Main Menu...");
				running = false;
			}
			default -> System.out.println("Invaild input.");
			}
		}
	}

	private void printMenu() {
		System.out.println("\n--- Payment Menu ---");
		System.out.println("1) Create Account");
		System.out.println("2) Delete Account");
		System.out.println("3) List Accounts");
		System.out.println("4) Execute payment (by Booking ID)");
		System.out.println("5) Undo");
		System.out.println("6) Redo");
		System.out.println("0) Back to Main Menu");
		System.out.print("Your choice: ");
	}

	// ===== Command-Infrastruktur =====
	private interface Command {
		void execute();

		void undo();
	}

	private class CreateAccountCommand implements Command {
		private final Account account;

		CreateAccountCommand(Account acc) {
			this.account = acc;
		}

		@Override
		public void execute() {
			accounts.put(account.getAccountNumber(), account);
			if (account.getOwner().getAccount() != account) {
				account.getOwner().setAccount(account);
			}

		}

		@Override
		public void undo() {
			accounts.remove(account.getAccountNumber());
			if (account.getOwner().getAccount() == account) {
				account.getOwner().setAccount(null);
			}
		}
	}

	private class DeleteAccountCommand implements Command {
		private final String accountNo;
		private Account removed;

		DeleteAccountCommand(String accountNo) {
			this.accountNo = accountNo;
		}

		@Override
		public void execute() {
			removed = accounts.remove(accountNo);
			if (removed != null && removed.getOwner().getAccount() == removed) {
				removed.getOwner().setAccount(null);
			}
		}

		@Override
		public void undo() {
			if (removed != null)
				accounts.put(accountNo, removed);
			removed.getOwner().setAccount(removed);
		}
	}

	private void doCmd(Command c) {
		c.execute();
		undo.push(c);
		redo.clear();
	}

	private void doUndo() {
		if (undo.isEmpty()) {
			System.out.println("Nothing to undo.");
			return;
		}
		Command c = undo.pop();
		c.undo();
		redo.push(c);
		System.out.println("Undo performed.");
	}

	private void doRedo() {
		if (redo.isEmpty()) {
			System.out.println("Nothing to redo.");
			return;
		}
		Command c = redo.pop();
		c.execute();
		undo.push(c);
		System.out.println("Redo performed.");
	}

	// ===== Menü-Aktionen =====
	private void createAccount() {
		System.out.print("New account number: ");
		String no = scanner.nextLine().trim();
		if (no.isEmpty() || accounts.containsKey(no)) {
			System.out.println("Aborted (empty or already existing account number).");
			return;
		}

		// Kein Owner-Typ mehr abfragen – wir verwenden die bereits existierende Person
		System.out.print("Owner name: ");
		String name = scanner.nextLine().trim();
		if (name.isEmpty()) {
			System.out.println("Aborted (empty name).");
			return;
		}

		// >>> Bestehende Person aus dem PersonService holen
		person.structure.Person owner;
		try {
			owner = personService.findPersonByName(name);
		} catch (IllegalArgumentException ex) {
			System.out.println("Person '" + name + "' does not exist. Please create it in the Person menu first.");
			return;
		}

		System.out.print("Initial balance: ");
		double bal;
		try {
			bal = Double.parseDouble(scanner.nextLine().trim());
			if (bal < 0) {
				System.out.println("Negative balance is not allowed.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid amount.");
			return;
		}

		Account acc = new Account(no, owner, bal);

		// >>> Sehr wichtig: Account an genau diese Person hängen
		owner.setAccount(acc);

		// In die lokale Map + History (Command setzt/entfernt den Link zusätzlich
		// defensiv)
		doCmd(new CreateAccountCommand(acc));
		System.out.println("Account created: " + acc);
	}

	private void deleteAccount() {
		System.out.print("Account number to delete: ");
		String no = scanner.nextLine().trim();
		if (!accounts.containsKey(no)) {
			System.out.println("Account not found.");
			return;
		}
		doCmd(new DeleteAccountCommand(no));
		System.out.println("Account deleted: " + no);
	}

	private void listAccounts() {
		if (accounts.isEmpty()) {
			System.out.println("(no accounts)");
			return;
		}
		accounts.values().forEach(a -> System.out.println(" - " + a));
	}

	private void payBooking() {
		System.out.print("Booking-ID: ");
		String bookingId = scanner.nextLine().trim();
		PaymentType type = askPaymentType();
		boolean ok = paymentService.payBookingById(bookingId, type);
		System.out.println(ok ? "Payment successful." : "Payment failed.");
	}

	private PaymentType askPaymentType() {
		System.out.println("Payment method:");
		System.out.println(" 1) PAYPAL");
		System.out.println(" 2) GOOGLE_WALLET");
		System.out.println(" 3) MOBILE_MONEY_WALLET");
		System.out.print("Your choice: ");
		String c = scanner.nextLine().trim();
		return switch (c) {
		case "1" -> PaymentType.PAYPAL;
		case "2" -> PaymentType.GOOGLE_WALLET;
		case "3" -> PaymentType.MOBILE_MONEY_WALLET;
		default -> {
			System.out.println("Invalid choice – defaulting to PAYPAL.");
			yield PaymentType.PAYPAL;
		}
		};
	}

	public static void main(String[] args) {
		BookingService bookingService = new BookingService(new PersonService(), new ResourceService());
		PaymentService paymentService = new PaymentService(bookingService);
		PersonService personService = new PersonService();
		Account companyAcc = paymentService.getCompanyAccount(); // Konto direkt aus dem Service
		new PaymentClient(paymentService, companyAcc, personService).start();
	}
}
