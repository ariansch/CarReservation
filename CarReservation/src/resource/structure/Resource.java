package resource.structure;

public class Resource {
	
	private final String name;
	private final double price;
	
	public Resource(String name) {
		this(name, 0.0);
	}
	public Resource(String name, double price) {
		this.name = name;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	
	public double getPrice() {
		return price;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
