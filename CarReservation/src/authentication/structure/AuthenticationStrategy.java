package authentication.structure;

import javax.naming.AuthenticationException;
// Strategy interface for different authentication methods
public interface AuthenticationStrategy {
		boolean authenticate(Subject subject, Credential credential) throws AuthenticationException;
}
