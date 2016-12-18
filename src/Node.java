import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
	private String id;
	private ArrayList<Edge> edges;
	double best = Double.MAX_VALUE;
	boolean marked = false;

	public Node(String id) {
		this.id = id;
		edges = new ArrayList<Edge>();
	}

	public void addEdge(Edge edge) {
		if (containsEdge(edge)) {
			return;
		}
		this.edges.add(edge);
	}

	public boolean containsEdge(Edge edge) {
		if (this.edges.contains(edge)) {
			return true;
		}
		return false;
	}

	public boolean hasEdge(Node that) {
		for (Edge e : this.edges) {
			for (Node n : e.both) {
				if (n.equals(that)) {
					return true;
				}
			}
		}
		return false;
	}

	public void removeEdge(Edge e) {
		this.edges.remove(e);
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public ArrayList<Node> getNodes() {
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (Edge e : this.edges) {
			if (!nodes.contains(e.getOther(this))) {
				nodes.add(e.getOther(this));
			}
		}
		return nodes;
	}

	public Edge getEdge(Node node) {
		for (Edge e : this.edges) {
			if (node.containsEdge(e)) {
				return e;
			}
		}
		System.out.println(this.id + " and " + node.getID() + " do not contain an edge");
		return null;
	}

	public Node getByWeight(Node n, double w) {
		for (Edge e : n.edges) {
			if (e.weight == w) {
				return e.getOther(n);
			}
		}
		System.out.println("Node has no edge with this weight");
		return null;
	}

	public String getID() {
		return id;
	}
}