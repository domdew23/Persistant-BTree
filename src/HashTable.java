import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class HashTable<Key, Value> implements Serializable {

	private static final int startingCapacity = 4;
	private Word<Key, Value>[] word;
	// private LinkedList<Key> word;
	// Keeps track of the size of the table and frequencies
	private int pairs;
	private int size;

	public HashTable() {
		this(startingCapacity);
	}

	@SuppressWarnings("unchecked")
	// Constructor
	public HashTable(int size) {

		this.size = size;

		// new linked list size of table
		word = (Word<Key, Value>[]) new Word[size];

		for (int i = 0; i < size; i++) {

			word[i] = new Word<Key, Value>();
		}
	}

	public int hash(Key key) {

		return (key.hashCode() & 0x7fffffff) % size;
	}

	public int amountOfKeys() {
		int count = 0;
		for (Key key : this.keys()) {
			count++;
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public void put(Key key, Value value) {

		if (key == null) {
			throw new NullPointerException("first argument to put() is null");
		}
		if (value == null) {
			delete(key);
			return;
		}
		if (pairs >= 10 * size) {
			resize(2 * size);
		}

		// handle collisions
		int i = hash(key);
		if (!word[i].contains(key)) {
			pairs++;
			word[i].put(key, value);
		} else {
			if (value instanceof Integer) {
				value = get(key);
				Integer val = new Integer(((Integer) value) + 1);
				value = (Value) val;
				word[i].put(key, value);
			} else {
				delete(key);
			}
		}

	}

	public void delete(Key key) {
		if (key == null) {
			throw new NullPointerException("first argument to put() is null");
		}

		int i = hash(key);
		if (word[i].contains(key)) {
			pairs--;
			word[i].delete(key);
		}

		if (size > startingCapacity && pairs <= 2 * size) {
			resize(size / 2);
		}

	}

	// makes a list of keys
	public Iterable<Key> keys() {
		Queue<Key> queue = new LinkedList<Key>();
		for (int i = 0; i < size; i++) {
			for (Key key : word[i].keys())
				queue.add(key);
		}
		return queue;
	}

	private void resize(int chains) {
		HashTable<Key, Value> temp = new HashTable<Key, Value>(chains);
		for (int i = 0; i < size; i++) {
			for (Key key : word[i].keys()) {
				temp.put(key, word[i].get(key));
			}
		}

		this.size = temp.size;
		this.pairs = temp.pairs;
		this.word = temp.word;
	}

	public Value get(Key key) {
		if (key == null) {
			throw new NullPointerException("null");
		}
		int i = hash(key);
		return word[i].get(key);
	}

	public void printHash() {
		for (Key k : keys()) {
			int i = hash(k);
			System.out.println("##########");
			System.out.println(word[i].keys());
			System.out.println(i);
			System.out.println("##########");

		}
	}

	public boolean contains(Key key) {
		if (key == null) {
			throw new NullPointerException("null");
		}

		return get(key) != null;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return size;
	}
}