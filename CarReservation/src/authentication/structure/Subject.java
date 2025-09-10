package authentication.structure;

// Represents a subject (user or entity) to be authenticated
public interface Subject {
	String getName();
	void setCredential(Credential credential);
	Credential getCredential();
}
