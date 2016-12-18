import java.util.LinkedList;

public class CosineSimilarity {

	public class Values {
		int val1;
		int val2;

		Values(int v1, int v2) {
			this.val1 = v1;
			this.val2 = v2;
		}

		public void Update_VAl(int v1, int v2) {
			this.val1 = v1;
			this.val2 = v2;
		}
	}

	public double getSimilarity(String Text1, String Text2) {
		double score = 0.0000000;
		// Identify distinct words from both documents
		String[] text1 = Text1.split(" ");
		String[] text2 = Text2.split(" ");
		HashTable<String, Values> wordFreq = new HashTable<String, CosineSimilarity.Values>();
		LinkedList<String> words = new LinkedList<String>();

		// prepare word frequency vector by using Text1
		for (int i = 0; i < text1.length; i++) {
			String temp = text1[i].trim();
			if (temp.length() > 0) {
				if (wordFreq.contains(temp)) {
					Values vals1 = wordFreq.get(temp);
					int freq1 = vals1.val1 + 1;
					int freq2 = vals1.val2;
					vals1.Update_VAl(freq1, freq2);
					wordFreq.put(temp, vals1);
				} else {
					Values vals1 = new Values(1, 0);
					wordFreq.put(temp, vals1);
					words.add(temp);
				}
			}
		}

		// prepare word frequency vector by using Text2
		for (int i = 0; i < text2.length; i++) {
			String temp = text2[i].trim();
			if (temp.length() > 0) {
				if (wordFreq.contains(temp)) {
					Values vals1 = wordFreq.get(temp);
					int freq1 = vals1.val1;
					int freq2 = vals1.val2 + 1;
					vals1.Update_VAl(freq1, freq2);
					wordFreq.put(temp, vals1);
				} else {
					Values vals1 = new Values(0, 1);
					wordFreq.put(temp, vals1);
					words.add(temp);
				}
			}
		}

		// calculate the cosine similarity
		double VectAB = 0.0000000;
		double VectA = 0.0000000;
		double VectB = 0.0000000;

		for (int i = 0; i < words.size(); i++) {
			Values vals12 = wordFreq.get(words.get(i));

			double freq1 = (double) vals12.val1;
			double freq2 = (double) vals12.val2;

			VectAB = VectAB + (freq1 * freq2);

			VectA = VectA + freq1 * freq1;
			VectB = VectB + freq2 * freq2;
		}
		score = ((VectAB) / (Math.sqrt(VectA) * Math.sqrt(VectB)));

		return (score);
	}
}
