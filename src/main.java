import java.io.File;
import java.io.File;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class main {
	private static Random rand;
	private static Node one;
	private static Node two;
	private static BestPath best;
	private static ArrayList<Team> teams = new ArrayList<Team>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		run();
	}

	private static void print(Graph g) {
		for (Node n : g.getNodes()) {
			System.out.println(n.getID() + ": ");
			for (Node x : g.adjacentTo(n)) {
				System.out.println(x.getID() + " ");
			}
		}
	}

	private static void test() throws IOException {
		rand = new Random();
		File file = new File("prac4.txt");
		Graph g = new Graph(file);
		Test prim = new Test(g);
		System.out.println("Trees: " + prim.report());
		System.out.println("Total Weight: " + prim.weight());
		System.out.println("");
		one = anyNode(g);
		two = anyNode(g);
		best = new BestPath(g, one);
		check(best, g, one, two);

		System.out.println("SOURCE: " + one.getID());
		for (Edge e : best.pathTo(two)) {
			System.out.println("PATH: " + e.destination.getID() + " WEIGHT: " + e.weight);
		}
		System.out.println("DESTINATION: " + two.getID());
	}

	private static boolean check(BestPath b, Graph g, Node src, Node dst) {
		if (b.hasPathTo(two) && b.pathTo(two) != null) {
			System.out.println(b.pathTo(two).size());
			one = src;
			two = dst;
			best = b;
			return true;
		} else {
			one = anyNode(g);
			two = anyNode(g);
			b = new BestPath(g, one);
			check(b, g, one, two);
			return false;
		}
	}

	private static Node anyNode(Graph g) {
		int index = rand.nextInt(g.amountOfNodes());
		Node node = g.getNodes().get(index);
		return node;
	}

	private static void run() throws IOException, ClassNotFoundException {

		String src = JOptionPane.showInputDialog("Enter a source link: ");
		String dest = JOptionPane.showInputDialog("Enter a desintation link: ");
		Data d = new Data(src, dest);
		StringBuilder sb = new StringBuilder();
		sb.append("\nAmount of Trees: " + d.treeCount + "\n");
		sb.append("Path:\n");
		for (Edge e : d.path) {
			sb.append("\n" + e.destination.getID() + "\n");
		}
		// Sources s = new Sources(url);

		JOptionPane.showMessageDialog(null, sb, "the title", JOptionPane.PLAIN_MESSAGE);
	}
}
