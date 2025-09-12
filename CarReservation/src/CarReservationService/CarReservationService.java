package CarReservationService;

import java.util.Scanner;

import authentication.behaviour.AuthenticationClient;
import authentication.behaviour.AuthenticationService;
import booking.behaviour.BookingClient;
import booking.behaviour.BookingService;
import content.behaviour.ContentClient;
import content.behaviour.ContentService;
import payment.behaviour.PaymentClient;
import payment.behaviour.PaymentService;
import person.behaviour.PersonClient;
import person.behaviour.PersonService;
import resource.behaviour.ResourceClient;
import resource.behaviour.ResourceService;
import statistics.behaviour.StatisticsClient;
import statistics.behaviour.StatisticsService;

public class CarReservationService {

	private final PersonService personService = new PersonService();
	private final ResourceService resourceService = new ResourceService();
	private final ContentService contentService = new ContentService();
	private final StatisticsService statisticsService = new StatisticsService();
	private final AuthenticationService authService = new AuthenticationService(personService);
	private final BookingService bookingService = new BookingService(personService, resourceService);
	private final PaymentService paymentService = new PaymentService(personService, bookingService);

	private final Scanner scanner = new Scanner(System.in);

	public void start() {
		while (true) {
			System.out.println("\n=== Car Reservation Service ===");
			System.out.println("1. Manage Persons");
			System.out.println("2. Manage Resources");
			System.out.println("3. Manage Authentication");
			System.out.println("4. Manage Bookings");
			System.out.println("5. Manage Payments");;
			System.out.println("6. Manage Content");
			System.out.println("7. Manage Statistics");
			System.out.println("8. Exit");
			System.out.print("Your choice: ");

			int choice = readInt();
			try {
				switch (choice) {
				case 1:
					new PersonClient(personService).start();
					break;
				case 2:
					new ResourceClient(resourceService).start();
					break;
				case 3:
					new AuthenticationClient(personService).start();
					break;
				case 4:
					new BookingClient(bookingService).start();
					break;
				case 5:
					new PaymentClient(personService, bookingService).start();
					break;
				case 6:
					new ContentClient(contentService).start();
					break;
				case 7:
					new StatisticsClient(bookingService, statisticsService).start();
				case 8:
					System.out.println("Goodbye!");
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
				}
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}

	private int readInt() {
		int v = scanner.nextInt();
		scanner.nextLine();
		return v;
	}

	public static void main(String[] args) {
		new CarReservationService().start();
	}

}