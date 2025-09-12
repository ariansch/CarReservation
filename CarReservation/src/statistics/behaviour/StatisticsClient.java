package statistics.behaviour;

import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;

import booking.behaviour.BookingService;
import booking.structure.Booking;

public class StatisticsClient {


	private interface Command {
		void execute();

		void undo();
	}

	private static final class History {
		private final Command[] undo = new Command[200];
		private final Command[] redo = new Command[200];
		private int uTop = 0, rTop = 0;

		void execute(Command cmd) {
			cmd.execute();
			undo[uTop++] = cmd;
			rTop = 0;
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

	private static final class SetSelectionCommand implements Command {
		private final List<Booking> target;
		private final List<Booking> newSelection;
		private List<Booking> oldSnapshot;

		SetSelectionCommand(List<Booking> target, List<Booking> newSelection) {
			this.target = target;
			this.newSelection = newSelection;
		}

		@Override
		public void execute() {
			oldSnapshot = new ArrayList<>(target);
			target.clear();
			if (newSelection != null)
				target.addAll(newSelection);
		}

		@Override
		public void undo() {
			target.clear();
			if (oldSnapshot != null)
				target.addAll(oldSnapshot);
		}
	}

	/** Löscht die aktuelle Auswahl (mit Undo/Redo). */
	private static final class ClearSelectionCommand implements Command {
		private final List<Booking> target;
		private List<Booking> oldSnapshot;

		ClearSelectionCommand(List<Booking> target) {
			this.target = target;
		}

		@Override
		public void execute() {
			oldSnapshot = new ArrayList<>(target);
			target.clear();
		}

		@Override
		public void undo() {
			target.clear();
			if (oldSnapshot != null)
				target.addAll(oldSnapshot);
		}
	}
	
	private final BookingService bookingService;
	private final StatisticsService statisticsService;

	private final List<Booking> selection = new ArrayList<>();
	private final History history = new History();
	private final Scanner scanner = new Scanner(System.in);

	public StatisticsClient(BookingService bookingService, StatisticsService statisticsService) {
		this.bookingService = bookingService;
		this.statisticsService = statisticsService;
	}

	public void start() {
		while (true) {
			System.out.println("1. Auswahl tätigen");
			System.out.println("2. Auswahl löschen");
			System.out.println("3. Ergebnisse anzeigen");
			System.out.println("4. Undo");
			System.out.println("5. Redo");
			System.out.println("6. Exit");

			String choice = scanner.nextLine().trim();
			try {
				switch (choice) {
				case "1":
					setSelection();
					break;
				case "2":
					clearSelection();
					break;
				case "3":
					showResults();
					break;
				case "4":
					if (history.canUndo())
						history.undo();
					else
						System.out.println("Nothing to undo.");
					break;
				case "5":
					if (history.canRedo())
						history.redo();
					else
						System.out.println("Nothing to redo.");
					break;
				case "6":
					System.out.println("Exiting ");
					return;
				default:
					System.out.println("Invalid option. Please try again.");
				}
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}

	private void setSelection() {
		System.out.print("IDs hier eingeben): ");
		String line = scanner.nextLine().trim();
		if (line.isBlank()) {
			System.out.println("Keine IDs eingegeben ");
			return;
		}
		String[] parts = line.split(",");
		List<Booking> newSel = new ArrayList<>();
		for (String raw : parts) {
			String id = raw.trim();
			if (id.isEmpty())
				continue;
			Booking b = bookingService.getBookingById(id);
			if (b != null) {
				newSel.add(b);
			} else {
				System.out.println("Booking mit ID '" + id + "' nicht gefunden ");
			}
		}
		history.execute(new SetSelectionCommand(selection, newSel));
		System.out.println("Auswahl gesetzt: " + selection.size() + " Buchung/Buchungen.");
	}

	private void clearSelection() {
		history.execute(new ClearSelectionCommand(selection));
		System.out.println("Auswahl gelöscht.");
	}

	private void showResults() {
		List<Booking> de = statisticsService.filterGerman(selection);
		List<Booking> en = statisticsService.filterEnglish(selection);
		StatisticsService.Result res = statisticsService.compute(selection);


		System.out.println("Deutsche Buchungen:");
		if (de.isEmpty())
			System.out.println("- keine deutschen Buchungen -");
		else
			for (Booking b : de)
				System.out.println("- " + b.getBookingId());

		System.out.println("Englische Buchungen:");
		if (en.isEmpty())
			System.out.println("- keine englischen Buchungen -");
		else
			for (Booking b : en)
				System.out.println("- " + b.getBookingId());

		System.out.println("Summe und Anzahl:");
		System.out.println("DE -- count=" + res.getGermanCount() + ", total=" + res.getGermanTotal());
		System.out.println("EN -- count=" + res.getEnglishCount() + ", total=" + res.getEnglishTotal());
	}
}
