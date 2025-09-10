package authentication.structure;

public class EyeScanStrategy implements AuthenticationStrategy {

	@Override
	public boolean authenticate(Subject subject, Credential credential) {
		if (credential == null || !"eyescan".equalsIgnoreCase(credential.getType())) {
			return false;
		}
		return true;
	}
	
}
