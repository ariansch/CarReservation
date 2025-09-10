package authentication.structure;

import javax.naming.AuthenticationException;

//Authentication strategy: Username and Password
public class UserNamePasswordStrategy implements AuthenticationStrategy {

	@Override
	public boolean authenticate(Subject subject, Credential providedCredential) throws AuthenticationException {
		if (subject == null || providedCredential == null) {
			return false;
		}

		Credential storedCredential = subject.getCredential();

		if (storedCredential == null) {
			return false;
		}

		if (storedCredential.getType() != CredentialType.PASSWORD
				|| providedCredential.getType() != CredentialType.PASSWORD) {
			return false;
		}

		return storedCredential.getValue().equals(providedCredential.getValue());
	}
}