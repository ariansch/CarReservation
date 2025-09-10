package authentication.structure;

public class Credential {
	// Type of credential, e.g., "password", fingerprint, "eye scan"
	private final CredentialType type;

	// Value of the credential, e.g., actual password, fingerprint data, eye scan
	// data
	private final String value;

	public Credential(CredentialType type, String value) {
		this.type = type;
		this.value = value;
	}

	public CredentialType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

}
