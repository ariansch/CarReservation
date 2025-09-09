package resource.structure;

public class ResourceDecorator extends Resource {
	
	protected final Resource inner;
	protected final double extra;
	protected final String label;
	
	public ResourceDecorator(Resource inner, double extra, String label) {
		super(inner.getName(), 0.0);
		this.inner = inner;
		this.extra = extra;
		this.label = label;
	}
	
	@Override
	public String getName() {
		return inner.getName();
	}
	
	@Override
	public double getPrice() {
		return inner.getPrice() + extra;
	}
	
	@Override
	public String toString() {
		return inner.toString() + " + " + label;
	}
}
