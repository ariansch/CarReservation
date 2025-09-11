package payment.structure;

// Abstrakte Basisklasse für das Template Method
// 3 Schritte Algorithmus für eine Zahlung
public abstract class PaymentProcessor {

	// Die Template-Methode. Sie ist final, um den Algorithmus-Ablauf zu schützen.
	public final boolean processPayment(Account sender, Account receiver, CurrencyAmount amount) {
		// Schritt 1: Authentifizierung (für alle gleich)
		authenticatePayer(sender);

		// Schritt 2: Buchung (individuell)
		boolean success = transferAmount(sender, receiver, amount);

		// Schritt 3: Bestätigung (für alle gleich bei Erfolg)
		if (success) {
			createConfirmation(sender, receiver, amount);
		} else {
			System.out.println("Payment failed. No confirmation will be created.");
		}
		return success;
	}

	// Simulation der Bezahlung, da sonst bspw. Datenbankanbindung notwendig wäre

	// Fester Schritt 1: für alle Bezahlmethoden identisch.
	private void authenticatePayer(Account sender) {
		System.out.println("\n--- Starting Payment Process ---");
		System.out.println("Step 1/3: Authenticating payer '" + sender.getOwner().getName() + "'...");
		// Simulation einer erfolgreichen Authentifizierung
		System.out.println("=> Authentication successful.");
	}

	// Fester Schritt 3: für alle Bezahlmethoden identisch.
	private void createConfirmation(Account sender, Account receiver, CurrencyAmount amount) {
		System.out.println("Step 3/3: Creating payment confirmation for " + amount + "...");
		// Simulation der Bestätigung
		System.out.println("=> Confirmation created and sent to '" + sender.getOwner().getName() + "'.");
		System.out.println("--- Payment Process Finished ---");
	}

	// Variabler Schritt 2: Muss von jeder konkreten Klasse implementiert werden.
	protected abstract boolean transferAmount(Account sender, Account receiver, CurrencyAmount amount);
}