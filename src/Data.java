import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Data {

	private HashTable<Link, ArrayList<Link>> table;
	String[] roots = new String[] { "https://en.wikipedia.org/wiki/Drake_(rapper)" };
	private int count;
	Node src;
	Node dest;
	ArrayList<Edge> path;
	int treeCount;

	public Data(String one, String two) throws IOException, ClassNotFoundException {
		File file = new File("graph.bin");
		File linksFile = new File("links.txt");
		table = new HashTable<Link, ArrayList<Link>>();
		if (linksFile.length() == 0) {
			for (String key : roots) {
				Link link = new Link(key);
				write(file, link);
			}
		}
		if (file.length() == 0) {
			Graph g = new Graph(linksFile);
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
			os.writeObject(g);
			os.close();
		} else {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
			Graph g = (Graph) is.readObject();
			PrimMST prim = new PrimMST(g);
			treeCount = prim.report();
			src = g.getNode(one);
			dest = g.getNode(two);
			BestPath best = new BestPath(g, src);
			path = best.pathTo(dest);
			is.close();
		}
	}

	private int[] addValues(int[] values, HashTable<String, Integer> table) {
		int i = 0;
		for (String key : table.keys()) {
			values[i] = table.get(key);
			i++;
		}
		return values;
	}

	public Double similar(HashTable<String, Integer> t1, HashTable<String, Integer> t2) throws IOException {

		double sim = 0.0;
		int[] values = new int[t1.amountOfKeys()];
		int[] values2 = new int[t2.amountOfKeys()];
		values = addValues(values, t1);
		values2 = addValues(values2, t2);
		if (values2.length < values.length) {
			sim = cosineSimilarity(toDoubleArray(values2), toDoubleArray(values));
		} else {
			sim = cosineSimilarity(toDoubleArray(values), toDoubleArray(values2));
		}
		return sim;
	}

	private double[] toDoubleArray(int[] array) {
		double[] doubleArray = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			doubleArray[i] = array[i];
		}
		return doubleArray;
	}

	private double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private void write(File file, Link key) throws IOException {
		Writer output = null;
		output = new BufferedWriter(new FileWriter(file));
		getLinks(key.id);
		for (Link k : table.keys()) {
			HashTable<String, Integer> t1 = new HashTable<String, Integer>();
			t1 = parse(k.id, t1);
			for (Link link : table.get(k)) {
				HashTable<String, Integer> t2 = new HashTable<String, Integer>();
				t2 = parse(link.id, t2);
				Double sim = similar(t1, t2);
				if (!sim.isNaN()) {
					// System.out.println("\n" + k.id + " " + link.id + " " +
					// sim);
					output.write(k.id + " " + link.id + " " + sim + "\n");
				}
			}
		}

		System.out.println("Done Writing");
		output.close();
	}

	private void getLinks(String url) throws IOException {
		ArrayList<Link> list = new ArrayList<Link>();
		Document doc = Jsoup.connect(url).get();
		Elements pars = doc.select("p");
		for (Element par : pars) {
			Elements anchor = par.select("a");
			for (Element link : anchor) {
				String temp = link.attr("href");
				if (temp.startsWith("/wiki/")) {
					temp = "https://en.wikipedia.org" + temp;
				}
				if (temp.contains("https://en.wikipedia.org/wiki/") && !temp.contains("File")) {
					if (!list.contains(temp) && !temp.equals(url)) {
						Link l = new Link(temp);
						list.add(l);
					}
				}
			}
		}
		Link l = new Link(url);
		if (!table.contains(l)) {
			table.put(l, list);
		}
		for (Link key : table.keys()) {
			if (!key.marked) {
				for (Link link : table.get(key)) {
					if (!link.marked2) {
						link.marked2 = true;
						if (table.amountOfKeys() < 500) {
							getLinks(link.id);
						} else {
							return;
						}
					}
				}
				key.marked = true;
			}
		}
	}

	private HashTable<String, Integer> parse(String url, HashTable<String, Integer> table) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements paragraphs = doc.select(".mw-content-ltr p");
		Element firstParagraph = paragraphs.first();
		Element lastParagraph = paragraphs.last();
		Element p;
		int i = 1;
		p = firstParagraph;
		while (p != lastParagraph) {
			p = paragraphs.get(i);
			String words = p.ownText();
			Scanner stringScanner = new Scanner(words);
			while (stringScanner.hasNext()) {
				String word = stringScanner.next();
				word = word.toLowerCase();
				word = word.replaceAll("[^a-zA-Z]+", "");
				word = word.trim();
				if (word != null) {
					table.put(word, 1);
				}
			}
			stringScanner.close();
			i++;
		}
		return table;
	}

	private static void getTeams(String url) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		Document doc = Jsoup.connect(url).get();
		Elements pars = doc.select("tr");
		for (Element par : pars) {
			Elements anchor = par.select("td");
			for (Element link : anchor) {
				String temp = link.attr("align");
				list.add(temp);
			}
		}
	}

	public int report() {
		return count;
	}

}
