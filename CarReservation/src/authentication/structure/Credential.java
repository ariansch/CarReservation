package authentication.structure;

public class Credential {
	//Type of credential, e.g., "password", fingerprint, "eye scan"
	private String type;
	
	//Value of the credential, e.g., actual password, fingerprint data, eye scan data
	private String value;
	
	public Credential(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
}
