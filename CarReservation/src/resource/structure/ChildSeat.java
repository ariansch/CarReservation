package resource.structure;

public class ChildSeat extends ResourceDecorator {
	
	public ChildSeat(Resource inner)
	{
		super(inner, 15.0, "Child Seat");
	}

}
