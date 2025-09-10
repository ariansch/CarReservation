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
			System.out.println("1. Personen verwalten");
			System.out.println("2. Ressourcen verwalten");
			System.out.println("3. Authentifizierungen verwalten");
			System.out.println("4. Buchungen verwalten");
			System.out.println("5. Beenden");
			
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
					System.out.println("Auf Wiedersehen!");
					return;
				default:
					System.out.println("Ungültige Auswahl. Bitte erneut versuchen.");
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