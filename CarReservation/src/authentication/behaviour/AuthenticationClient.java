package authentication.behaviour;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import authentication.structure.*;
import person.behaviour.PersonService;
import person.structure.Person; 

public class AuthenticationClient {

	private final PersonService personService;
	private final AuthenticationService authService;

	private final History history;
	private final Scanner scanner;

	private interface Command {
		void execute();

		void undo();
	}

	// Erstellt oder aktualisiert die Credentials einer Person.
	private class CreateAuthenticationDataCommand implements Command {
		private Person personToRegister;
		private Credential oldCredential; // Für Undo

		@Override
		public void execute() {
			System.out.print("Enter name of existing person to register credentials for: ");
			String name = scanner.nextLine();

			try {
				this.personToRegister = personService.findPersonByName(name);
			} catch (IllegalArgumentException e) {
				System.out.println("Error: " + e.getMessage());
				this.personToRegister = null;
				return;
			}

			this.oldCredential = personToRegister.getCredential();

			System.out.println("Select credential type: 1=Password, 2=FingerPrint, 3=EyeScan");
			System.out.print("Your choice: ");
			int typeChoice = Integer.parseInt(scanner.nextLine());

			Credential newCredential;
			switch (typeChoice) {
			case 1:
				System.out.print("Set a password for '" + name + "': ");
				String password = scanner.nextLine();
				newCredential = new Credential(CredentialType.PASSWORD, password);
				break;
			case 2:
				System.out.println("A simulated, valid FingerPrint is being registered for '" + name + "'.");
				newCredential = new Credential(CredentialType.FINGERPRINT, "valid_fingerprint");
				break;
			case 3:
				System.out.println("A simulated, valid EyeScan is being registered for '" + name + "'.");
				newCredential = new Credential(CredentialType.EYESCAN, "valid_eye_scan");
				break;
			default:
				System.out.println("Invalid credential type. Aborting.");
				this.personToRegister = null;
				return;
			}

			personToRegister.setCredential(newCredential);
			System.out.println("Credentials for person '" + name + "' have been registered/updated.");
		}

		@Override
		public void undo() {
			if (this.personToRegister != null) {
				this.personToRegister.setCredential(this.oldCredential);
				System.out.println(
						"Undo: Credential change for '" + this.personToRegister.getName() + "' has been reverted.");
			}
		}
	}

	// Entfernt die Credentials einer Person.
	private class DeleteAuthenticationDataCommand implements Command {
		private String personName;
		private Credential deletedCredential; // Für Undo

		@Override
		public void execute() {
			System.out.print("Enter name of person to delete credentials for: ");
			this.personName = scanner.nextLine();
			Person p;
			try {
				p = personService.findPersonByName(personName);
			} catch (IllegalArgumentException e) {
				System.out.println("Error: " + e.getMessage());
				this.deletedCredential = null;
				return;
			}

			if (p.getCredential() == null) {
				System.out.println("No credentials found for person '" + personName + "'.");
				this.deletedCredential = null;
				return;
			}
			this.deletedCredential = p.getCredential();
			p.setCredential(null);
			System.out.println("Credentials for '" + personName + "' deleted.");
		}

		@Override
		public void undo() {
			if (this.deletedCredential != null) {
				Person p = personService.findPersonByName(personName);
				if (p != null) {
					p.setCredential(this.deletedCredential);
					System.out.println("Undo: Deletion of credentials for '" + personName + "' has been reverted.");
				}
			}
		}
	}
	// Verwaltet die History von Commands für Undo/Redo
	private static class History {
		private final Stack<Command> undoStack = new Stack<>();
		private final Stack<Command> redoStack = new Stack<>();

		public void execute(Command cmd) {
			cmd.execute();
			// Nur erfolgreich ausgeführte Commands zur History hinzufügen
			if (cmd instanceof CreateAuthenticationDataCommand
					&& ((CreateAuthenticationDataCommand) cmd).personToRegister == null)
				return;
			if (cmd instanceof DeleteAuthenticationDataCommand
					&& ((DeleteAuthenticationDataCommand) cmd).deletedCredential == null)
				return;

			undoStack.push(cmd);
			redoStack.clear();
		}

		public void undo() {
			if (canUndo()) {
				Command cmd = undoStack.pop();
				cmd.undo();
				redoStack.push(cmd);
			} else {
				System.out.println("Nothing to undo.");
			}
		}

