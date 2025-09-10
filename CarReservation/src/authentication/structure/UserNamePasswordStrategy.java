package authentication.structure;

import javax.naming.AuthenticationException;

public class UserNamePasswordStrategy implements AuthenticationStrategy {

	@Override
	public boolean authenticate(Subject subject, Credential credential) throws AuthenticationException {
		if (credential == null || !"password".equalsIgnoreCase(credential.getType())) {
			return false;
		}

		return true;
	}

}
