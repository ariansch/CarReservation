package tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import authentication.behaviour.AuthenticationService;
import authentication.structure.AuthenticationStrategy;
import authentication.structure.Credential;
import authentication.structure.CredentialType;
import authentication.structure.FingerPrintStrategy;
import authentication.structure.EyeScanStrategy;
import authentication.structure.Subject;
import authentication.structure.UserNamePasswordStrategy;
import person.behaviour.PersonService;
import person.structure.Person;
import person.structure.LegalPerson;

class AuthenticationServiceTest {

	private AuthenticationService authService;
	private PersonService personService;
	private Subject user;
	private Credential validPassword;
	private Credential validFingerprint;
	private Credential validEyeScan;

	@BeforeEach
	public void setUp() {
		// Testumgebung für jeden Test vorbereiten
		personService = new PersonService();
		authService = new AuthenticationService(personService);

		// Testperson und Credentials erstellen
		personService.createPerson("natural", "Max Mustermann");
		user = personService.findPersonByName("Max Mustermann");

		validPassword = new Credential(CredentialType.PASSWORD, "secret123");
		validFingerprint = new Credential(CredentialType.FINGERPRINT, "valid_fingerprint");
		validEyeScan = new Credential(CredentialType.EYESCAN, "valid_eye_scan");

		// Credentials der Testperson zuweisen
		user.setCredential(validPassword);
	}

	@Test
	// Testet die erfolgreiche Authentifizierung mit korrektem Passwort.
	public void isAuthenticationWithCorrectPasswordSuccessful() {
		AuthenticationStrategy strategy = new UserNamePasswordStrategy();
		Credential providedPassword = new Credential(CredentialType.PASSWORD, "secret123");

		assertTrue(authService.authenticateSubject(strategy, user, providedPassword),
				"Authentifizierung mit korrektem Passwort sollte erfolgreich sein.");
	}

	@Test
	// Testet die fehlgeschlagene Authentifizierung mit falschem Passwort.
	public void isAuthenticationWithIncorrectPasswordRejected() {
		AuthenticationStrategy strategy = new UserNamePasswordStrategy();
		Credential providedPassword = new Credential(CredentialType.PASSWORD, "wrongpassword");

		assertFalse(authService.authenticateSubject(strategy, user, providedPassword),
				"Authentifizierung mit falschem Passwort sollte fehlschlagen.");
	}

	@Test
	// Testet, ob die Authentifizierung bei einer falschen Strategie fehlschlägt.
	public void isAuthenticationWithWrongStrategyRejected() {
		AuthenticationStrategy strategy = new FingerPrintStrategy();
		Credential providedCredential = new Credential(CredentialType.FINGERPRINT, "some_data");

		assertFalse(authService.authenticateSubject(strategy, user, providedCredential),
				"Authentifizierung sollte bei Strategie-Typ-Fehler fehlschlagen.");
	}

	@Test
	// Testet die erfolgreiche Fingerprint-Authentifizierung.
	public void isFingerPrintAuthenticationSuccessful() {
		user.setCredential(validFingerprint);

		AuthenticationStrategy strategy = new FingerPrintStrategy();
		Credential providedCredential = new Credential(CredentialType.FINGERPRINT, "valid_fingerprint");

		assertTrue(authService.authenticateSubject(strategy, user, providedCredential),
				"Fingerprint-Authentifizierung mit korrekten Daten sollte erfolgreich sein.");
	}

	@Test
	// Testet die fehlgeschlagene Fingerprint-Authentifizierung mit falschen Daten.
	public void isFingerPrintAuthenticationRejectedWithWrongData() {
		user.setCredential(validFingerprint);

		AuthenticationStrategy strategy = new FingerPrintStrategy();
		Credential providedCredential = new Credential(CredentialType.FINGERPRINT, "wrong_fingerprint");

		assertFalse(authService.authenticateSubject(strategy, user, providedCredential),
				"Fingerprint-Authentifizierung mit falschen Daten sollte abgelehnt werden.");
	}

	@Test
	// Testet die erfolgreiche EyeScan-Authentifizierung.
	public void isEyeScanAuthenticationSuccessful() {
		user.setCredential(validEyeScan);

		AuthenticationStrategy strategy = new EyeScanStrategy();
		Credential providedCredential = new Credential(CredentialType.EYESCAN, "valid_eye_scan");

		assertTrue(authService.authenticateSubject(strategy, user, providedCredential),
				"EyeScan-Authentifizierung mit korrekten Daten sollte erfolgreich sein.");
	}

	@Test
	// Testet die fehlgeschlagene EyeScan-Authentifizierung mit falschen Daten.
	public void isEyeScanAuthenticationRejectedWithWrongData() {
		user.setCredential(validEyeScan);

		AuthenticationStrategy strategy = new EyeScanStrategy();
		Credential providedCredential = new Credential(CredentialType.EYESCAN, "wrong_eye_scan");

		assertFalse(authService.authenticateSubject(strategy, user, providedCredential),
				"EyeScan-Authentifizierung mit falschen Daten sollte abgelehnt werden.");
	}

	@Test
	// Testet die fehlgeschlagene Authentifizierung, wenn keine Credentials gesetzt sind.
	public void isAuthenticationRejectedIfNoCredentialsSet() {
		user.setCredential(null);

		AuthenticationStrategy strategy = new UserNamePasswordStrategy();
		Credential providedCredential = new Credential(CredentialType.PASSWORD, "any_password");

		assertFalse(authService.authenticateSubject(strategy, user, providedCredential),
				"Authentifizierung sollte abgelehnt werden, wenn keine Credentials gesetzt sind.");
	}
}