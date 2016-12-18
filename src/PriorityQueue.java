
public class PriorityQueue {
	Node[] keys;
	int size;

	public PriorityQueue(int max) {
		keys = new Node[max + 1];
		size = 0;
	}

	public void insert(Node k) {
		keys[++size] = k;
		check(size);
	}

	private int find(Node n) {
		int i = 0;
		for (Node node : keys) {
			if (n.equals(node)) {
				return i;
			}
			i++;
		}
		System.out.println("Node: " + n.getID() + " was not found");
		return -1;
	}

	public void decreaseKey(Node node) {
		keys[find(node)] = node;
	}

	private void check(int k) {
		while (k > 1 && greater(k / 2, k)) {
			siftUp(k, k / 2);
			k = k / 2;
		}
	}

	private boolean greater(int i, int j) {
		return keys[i].best > keys[j].best;
	}

	private void siftUp(int i, int j) {
		Node temp = keys[i];
		keys[i] = keys[j];
		keys[j] = temp;
	}

	private void siftDown(int k) {
		while (2 * k <= size) {
			int j = 2 * k;
			if (j < size && greater(j, j + 1)) {
				j++;
			}
			if (!greater(k, j)) {
				break;
			}
			siftUp(k, j);
			k = j;
		}
	}

	public Node remove() {
		siftUp(1, size);
		Node min = keys[size--];
		siftDown(1);
		return min;
	}

	public boolean contains(Node x) {
		for (Node n : keys) {
			if (x.equals(n)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return size == 0;
	}
}
