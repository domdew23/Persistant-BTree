import java.io.Serializable;

public class Edge implements Serializable {
	double weight;
	Node destination;
	Node source;
	Node[] both;

	public Edge(Node src, Node dest, double weight) {
		this.weight = weight;
		destination = dest;
		source = src;
		both = new Node[] { src, dest };
	}

	public Node getEither() {
		return source;
	}

	public Node getOther(Node vertex) {
		if (vertex == destination) {
			return source;
		} else if (vertex == source) {
			return destination;
		} else {
			System.out.println("Illegal endpoint");
			return null;
		}
	}

	public int compareWeight(Edge that) {
		if (this.weight < that.weight) {
			return -1;
		} else if (this.weight > that.weight) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean has(Node n) {
		if (destination.getID().equals(n.getID())) {
			System.out.println("YES " + n.getID());
			return true;
		}
		return false;
	}

	public String toString(Edge edge) {
		return "" + edge;
	}
}