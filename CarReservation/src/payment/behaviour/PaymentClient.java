package payment.behaviour;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import booking.behaviour.BookingService;
import booking.structure.Booking;
import payment.structure.Account;
import payment.structure.CurrencyAmount;
import payment.structure.GoogleWalletProcessor;
import payment.structure.MobileMoneyWalletProcessor;
import payment.structure.PaymentProcessor;
import payment.structure.PaymentType;
import payment.structure.PaypalProcessor;
import person.behaviour.PersonService;
import person.structure.Person;
import resource.behaviour.ResourceService;

/**
 * A standalone console client to demonstrate the functionality of the 'Payment'
 * package, following the established project style.
 */
public class PaymentClient {

	private final PersonService personService;
	private final BookingService bookingService;
	private final PaymentService paymentService;
	private final History history;
	private final Scanner scanner;

	/**
	 * Defines the common interface for all command objects.
	 */
	private interface Command {
		void execute();

		void undo();
	}

	/**
	 * A concrete command for executing a payment, allowing it to be undone.
	 */
	private class PayCommand implements Command {
		private final PaymentProcessor processor;
		private final String bookingId;
		private Account sender;
		private Account receiver;
		private CurrencyAmount amount;

		public PayCommand(PaymentProcessor processor, String bookingId) {
			this.processor = processor;
			this.bookingId = bookingId;
		}

		@Override
		public void execute() {
			try {
				Booking bookingToPay = bookingService.getBookingById(bookingId);
				if (bookingToPay == null) {
					System.out.println("Payment failed: Booking with ID '" + bookingId + "' not found.");
					return;
				}

				this.sender = bookingToPay.getPerson().getAccount();
				this.amount = new CurrencyAmount(bookingToPay.getPrice(), "EUR");

				// Create a dummy receiver account for the simulation
				this.receiver = new Account("COMPANY_ACCOUNT_01",
						new person.structure.LegalPerson("Car Reservation Inc."), 100000);

				processor.processPayment(sender, receiver, amount);
			} catch (Exception e) {
				System.out.println("Payment failed during execution: " + e.getMessage());
			}
		}

		@Override
		public void undo() {
			if (sender != null && receiver != null && amount != null) {
				System.out.println("\n--- Undoing Payment for Booking ID: " + bookingId + " ---");
				// Reverse the transaction
				receiver.debit(amount.getAmount());
				sender.credit(amount.getAmount());
				System.out.println("=> Payment successfully reverted.");
				System.out.println("--- Undo Finished ---");
			}
		}
	}

	/**
	 * Manages a history of executed commands to support undo and redo.
	 */
	private static class History {
		private final Stack<Command> undoStack = new Stack<>();
		private final Stack<Command> redoStack = new Stack<>();

		public void execute(Command cmd) {
			cmd.execute();
			undoStack.push(cmd);
			redoStack.clear();
		}

		public void undo() {
			if (!undoStack.isEmpty()) {
				Command cmd = undoStack.pop();
				cmd.undo();
				redoStack.push(cmd);
			} else {
				System.out.println("Nothing to undo.");
			}
		}

		public void redo() {
			if (!redoStack.isEmpty()) {
				Command cmd = redoStack.pop();
				cmd.execute();
				undoStack.push(cmd);
			} else {
				System.out.println("Nothing to redo.");
			}
		}
	}

	public PaymentClient(PersonService personService, BookingService bookingService) {
		this.personService = personService;
		this.bookingService = bookingService;
		this.paymentService = new PaymentService(personService, bookingService);
		this.history = new History();
		this.scanner = new Scanner(System.in);
	}

	public void start() {
		while (true) {
			printMenu();
			try {
				int choice = Integer.parseInt(scanner.nextLine());
				if (choice == 6) {
					System.out.println("Exiting Payment Menu...");
					break;
				}
				handleMenuChoice(choice);
			} catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
			}
		}
	}

	private void printMenu() {
		System.out.println("\n--- Payment Paket-Menü ---");
		System.out.println("1. Daten eingeben (Make a Payment)");
		System.out.println("2. Daten löschen");
		System.out.println("3. Daten ausgeben (List Accounts)");
		System.out.println("4. Undo");
		System.out.println("5. Redo");
		System.out.println("6. Exit");
		System.out.print("Ihre Wahl: ");
	}

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
		default:
			System.out.println("Invalid input. Please try again.");
			break;
		}
	}

	private void createPayment() {
		System.out.println("\n--- Make a Payment ---");
		bookingService.listBookings();
		System.out.print("Enter the Booking ID to pay for: ");
		String bookingId = scanner.nextLine();

		System.out.println("Select a payment method: 1=PayPal, 2=Google Wallet, 3=Mobile Money");
		System.out.print("Your choice: ");
		int processorChoice = Integer.parseInt(scanner.nextLine());

		PaymentProcessor processor;
		switch (processorChoice) {
		case 1:
			processor = new PaypalProcessor();
			break;
		case 2:
			processor = new GoogleWalletProcessor();
			break;
		case 3:
			processor = new MobileMoneyWalletProcessor();
			break;
		default:
			System.out.println("Invalid payment method.");
			return;
		}

		history.execute(new PayCommand(processor, bookingId));
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