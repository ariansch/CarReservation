package resource.behaviour;

import resource.structure.Resource;

public class ResourceService {
	// Speicher, max. 10 Ressourcen
	private final Resource [] store = new Resource[10];
	private int size = 0;
	
	public void addResource(Resource r) {
		if (r == null || r.getName() == null || r.getName().trim().isEmpty())
			throw new IllegalArgumentException("Resource name required");
		if (findIndexByName(r.getName()) >= 0) {
			System.out.println("Resource already exists: " + r.getName());
			return;
		}
		if (size >= store.length)
			throw new IllegalStateException("Resource store is full");
		store[size++] = r;
		System.out.println("Resource added: " + r + " (price: " + r.getPrice() + ")");
	}
	
	public void displayResources() {
		if (size == 0) {
			System.out.println("No resources available.");
			return;
		}
		for (int i = 0; i < size; i++)
			System.out.println("- " + store[i] + " | name=" + store[i].getName() + " | price=" + store[i].getPrice());
	}
	
	public void removeResource(String name) {
		int idx = findIndexByName(name);
		if (idx < 0) {
			System.out.println("Resource not found: " + name);
			return;
		}
		// Löschen der Resource
		for (int i = idx; i < size - 1; i++)
			store[i] = store[i + 1];
		size--;
		store[size] = null;
		System.out.println("Resource deleted: " + name);
	}
	
	// Benötigt für BookingService
	public Resource getSelectedResource(String name) {
		int idx = findIndexByName(name);
		if (idx < 0) throw new IllegalArgumentException("Resource not found: " + name);
		return store[idx];
	}
	
	private int findIndexByName(String name) {
		if (name == null)
			return -1;
		for (int i = 0; i < size; i++) {
			if (store[i].getName().equalsIgnoreCase(name))
				return i;
		}
		return -1;
	}

}
