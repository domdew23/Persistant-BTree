import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Sources {

	private HashTable<String, BTree> sportsTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> musicTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> carsTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> moviesTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> videoGamesTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> foodTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> clothingTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> englishTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> healthTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> politicsTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> companiesTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> technologyTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> countriesTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> weatherTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> religonTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> animalsTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> scienceTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> booksTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> historyTable = new HashTable<String, BTree>();
	private HashTable<String, BTree> lawsTable = new HashTable<String, BTree>();

	private BTree sportsTree;
	private BTree musicTree;
	private BTree carsTree;
	private BTree moviesTree;
	private BTree videoGamesTree;
	private BTree foodTree;
	private BTree clothingTree;
	private BTree englishTree;
	private BTree healthTree;
	private BTree politicsTree;
	private BTree companiesTree;
	private BTree technologyTree;
	private BTree countriesTree;
	private BTree weatherTree;
	private BTree religonTree;
	private BTree animalsTree;
	private BTree scienceTree;
	private BTree booksTree;
	private BTree historyTree;
	private BTree lawsTree;

	private int maxDepth = 1;
	private int maxLinks = 60;

	private File userFile = new File("/Users/dominicdewhurst/Documents/Computer Science/BTree2/testUser");
	private RandomAccessFile userRAF = new RandomAccessFile(userFile, "rw");

	private String[] files = new String[] { "/Users/dominicdewhurst/Documents/Computer Science/BTree2/testSports",
			"/Users/dominicdewhurst/Documents/Computer Science/csc365Links/testMusic",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testCars",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testMovies",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testVideoGames",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testFood",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testColthing",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testEnglish",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testHealth",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testPolitics",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testCompanies",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testTechnology",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testCountries",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testWeather",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testReligon",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testAnimals",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testScience",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testBooks",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testHistory",
			"/Users/dominicdewhurst/Documents/Computer Science/BTree2/testLaws", };
	private String[] rootPages = new String[] { "https://en.wikipedia.org/wiki/Sport",
			"https://en.wikipedia.org/wiki/Music", "https://en.wikipedia.org/wiki/Car",
			"https://en.wikipedia.org/wiki/Film", "https://en.wikipedia.org/wiki/Video_game",
			"https://en.wikipedia.org/wiki/Food", "https://en.wikipedia.org/wiki/Clothing",
			"https://en.wikipedia.org/wiki/English_language", "https://en.wikipedia.org/wiki/Health",
			"https://en.wikipedia.org/wiki/Politics", "https://en.wikipedia.org/wiki/Company",
			"https://en.wikipedia.org/wiki/Technology", "https://en.wikipedia.org/wiki/Country",
			"https://en.wikipedia.org/wiki/Weather", "https://en.wikipedia.org/wiki/Religion",
			"https://en.wikipedia.org/wiki/Animal", "https://en.wikipedia.org/wiki/Science",
			"https://en.wikipedia.org/wiki/Book", "https://en.wikipedia.org/wiki/History",
			"https://en.wikipedia.org/wiki/Law" };

	final String userURL;

	ArrayList<String> finals = new ArrayList<String>();

	public Sources(String url) throws IOException {
		ArrayList<BTree> btrees = new ArrayList<BTree>();
		btrees = addTrees(btrees);
		ArrayList<HashTable<String, BTree>> tables = new ArrayList<HashTable<String, BTree>>();
		tables = addTables(tables);
		this.userURL = url;
		ArrayList<Double> sims = new ArrayList<Double>();
		ArrayList<Double> top10 = new ArrayList<Double>();
		for (int i = 0; i < btrees.size(); i++) {
			System.out.println("LOADED TREE# " + i);
			String file = files[i];
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			BTree tree = btrees.get(i);
			HashTable<String, BTree> table = tables.get(i);
			if (raf.length() != 0) {
				table = new HashTable<String, BTree>();
				tree = new BTree(raf);
				sims.add(similar(userURL, tree, i));
				table.put(rootPages[i], tree);
			} else {
				table = new HashTable<String, BTree>();
				tree = new BTree(raf);
				table.put(rootPages[i], tree);
				tree = parse(rootPages[i], tree, 0, table);
				sims.add(similar(userURL, tree, i));
			}
		}
		ArrayList<Double> tmpSims = new ArrayList<Double>(sims);
		top10 = new ArrayList<Double>(tmpSims.subList(tmpSims.size() - 10, tmpSims.size()));
		for (int i = 0; i < sims.size(); i++) {
			if (top10.contains(sims.get(i))) {
				finals.add(rootPages[i]);
			}
		}

	}

	public Double similar(String url, BTree tree, int spot) throws IOException {

		if (userRAF.length() == 0) {
			BTree userTree = new BTree(userRAF);
			HashTable<String, BTree> userTable = new HashTable<String, BTree>();
			userTable.put(url, userTree);
			if (spot > 0) {
				userTree = parse(url, userTree, 0, userTable);
			}
			double sim = 0.0;
			ArrayList<String> valueStrings = new ArrayList<String>();
			ArrayList<String> userValueStrings = new ArrayList<String>();
			Scanner userValues = new Scanner(userTree.traverseValues(userTree.root));
			Scanner values = new Scanner(tree.traverseValues(tree.root));
			try {
				while (values.hasNext()) {
					if (values.next().matches("[0-9]+")) {
						valueStrings.add(values.next());
					}
				}
			} catch (NoSuchElementException e) {
			}
			try {
				while (userValues.hasNext()) {
					if (userValues.next().matches("[0-9]+")) {
						userValueStrings.add(userValues.next());
					}
				}
			} catch (NoSuchElementException e) {
			}
			ArrayList<Integer> valueInts = new ArrayList<Integer>();
			ArrayList<Integer> userValueInts = new ArrayList<Integer>();
			for (String value : valueStrings) {
				int val = Integer.parseInt(value);
				valueInts.add(val);
			}
			for (String value : userValueStrings) {
				int val = Integer.parseInt(value);
				userValueInts.add(val);
			}
			if (userValueInts.size() < valueInts.size()) {
				sim = cosineSimilarity(toDoubleArray(userValueInts), toDoubleArray(valueInts));
			} else {
				sim = cosineSimilarity(toDoubleArray(valueInts), toDoubleArray(userValueInts));
			}
			return sim;
		} else {
			userRAF.setLength(0);
			return similar(url, tree, 0);
		}

	}

	private static ArrayList<Double> toDoubleArray(ArrayList<Integer> array) {
		ArrayList<Double> doubleArray = new ArrayList<Double>();
		for (int i = 0; i < array.size(); i++) {
			doubleArray.add((double) array.get(i));
		}
		return doubleArray;
	}

	private static double cosineSimilarity(ArrayList<Double> vectorA, ArrayList<Double> vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.size(); i++) {
			dotProduct += vectorA.get(i) * vectorB.get(i);
			normA += Math.pow(vectorA.get(i), 2);
			normB += Math.pow(vectorB.get(i), 2);
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private ArrayList<BTree> addTrees(ArrayList<BTree> btrees) {
		btrees.add(sportsTree);
		btrees.add(musicTree);
		btrees.add(carsTree);
		btrees.add(moviesTree);
		btrees.add(videoGamesTree);
		btrees.add(foodTree);
		btrees.add(clothingTree);
		btrees.add(englishTree);
		btrees.add(healthTree);
		btrees.add(politicsTree);
		btrees.add(companiesTree);
		btrees.add(technologyTree);
		btrees.add(countriesTree);
		btrees.add(weatherTree);
		btrees.add(religonTree);
		btrees.add(animalsTree);
		btrees.add(scienceTree);
		btrees.add(booksTree);
		btrees.add(historyTree);
		btrees.add(lawsTree);

		return btrees;
	}

	private ArrayList<HashTable<String, BTree>> addTables(ArrayList<HashTable<String, BTree>> tables) {
		tables.add(sportsTable);
		tables.add(musicTable);
		tables.add(carsTable);
		tables.add(moviesTable);
		tables.add(videoGamesTable);
		tables.add(foodTable);
		tables.add(clothingTable);
		tables.add(englishTable);
		tables.add(healthTable);
		tables.add(politicsTable);
		tables.add(companiesTable);
		tables.add(technologyTable);
		tables.add(countriesTable);
		tables.add(weatherTable);
		tables.add(religonTable);
		tables.add(animalsTable);
		tables.add(scienceTable);
		tables.add(booksTable);
		tables.add(historyTable);
		tables.add(lawsTable);

		return tables;
	}

	private BTree parse(String url, BTree tree, int depth, HashTable<String, BTree> table) throws IOException {
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
					if (!tree.contains(word)) {
						tree.add(word, "1");
					} else {
						if (tree.getValue(word).matches("[0-9]+")) {
							tree.add(word, Integer.toString((Integer.parseInt(tree.getValue(word).trim()) + 1)));
						}
					}
				}
			}
			stringScanner.close();
			i++;
		}
		Elements pars = doc.select("p");
		for (Element par : pars) {
			Elements anchor = par.select("a");
			for (Element link : anchor) {
				String temp = link.attr("href");
				if (temp.startsWith("/wiki/")) {
					temp = "https://en.wikipedia.org" + temp;
				}
				if (temp.contains("https://en.wikipedia.org/wiki/") && !temp.contains("File")) {
					if (!table.contains(temp) && !temp.equals(url)) {
						table.put(temp, tree);
					}
				}
			}
		}
		ArrayList<String> keys = new ArrayList<String>();
		for (String k : table.keys()) {
			keys.add(k);
		}
		if (depth <= maxDepth) {
			depth++;
			parse(keys.get(depth - 1), tree, depth, table);
		}
		return tree;
	}

}