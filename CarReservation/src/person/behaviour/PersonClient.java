package person.behaviour;

import java.util.Scanner;

public class PersonClient {
	
	private interface Command {
		void execute();
		void undo();
	}
	
	private static final class CreateCommand implements Command {
		private final PersonService svc;
		private final String type; // "natural" oder "legal"
		private final String name;
		
		public CreateCommand(PersonService svc, String type, String name) {
			this.svc = svc;
			this.type = type;
			this.name = name;
		}
		
		public void execute() {
			svc.createPerson(type, name);
		}
		
		public void undo() {
			svc.deletePerson(name);
		}
	}
	
	private static final class DeleteCommand implements Command {
		private final PersonService svc;
		private final String name;
		// Snapshot für Undo
		private String snapType; // "natural" oder "legal"
		private String snapName;
		
		public DeleteCommand(PersonService svc, String name) {
			this.svc = svc;
			this.name = name;
		}
		
		public void execute() {
			person.structure.Person p = svc.findPersonByName(name);
			String cls = p.getClass().getSimpleName();
			this.snapType = "LegalPerson".equals(cls) ? "legal" : "natural";
			this.snapName = p.getName();
			
			svc.deletePerson(name);
		}
		
		public void undo() {
			svc.createPerson(snapType, snapName);
		}
	}
	
	private static final class History {
		private final Command[] undo = new Command[10];
		private final Command[] redo = new Command[10];
		private int uTop = 0, rTop = 0;
		
		void execute(Command cmd) {
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
	
	// Person Client
	private final PersonService personService;
	private final History history = new History();
	private final Scanner scanner = new Scanner(System.in);
	
	public PersonClient(PersonService personService) {
		this.personService = personService;
	}
	
	public void start() {
		while (true) {
			System.out.println("\n--- Person Menu ---");
			System.out.println("1. Add a Person");
			System.out.println("2. Delete a Person");
			System.out.println("3. Show Persons");
			System.out.println("4. Undo");
			System.out.println("5. Redo");
			System.out.println("6. Back to Main Menu");
			System.out.print("Your choice: ");
			
			int choice = scanner.nextInt();
			scanner.nextLine();
			
			try {
				switch (choice) {
				case 1:
					createPerson();
					break;
				case 2:
					deletePerson();
					break;
				case 3:
					personService.listPersons();
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
						System.out.println("Invalid option. Please try again.");
				}
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}
	
	private void createPerson() {
		System.out.println("Enter type of person (natural/legal): ");
		String t = scanner.nextLine().trim().toLowerCase();
		String type = t.startsWith("l") ? "legal" : "natural"; // Kurzform erlaubt
		
		System.out.println("Enter name: ");
		String name = scanner.nextLine().trim();
		
		history.execute(new CreateCommand(personService, type, name));	
	}
	
	private void deletePerson() {
		System.out.println("Enter name of the person to delete: ");
		String name = scanner.nextLine().trim();
		
		history.execute(new DeleteCommand(personService, name));
	}
	
	public static void main(String[] args) {
		PersonService ps = new PersonService();
		new PersonClient(ps).start();
	}
}

