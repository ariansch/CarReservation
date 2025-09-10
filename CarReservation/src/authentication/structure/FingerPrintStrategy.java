package authentication.structure;

import javax.naming.AuthenticationException;

public class FingerPrintStrategy implements AuthenticationStrategy{

	@Override
	public boolean authenticate(Subject subject, Credential credential) throws AuthenticationException {
		if (credential == null || !"fingerprint".equalsIgnoreCase(credential.getType())) {
			return false;
		}
		return false;
	}

	

}
