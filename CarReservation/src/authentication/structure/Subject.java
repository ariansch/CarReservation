package authentication.structure;
// Represents a subject (user or entity) to be authenticated
public class Subject {
	private String name;
	
	public Subject(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
