package authentication.structure;

import javax.naming.AuthenticationException;

// Authentication strategy: Fingerprint	
public class EyeScanStrategy implements AuthenticationStrategy {
	@Override
	public boolean authenticate(Subject subject, Credential providedCredential) throws AuthenticationException {
		if (subject == null || providedCredential == null) {
			return false;
		}

		Credential storedCredential = subject.getCredential();
		if (storedCredential == null) {
			return false;
		}

		if (storedCredential.getType() != CredentialType.EYESCAN
				|| providedCredential.getType() != CredentialType.EYESCAN) {
			return false;
		}

		return storedCredential.getValue().equals(providedCredential.getValue());
	}
}
