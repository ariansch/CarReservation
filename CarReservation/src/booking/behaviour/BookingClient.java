package booking.behaviour;

import java.util.Scanner;

import booking.structure.Booking;
import person.behaviour.PersonService;
import resource.behaviour.ResourceService;

public class BookingClient {

	private interface Command {
		void execute();

		void undo();
	}

	private static final class CreateCommand implements Command {
		private final BookingService bookingService;
		private final String language;
		private final String bookingId;
		private final String personName;
		private final String resourceName;

		public CreateCommand(BookingService bookingService, String language, String bookingId, String personName,
				String resourceName) {
			this.bookingService = bookingService;
			this.language = language;
			this.bookingId = bookingId;
			this.personName = personName;
			this.resourceName = resourceName;
		}

		public void execute() {
			bookingService.createBooking(language, bookingId, personName, resourceName);
		}

		public void undo() {
			bookingService.deleteBooking(bookingId);
		}
	}

	private static final class DeleteCommand implements Command {
		private final BookingService bookingService;
		private final String bookingId;
		// Snapshot für Undo
		private String snapLanguage;
		private String snapPersonName;
		private String snapResourceName;
		
		public DeleteCommand(BookingService bookingService, String bookingId) {
			this.bookingService = bookingService;
			this.bookingId = bookingId;
		}

		public void execute() {
			Booking b = bookingService.getBookingById(bookingId);
			if (b == null)
				throw new IllegalArgumentException("Booking with id " + bookingId + " not found");
			this.snapLanguage = b.header().startsWith("Booking") ? "EN" : "DE";
			this.snapPersonName = b.getPerson().getName();
			this.snapResourceName = b.getResource().getName();
			
			bookingService.deleteBooking(bookingId);
		}

		public void undo() {
			bookingService.createBooking(snapLanguage, bookingId, snapPersonName, snapResourceName);
		}
	}

	private static final class History {
		private final Command[] undo = new Command[10];
		private final Command[] redo = new Command[10];
		private int uTop = 0, rTop = 0;

		public void execute(Command cmd) {
			cmd.execute();
			undo[uTop++] = cmd;
			rTop = 0; // redo stack löschen
		}

		boolean canUndo() {
			return uTop > 0;
		}

		boolean canRedo() {
			return rTop > 0;
		}

		public void undo() {
			if (uTop > 0) {
				Command cmd = undo[--uTop];
				cmd.undo();
				redo[rTop++] = cmd;
			}
		}

		public void redo() {
			if (rTop > 0) {
				Command cmd = redo[--rTop];
				cmd.execute();
				undo[uTop++] = cmd;
			}
		}
	}

	// Booking Client
	private final BookingService bookingSrvce;
	private final PersonService personService;
	private final ResourceService resourceService;
	private final History history = new History();
	private final Scanner scanner = new Scanner(System.in);

	public BookingClient(PersonService personService, ResourceService resourceService) {
		this.personService = personService;
		this.resourceService = resourceService;
		this.bookingSrvce = new BookingService(personService, resourceService);
		
	
	}
	
	public BookingClient(BookingService bookingService) { 
		this.personService = null;
		this.resourceService = null;
		this.bookingSrvce = bookingService;
	}

	public void start() {
		while (true) {
			System.out.println("\n--- Booking Menu ---");
			System.out.println("1. Create a Booking");
			System.out.println("2. Delete a Booking");
			System.out.println("3. Show Bookings");
			System.out.println("4. Undo");
			System.out.println("5. Redo");
			System.out.println("6. Back to Main Menu");
			System.out.print("Your choice: ");

			int choice = scanner.nextInt();
			scanner.nextLine();

			try {
				switch (choice) {
				case 1:
					createBooking();
					break;
				case 2:
					deleteBooking();
					break;
				case 3:
					bookingSrvce.listBookings();
					break;
				case 4:
					if (history.canUndo())
						history.undo();
					else
						System.out.println("Nothing to undo.");
					break;
				case 5:
					if (history.canRedo())
						history.redo();
					else
						System.out.println("Nothing to redo.");
					break;
				case 6:
					System.out.println("Exiting...");
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
				}
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}

	private void createBooking() {
		System.out.println("Enter language (german/english): ");
		String language = scanner.nextLine();
		
		// Map auf "EN" oder "DE"
		String langCode = language.toLowerCase().startsWith("e") ? "EN" : "DE";

		System.out.println("Enter booking ID: ");
		String bookingId = scanner.nextLine();

		System.out.println("Enter person name: ");
		String personName = scanner.nextLine();

		System.out.println("Enter car name: ");
		String resourceName = scanner.nextLine();

		history.execute(new CreateCommand(bookingSrvce, langCode, bookingId, personName, resourceName));
	}

	private void deleteBooking() {
		System.out.println("Enter booking ID to delete: ");
		String bookingId = scanner.nextLine();
		history.execute(new DeleteCommand(bookingSrvce, bookingId));
	}

	private void listBookings() {
		bookingSrvce.listBookings();
	}

	public static void main(String[] args) {
		PersonService personService = new PersonService();
		ResourceService resourceService = new ResourceService();
		new BookingClient(personService, resourceService).start();
	}

}
