import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Word<Key, Value> implements Serializable {

	private int pairs;
	private Node first;

	private class Node implements Serializable {
		private Key key;
		private Value value;
		private Node next;
		private long currentMarker = 0;

		public Node(Key key, Value value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
	}

	public Word() {

	}

	public int size() {
		return pairs;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(Key key) {
		return get(key) != null;
	}

	public Value get(Key key) {
		for (Node x = first; x != null; x = x.next) {
			if (key.equals(x.key)) {
				return x.value;
			}
		}
		return null;
	}

	// if you find it return true

	// if we’ve seen it before skip it

	// else for each edge recurse

	public void put(Key key, Value value) {
		if (value == null) {
			delete(key);
			return;
		}

		for (Node x = first; x != null; x = x.next) {
			if (key.equals(x.key)) {
				x.value = value;
				return;
			}
		}
		first = new Node(key, value, first);
		pairs++;
	}

	public void delete(Key key) {
		first = delete(first, key);
	}

	private Node delete(Node x, Key key) {
		if (x == null) {
			return null;
		}

		if (key.equals(x.key)) {
			pairs--;
			return x.next;
		}
		x.next = delete(x.next, key);
		return x;
	}

	public Iterable<Key> keys() {
		Queue<Key> queue = new LinkedList<Key>();
		for (Node x = first; x != null; x = x.next)
			queue.add(x.key);
		return queue;
	}
}
