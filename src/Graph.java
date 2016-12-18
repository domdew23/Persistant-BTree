import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Graph implements Serializable {

	private int nodeCount;
	private int edgeCount;
	HashTable<String, Node> nodes;
	HashTable<Node, HashTable<Node, Edge>> adjList;

	public Graph(File file) throws IOException {
		nodes = new HashTable<String, Node>();
		adjList = new HashTable<Node, HashTable<Node, Edge>>();
		load(file);
	}

	public Node addNode(String id) {
		Node n;
		n = nodes.get(id);
		if (n == null) {
			n = new Node(id);
			nodes.put(id, n);
			adjList.put(n, new HashTable<Node, Edge>());
			nodeCount++;
		}
		return n;
	}

	public void addEdges(String one, String two, double weight) {
		Node src;
		Node dest;
		if (hasEdge(one, two)) {
			return;
		}
		edgeCount++;
		if ((src = getNode(one)) == null) {
			src = addNode(one);
		}
		if ((dest = getNode(two)) == null) {
			dest = addNode(two);
		}
		Edge e = new Edge(src, dest, weight);
		src.addEdge(e);
		dest.addEdge(e);
		adjList.get(src).put(dest, e);
		adjList.get(dest).put(src, e);
	}

	private void load(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		Scanner sc = new Scanner(sb.toString());
		while (sc.hasNext()) {
			String link = sc.next();
			String link2 = sc.next();
			double weight = sc.nextDouble();
			addEdges(link, link2, weight);
		}
		sc.close();
		br.close();
	}

	public boolean connected(Node one, Node two) {
		for (Edge e : one.getEdges()) {
			if (two.getEdges().contains(e)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsNode(String id) {
		return this.nodes.contains(id);
	}

	public Edge getEdge(Node one, Node two) {
		for (Edge e : one.getEdges()) {
			for (Edge edge : two.getEdges()) {
				if (e.equals(edge)) {
					return e;
				}
			}
		}
		System.out.println(one.getID() + " and " + two.getID() + " are not connected");
		return null;
	}

	private boolean hasNode(String id) {
		if (nodes.contains(id)) {
			return true;
		}
		return false;
	}

	private boolean hasEdge(String one, String two) {
		if (!hasNode(one) || !hasNode(two)) {
			return false;
		}
		return adjList.get(nodes.get(one)).contains(nodes.get(two));
	}

	public ArrayList<Node> getNodes() {
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (String s : this.nodes.keys()) {
			nodes.add(getNode(s));
		}
		return nodes;
	}

	public Iterable<Node> adjacentTo(String id) {

		if (!hasNode(id)) {
			return null;
		}
		return adjList.get(getNode(id)).keys();
	}

	public Iterable<Node> adjacentTo(Node node) {

		if (!hasNode(node.getID())) {
			return null;
		}
		return adjList.get(node).keys();
	}

	public Node getNode(String id) {
		return nodes.get(id);
	}

	public int degree(Node node) {
		return adjList.get(node).amountOfKeys();
	}

	public int size() {
		return nodes.size();
	}

	public int amountOfNodes() {
		return nodeCount;
	}

	public int amountOfEdges() {
		return edgeCount;
	}

	public String toString(Node n) {
		return n.getID();
	}

}
