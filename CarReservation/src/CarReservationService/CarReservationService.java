package CarReservationService;

import java.util.Scanner;

import authentication.behaviour.AuthenticationClient;
import authentication.behaviour.AuthenticationService;
import booking.behaviour.BookingClient;
import booking.behaviour.BookingService;
import person.behaviour.PersonClient;
import person.behaviour.PersonService;
import resource.behaviour.ResourceClient;
import resource.behaviour.ResourceService;

public class CarReservationService {
	
	private final PersonService personService = new PersonService();
	private final ResourceService resourceService = new ResourceService();
	private final AuthenticationService authService = new AuthenticationService(personService);
	private final BookingService bookingService = new BookingService(personService, resourceService);
	
	private final Scanner scanner = new Scanner(System.in);
	
	public void start() {
		while (true) {
			System.out.println("\n=== Car Reservation Service ===");
			System.out.println("1. Manage Persons");
			System.out.println("2. Manage Resources");
			System.out.println("3. Manage Authentication");
			System.out.println("4. Manage Bookings");
			System.out.println("5. Exit");
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
					new BookingClient(personService, resourceService).start();
					break;
				case 5:
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