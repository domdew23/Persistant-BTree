import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BestPath {

	private PriorityQueue pq;
	private HashMap<Node, Edge> edgeTo;

	public BestPath(Graph g, Node s) {
		int max = g.amountOfNodes();
		edgeTo = new HashMap<Node, Edge>(max);
		pq = new PriorityQueue(max);
		reset(g);
		s.best = 0.0;
		pq.insert(s);
		while (!pq.isEmpty()) {
			Node n = pq.remove();
			for (Edge e : n.getEdges()) {
				Dijkstra(e);
			}
		}
	}

	private void Dijkstra(Edge e) {
		Node v = e.source;
		Node w = e.destination;
		if (w.best > v.best + e.weight) {
			w.best = v.best + e.weight;
			edgeTo.put(w, e);
			if (pq.contains(w)) {
				pq.decreaseKey(w);
			} else {
				pq.insert(w);
			}
		}
	}

	public ArrayList<Edge> pathTo(Node n) {
		if (!hasPathTo(n)) {
			return null;
		}
		ArrayList<Edge> path = new ArrayList<Edge>();
		for (Edge e = edgeTo.get(n); e != null; e = edgeTo.get(e.source)) {
			path.add(e);
		}
		Collections.reverse(path);
		return path;
	}

	public double distTo(Node n) {
		return n.best;
	}

	public boolean hasPathTo(Node n) {
		return n.best < Double.MAX_VALUE;
	}

	private void reset(Graph g) {
		for (String s : g.nodes.keys()) {
			g.nodes.get(s).best = Double.MAX_VALUE;
		}
	}
}