		public void redo() {
			if (canRedo()) {
				Command cmd = redoStack.pop();
				cmd.execute();
				undoStack.push(cmd);
			} else {
				System.out.println("Nothing to redo.");
			}
		}

		public boolean canUndo() {
			return !undoStack.isEmpty();
		}

		public boolean canRedo() {
			return !redoStack.isEmpty();
		}
	}

	public AuthenticationClient(PersonService personService) {
		this.personService = personService;
		this.authService = new AuthenticationService(personService);
		this.history = new History();
		this.scanner = new Scanner(System.in);
	}

	public void start() {
		while (true) {
			printMenu();
			try {
				int choice = Integer.parseInt(scanner.nextLine());
				if (choice == 6) {
					System.out.println("Exiting Authentication Menu...");
					break;
				}
				handleMenuChoice(choice);
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
			} catch (Exception e) {
				System.out.println("An unexpected error occurred: " + e.getMessage());
			}
		}
	}

	private void printMenu() {
		System.out.println("\n--- Authentication Menu ---");
		System.out.println("1. Register/Update Credentials for Person");
		System.out.println("2. Delete Credentials from Person");
		System.out.println("3. List Persons & Authenticate");
		System.out.println("4. Undo");
		System.out.println("5. Redo");
		System.out.println("6. Back to Main Menu");
		System.out.print("Your choice: ");
	}

	private void handleMenuChoice(int choice) {
		switch (choice) {
		case 1:
			history.execute(new CreateAuthenticationDataCommand());
			break;
		case 2:
			history.execute(new DeleteAuthenticationDataCommand());
			break;
		case 3:
			listAndAuthenticateSubjects();
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

	// Listet alle Personen auf und ermöglicht die Authentifizierung einer ausgewählten Person.
	private void listAndAuthenticateSubjects() {
		List<Person> persons = personService.getAllPersons();

		if (persons.isEmpty()) {
			System.out.println("No persons available. Please manage persons in the person menu first.");
			return;
		}

		System.out.println("\n--- Registered Persons (Subjects) ---");
		for (Person p : persons) {
			String credentialInfo = (p.getCredential() != null)
					? " (Registered Credential: " + p.getCredential().getType().name() + ")"
					: " (No credentials registered)";
			System.out.println("- " + p.getName() + credentialInfo);
		}

		System.out.print("\nDo you want to perform an authentication? (y/n): ");
		if (!"y".equalsIgnoreCase(scanner.nextLine())) {
			return;
		}

		System.out.print("Enter the name of the person to authenticate: ");
		String name = scanner.nextLine();
		Subject subjectToAuth;
		try {
			subjectToAuth = personService.findPersonByName(name);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}

		System.out.println("Select a strategy: 1=Password, 2=FingerPrint, 3=EyeScan");
		System.out.print("Your choice: ");
		int stratChoice = Integer.parseInt(scanner.nextLine());

		AuthenticationStrategy strategy;
		Credential providedCredential;

		switch (stratChoice) {
		case 1:
			strategy = new UserNamePasswordStrategy();
			System.out.print("Enter the password for the login attempt: ");
			String password = scanner.nextLine();
			providedCredential = new Credential(CredentialType.PASSWORD, password);
			break;
		case 2:
			strategy = new FingerPrintStrategy();
			System.out.print("Simulate a valid FingerPrint? (y/n): ");
			String fingerprintInput = scanner.nextLine();
			String fingerprintData = "y".equalsIgnoreCase(fingerprintInput) ? "valid_fingerprint"
					: "invalid_fingerprint";
			providedCredential = new Credential(CredentialType.FINGERPRINT, fingerprintData);
			break;
		case 3:
			strategy = new EyeScanStrategy();
			System.out.print("Simulate a valid EyeScan? (y/n): ");
			String eyeScanInput = scanner.nextLine();
			String eyeScanData = "y".equalsIgnoreCase(eyeScanInput) ? "valid_eye_scan" : "invalid_eye_scan";
			providedCredential = new Credential(CredentialType.EYESCAN, eyeScanData);
			break;
		default:
			System.out.println("Invalid strategy choice.");
			return;
		}
		authService.authenticateSubject(strategy, subjectToAuth, providedCredential);
	}

	public static void main(String[] args) {
		PersonService personService = new PersonService();
		new AuthenticationClient(personService).start();
	}
}