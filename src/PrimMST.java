import java.util.ArrayList;

public class PrimMST {
	private HashTable<Edge, Node> edgeTo;
	private PriorityQueue pq;
	private int count;

	public PrimMST(Graph g) {
		edgeTo = new HashTable<Edge, Node>(g.amountOfNodes());
		pq = new PriorityQueue(g.amountOfNodes());
		count = 0;

		for (Node n : g.getNodes()) {
			// run prim for each unmarked node
			if (!n.marked) {
				prim(g, n);
			}
		}
	}

	private void prim(Graph g, Node first) {
		first.best = 0.0;
		pq.insert(first);
		while (!pq.isEmpty()) {
			Node n = pq.remove();
			scan(g, n);
		}
		count++;
	}

	private void scan(Graph g, Node n) {
		n.marked = true;
		for (Edge e : n.getEdges()) {
			Node w = e.getOther(n);
			if (w.marked) {
				continue;
			}
			if (e.weight < w.best) {
				n.best = e.weight;
				edgeTo.put(e, w);
				if (pq.contains(w)) {
					pq.decreaseKey(w);
				} else {
					pq.insert(w);
				}
			}
		}
	}

	public ArrayList<Edge> edges() {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (Edge e : edgeTo.keys()) {
			edges.add(e);
		}
		return edges;
	}

	public int report() {
		return count;
	}

	public double weight() {
		double total = 0.0;
		for (Edge e : edges()) {
			total += e.weight;
		}
		return total;
	}
}
