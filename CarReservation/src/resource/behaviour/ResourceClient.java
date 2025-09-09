package resource.behaviour;

import java.util.Scanner;

import resource.structure.Car;
import resource.structure.ChildSeat;
import resource.structure.Resource;
import resource.structure.SetTopBox;

public class ResourceClient {
	
	private interface Command {
		void execute();
		void undo();
	}
	
	private static final class CreateCommand implements Command {
		private final ResourceService svc;
		private final Resource resource;
		public CreateCommand(ResourceService svc, Resource resource) {
			this.svc = svc;
			this.resource = resource;
		}
		public void execute() {
			svc.addResource(resource);
		}
		public void undo() {
			svc.removeResource(resource.getName());
		}
	}
	
	private static final class DeleteCommand implements Command {
		private final ResourceService svc;
		private final String name;
		// Snapshot für Undo
		private Resource snapResource;
		
		public DeleteCommand(ResourceService svc, String name) {
			this.svc = svc;
			this.name = name;
		}
		
		public void execute() {
			snapResource = svc.getSelectedResource(name);
			svc.removeResource(name);
		}
		
		public void undo() {
			if (snapResource != null)
				svc.addResource(snapResource);
		}
	}
	
	private static final class History {
		private final Command[] undo = new Command[200];
		private final Command[] redo = new Command [200];
		private int uTop = 0, rTop = 0;
		void execute(Command cmd) {
			cmd.execute();
			undo[uTop++] = cmd;
			rTop = 0; // redo löschen
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
	
	// Resource Client
	private final ResourceService resourceService;
	private final History history = new History();
	private final Scanner scanner = new Scanner(System.in);
	
	public ResourceClient(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	public void start() {
		while (true) {
			System.out.println("1. Daten eingeben");
			System.out.println("2. Daten löschen");
			System.out.println("3. Daten ausgeben");
			System.out.println("4. Undo");
			System.out.println("5. Redo");
			System.out.println("6. Exit");
			
			int choice = scanner.nextInt();
			scanner.nextLine();
			
			try {
				switch (choice) {
				case 1:
					createResource();
					break;
				case 2:
					deleteResource();
					break;
				case 3:
					resourceService.displayResources();
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
	
	private void createResource() {
		System.out.print("Enter car model name: ");
		String name = scanner.nextLine().trim();
		
		System.out.print("Enter base price: ");
		double price = Double.parseDouble(scanner.nextLine().trim());
		
		Resource r = new Car(name, price);
		
		System.out.print("Add Child Seat (y/n)? ");
		if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
			r = new ChildSeat(r);
		}
		
		System.out.print("Add Set Top Box (y/n)? ");
		if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
			r = new SetTopBox(r);
		}
		
		history.execute(new CreateCommand(resourceService, r));
	}
	
	private void deleteResource() {
		System.out.print("Enter name of the resource to delete: ");
		String name = scanner.nextLine().trim();
		
		history.execute(new DeleteCommand(resourceService, name));
	}
	
	public static void main(String[] args) {
		new ResourceClient(new ResourceService()).start();
	}

}
