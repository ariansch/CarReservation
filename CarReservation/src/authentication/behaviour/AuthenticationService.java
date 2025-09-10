package authentication.behaviour;

import javax.naming.AuthenticationException;

import authentication.structure.AuthenticationStrategy;
import authentication.structure.Credential;
import authentication.structure.Subject;
import person.behaviour.PersonService;

public class AuthenticationService {
	private final PersonService personService;
	
	public AuthenticationService(PersonService personService) {
		this.personService = personService;
	}

	public boolean authenticateSubject(AuthenticationStrategy strategy, Subject subject, Credential credential) {
		try {
			boolean authenticated = strategy.authenticate(subject, credential);
			
			if (authenticated) {
				System.out.println("Success: " + subject.getName() + " has been authenticated successfully.");
				return true;
			} else {
				System.out.println("Error: Authentication for " + subject.getName() + " failed (invalid credentials).");
				return false;
			}
		} catch (AuthenticationException e) {
			System.err.println(
					"System error during authentication for " 
					+ subject.getName() + ": " + e.getMessage());
			return false;
		}
	}
	
}
