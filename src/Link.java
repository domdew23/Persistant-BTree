public class Link {
	String id;
	boolean marked = false;
	boolean marked2 = false;

	public Link(String id) {
		this.id = id;
	}

	public Link get(String id) {
		if (id.equals(this.id)) {
			return this;
		}
		return null;
	}
}
