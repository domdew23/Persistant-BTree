import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

public class BTree {

	public Node root;
	RandomAccessFile file;
	public int treeSize = 0;
	static int nodeArity = 12;
	static int order = nodeArity;
	static int offset = 16;
	static int emptyNodePointer = 8;
	static int keyLength = 32;
	static int keySize = keyLength * nodeArity;
	static int valueLength = 293;
	static int valueSize = valueLength * nodeArity;
	static int nodeSize = 4096;
	static int parentSize = 8;
	static int numElementsSize = 4;
	static int PAD = nodeSize - parentSize - numElementsSize - keySize - valueSize;
	public LinkedList<Long> emptyNodes = new LinkedList<Long>();
	public HashTable<String, Node> nodeCache;

	public BTree(RandomAccessFile file) throws IOException {
		nodeCache = new HashTable<String, Node>(200) {

			private static final int MAX_ENTRIES = 1000;
		};

		this.file = file;
		try {
			if (file.length() == 0) {
				// create the file
				createFile();
				// create the root node
			} else {
				// read in the file
				root = new Node();
				root = root.read(offset);
			}
		} catch (EOFException e) {
			System.out.println("Got one");
		}
	}

	public boolean add(String key, String data) {
		boolean b = nativeAdd(key, data);
		if (b) {
			this.treeSize++;
			return true;
		} else {
			return false;
		}
	}

	private boolean nativeAdd(String key, String data) {
		try {
			if (key != null) {
				if (isRootNull()) {
					root = new Node(key, data);// create a new root node
					root.parent = -1;
					file.seek(offset);
					root.commit(root, -1);
					return true;
				}
				boolean needSplit = root.set(key, data);
				if (!needSplit) {
					file.seek(offset);
					root.commit(root, -1);
					return true;
				} else {
					root.commit(root, -1);
					root.splitRoot();
					root.commit(root, -1);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void createFile() {
		try {
			file.seek(0);
			// create the RandomAccessFile
			// first set the tree size to zero
			file.writeInt(treeSize);

			// then set the NODE arity of the tree (amount of data entries per
			// node)
			file.writeInt(nodeArity);

			// then set the first empty node (one that has been removed)
			file.writeLong(-1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRootNull() throws Exception {
		return file.length() <= offset;
	}

	public int getSize() {
		return this.treeSize;
	}

	public boolean contains(String key) {
		return this.searchForNode(key) != null;
	}

	public Node searchForNode(String key) {
		if (root == null && key != null) {
			return null;
		}
		return root.search(key);
	}

	public String getValue(String key) {
		if (root == null && key != null) {
			return null;
		}
		return root.getValue(key);
	}

	public static void print(Node node, int level) {
		if (node != null) {
			System.out.println(level + ": key: " + node.getKeysAsString() + " value: " + node.getValuesAsString());
			level++;
			for (int i = 0; i < node.getNumChildren(); i++) {
				print(node.getChild(i), level);
			}
		}
	}

	public String traverseKeys(Node node) {
		StringBuilder buf = new StringBuilder();
		if (node != null) {
			if (!node.hasChildren()) {
				buf.append(node.getKeysAsString());
			}
			for (int i = 0; i < node.getNumChildren(); ++i) {
				buf.append(traverseKeys(node.getChild(i)) + "\n");
			}
		}
		return buf.toString();

	}

	public String traverseValues(Node node) {
		StringBuilder buf = new StringBuilder();
		buf.append("\n");
		if (node != null) {
			if (!node.hasChildren()) {
				buf.append(node.getValuesAsString());
			}

			for (int i = 0; i < node.getNumChildren(); ++i) {
				buf.append(traverseValues(node.getChild(i)) + "\n");
			}
		}
		return buf.toString();
	}

	private class Node {

		List<String> keys;
		List<String> values;
		long parent;

		public Node(String key, String value) {
			this.keys = new LinkedList<String>();
			this.values = new LinkedList<String>();
			this.keys.add(key);
			this.values.add(value);
		}

		public Node() {
			this.parent = -666;
			this.keys = new LinkedList<String>();
			this.values = new LinkedList<String>();
		}

		public long popEmptyNodePointer() throws Exception {
			file.seek(emptyNodePointer);
			long emptyPointer = file.readLong();
			if (emptyPointer == -1) {
				return -1;
			} else {
				Node topNode = this.getNode(emptyPointer);
				if (!topNode.values.get(0).equals("$END")) {
					String nodePointer = topNode.values.get(0);
					long nextEmptyNode = this.getPointerLocation(nodePointer);
					file.seek(emptyNodePointer);
					file.writeLong(nextEmptyNode);
				} else {
					file.seek(emptyNodePointer);
					file.writeLong(-1);
				}
				return emptyPointer;
			}
		}

		private boolean tooBig(String key) {
			byte[] bytes = key.getBytes();
			if (bytes.length > keyLength) {
				return true;
			} else
				return false;
		}

		public void commit(Node node, long seek) {
			try {
				// write the parent pointer to the file
				if (node.parent == -1 || seek == -1) {
					file.seek(offset);
					// write parent
					file.writeLong(-1);
				} else {
					file.seek(seek);
					// write parent
					file.writeLong(node.parent);
				}
				// write the number of elements
				int numElements = node.keys.size();
				file.writeInt(numElements);
				// key array
				// write all the keys
				for (String s : node.keys) {
					if (!tooBig(s)) {
						byte[] bytes = s.getBytes();
						file.write(bytes);
						// needed to pad the key
						int tmpPad = keyLength - bytes.length;
						try {
							byte[] buffer1 = new byte[tmpPad];

							for (int i = 0; i < tmpPad; i++) {
								buffer1[i] = ' ';
							}
							file.write(buffer1);
						} catch (NegativeArraySizeException e) {
							System.out.println("Exception where: " + "\n" + "Temp Pad: " + tmpPad + "\n"
									+ "Key Length: " + keyLength + "\n" + "Bytes Length: " + bytes.length);
						}
					}
				}
				// pad the end of the keys
				int padNumBytes = (nodeArity - numElements) * keyLength;
				byte[] buffer2 = new byte[padNumBytes];
				for (int i = 0; i < padNumBytes; i++) {
					buffer2[i] = ' ';
				}
				file.write(buffer2);
				// write all the values
				for (String s : node.values) {
					byte[] bytes = s.getBytes();
					file.write(bytes);
					// needed to pad the key
					int tmpPad = valueLength - bytes.length;
					byte[] buffer3 = new byte[tmpPad];
					for (int i = 0; i < tmpPad; i++) {
						buffer3[i] = ' ';
					}
					file.write(buffer3);
				}
				// pad the end of the values
				int padNumBytes2 = (nodeArity - numElements) * valueLength;
				byte[] buffer4 = new byte[padNumBytes2];
				for (int i = 0; i < padNumBytes2; i++) {
					buffer4[i] = ' ';
				}
				file.write(buffer4);
				// pad the end of the node to make the node = 4096 bytes (4k)
				byte[] buffer5 = new byte[PAD];
				for (int i = 0; i < PAD; i++) {
					buffer5[i] = ' ';
				}
				file.write(buffer5);
				nodeCache.put(seek + "", node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Node read(long seek) {
			try {
				Node tmp = new Node();
				file.seek(seek);
				// read and set parent
				tmp.parent = file.readLong();
				// read number of elements
				int numElements = file.readInt();
				long pointer = file.getFilePointer();
				// read and set keys
				for (int i = 0; i < numElements; i++) {
					byte[] inBytes = new byte[keyLength];
					file.readFully(inBytes, 0, keyLength);
					String s = new String(inBytes);
					// String s = file.readLine();
					s = s.trim();
					tmp.keys.add(s);
				}
				// seek to the end of keys
				long valuePointer = pointer + keySize;
				file.seek(valuePointer);
				// read the values
				for (int i = 0; i < numElements; i++) {
					byte[] inBytes = new byte[valueLength];
					file.readFully(inBytes, 0, valueLength);
					String s = new String(inBytes);
					s = s.trim();
					tmp.values.add(s);
				}
				return tmp;
			} catch (IOException e) {
				return null;
			}

		}

		public Node getNode(long address) {
			Node n;
			if (nodeCache.contains(address + "")) {
				n = nodeCache.get(address + "");
			} else {
				n = this.read(address);
			}
			return n;
		}

		public Node search(String key) {
			try {
				if (this.hasChildren()) {
					int ks = this.keys.size();
					for (int i = 0; i < ks; i++) {
						if (key.equals(this.keys.get(i))) {
							// if the key is equal to the one in the list
							long nPointerLocation = this.getPointerLocation(this.values.get(i + 1));
							Node n = this.getNode(nPointerLocation);
							return n.search(key);
						}
						if (this.keys.get(i) == null || this.keys.get(i).equals("null")) {
							break;
						}
						int cp = Strings.compareNatural(key, this.keys.get(i));
						// if a key is found in the list thats greater then the
						// key
						// to be added
						if (cp < 0) {
							long nPointerLocation = this.getPointerLocation(this.values.get(i));
							Node n = this.getNode(nPointerLocation);
							return n.search(key);
						}
					}
					// if the key wasnt found in the list or is greater then do
					// something
					long nPointerLocation = this.getPointerLocation(this.values.get(this.keys.size() - 1));
					Node n = this.getNode(nPointerLocation);
					return n.search(key);
				} else {
					int ks = this.keys.size();
					for (int i = 0; i < ks; i++) {
						if (key.equals(this.keys.get(i))) {
							// if the key is equal to the one in the list
							return this;
						}
						int cp = Strings.compareNatural(key, this.keys.get(i));
						// if a key is found in the list thats greater then the
						// key
						// to be added
						if (cp < 0) {
							return null;
						}
					}
				}
			} catch (IndexOutOfBoundsException e) {
				// if the key wasnt found in the list then do something
			}
			return null;
		}

		public boolean hasChildren() {
			if (this.values.size() == 0) {
				return false;
			} else {
				// if the first value is an address then it has children
				if (this.values.get(0).startsWith("$")) {
					return true;
				} else {
					return false;
				}
			}
		}

		public void splitRoot() {
			boolean hadChildren = hasChildren();

			boolean fix = true;
			Node left = new Node();
			Node right = new Node();

			long leftPointer = this.getNextAvailPointer();
			nodeCache.put(leftPointer + "", left);
			long rightPointer = this.getNextAvailPointer();
			// create the right and left nodes on disk
			nodeCache.put(rightPointer + "", right);
			int half = (int) Math.ceil(1.0 * BTree.order / 2);
			int size = this.keys.size();
			for (int i = 0; i < size; i++) {
				if (i < half) {
					left.set(this.keys.remove(0), this.values.remove(0));
				} else {
					right.set(this.keys.remove(0), this.values.remove(0));
				}
			}
			if (hadChildren) {
				fix = false;
				// need to set the left seek location
				this.set(left.keys.set(left.keys.size() - 1, "null"), this.createPointerLocation(leftPointer));
			}
			left.parent = offset;
			right.parent = offset;
			if (fix) {
				this.set(right.keys.get(0), this.createPointerLocation(leftPointer));
			}
			// thinking about setting all the nulls to "$null"
			this.set("null", this.createPointerLocation(rightPointer));
			// now commit again
			this.commit(left, leftPointer);
			this.commit(right, rightPointer);
		}

		private String createPointerLocation(long pointer) {
			return "$" + pointer;
		}

		private long getPointerLocation(String pointer) {
			// returns -99 if the inputed string is not a pointer to a node
			if (pointer.startsWith("$")) {
				return Long.parseLong(pointer.substring(1));
			} else {
				return -99;
			}
		}

		private long getNextAvailPointer() {
			try {
				long nextPointer = this.popEmptyNodePointer();
				if (nextPointer == -1) {
					long length = file.length();
					if (!nodeCache.contains(length + "")) {
						return length;
					} else {
						while (nodeCache.contains(length + "")) {
							length = length + nodeSize;
						}
					}
					return length;
				} else {
					return nextPointer;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}

		private void splitLeafNode(Node right) {
			Node left = new Node();
			long leftPointer = this.getNextAvailPointer();
			nodeCache.put(leftPointer + "", left);
			int half = (int) Math.ceil(1.0 * BTree.order / 2);
			for (int i = 0; i < half; i++) {
				left.set(right.keys.remove(0), right.values.remove(0));
			}
			// the parent pointer was a pain so i just left it out except for
			// root
			this.set(right.keys.get(0), this.createPointerLocation(leftPointer));
			this.commit(left, leftPointer);
		}

		private void splitInternalNode(Node right) {
			Node left = new Node();
			long leftPointer = this.getNextAvailPointer();
			nodeCache.put(leftPointer + "", left);
			int half = (int) Math.ceil(1.0 * BTree.order / 2);
			for (int i = 0; i < half; i++) {
				left.set(right.keys.remove(0), right.values.remove(0));
			}
			// the parent pointer was a pain so i just left it out except for
			// root
			String s = left.keys.set(left.keys.size() - 1, "null");
			this.set(s, this.createPointerLocation(leftPointer));
			this.commit(left, leftPointer);
		}

		private boolean needToSplit() {
			return this.keys.size() > BTree.order - 1;
		}

		private void splitChild(Node n) {
			if (!n.hasChildren()) {
				// do split leaf
				this.splitLeafNode(n);
			} else {
				// do split internal
				this.splitInternalNode(n);
			}
		}

		public String getKeysAsString() {
			String tmp = "";
			StringBuilder buf = new StringBuilder();
			for (String s : this.keys) {
				buf.append(s + "\n");
			}
			tmp = buf.toString();
			return tmp;
		}

		public String getValuesAsString() {
			String tmp = "";
			StringBuilder buf = new StringBuilder();
			for (String s : this.values) {
				buf.append(s + "\n");
			}
			tmp = buf.toString();
			return tmp;
		}

		public String getValue(String key) {
			Node n = this.search(key);
			for (int i = 0; i < n.keys.size(); i++) {
				if (key.equals(n.keys.get(i))) {
					return (String) n.values.get(i);
				}
			}
			return null;
		}

		public int getNumChildren() {
			return this.values.size();
		}

		public Node getChild(int i) {
			if (this.hasChildren()) {
				long nPointerLocation = this.getPointerLocation(this.values.get(i));
				Node n = this.getNode(nPointerLocation);
				return n;
			} else {
				return null;
			}
		}

		public boolean set(String key, String value) {
			if (this.keys.size() == 0) {
				this.keys.add(key);
				this.values.add(value);
				return this.needToSplit();
			} else {
				if (!this.hasChildren()) {
					int ks = this.keys.size();
					for (int i = 0; i < ks; i++) {
						if (key.equals("null")) {
							this.keys.add(key);
							this.values.add(value);
							return this.needToSplit();
						}
						int cp = Strings.compareNatural(key, this.keys.get(i));
						// if its already there replace it
						if (key.equals(this.keys.get(i))) {
							this.keys.set(i, key);
							this.values.set(i, value);
							return this.needToSplit();
						}
						// if a key is found in the list thats greater then the
						// key to be added
						if (cp < 0) {
							this.keys.add(i, key);
							this.values.add(i, value);
							return this.needToSplit();
						}
					}
					// if none of the current keys are greater add at the end
					this.keys.add(key);
					this.values.add(value);
					return this.needToSplit();
				} else {
					// adds to internal nodes
					if (value.startsWith("$")) {
						if (key.equals("null")) {
							// +if null is allready there then overwrite it
							if (this.keys.get(this.keys.size() - 1).equals("null")) {
								this.keys.set(this.keys.size() - 1, "null");
								this.values.set(this.values.size() - 1, value);
								return this.needToSplit();
							} else {
								this.keys.add("null");
								this.values.add(value);
								// check to see if internal needs to split
								return this.needToSplit();
							}
						}
						int ks = this.keys.size();
						for (int i = 0; i < ks; i++) {
							if (!this.keys.get(i).equals("null")) {
								int cp = Strings.compareNatural(key, this.keys.get(i));
								if (key.equals(this.keys.get(i))) {
									this.keys.set(i, key);
									this.values.set(i, value);
									return this.needToSplit();
								}
								// if a key is found in the list thats greater
								// then the key to be added
								if (cp < 0) {
									this.keys.add(i, key);
									this.values.add(i, value);
									return this.needToSplit();
								}
							}
						}
						// if none of the current keys are greater add at the
						// end (but before null)
						int ksize = this.keys.size();
						for (int i = 0; i < ksize; i++) {
							if (this.keys.get(i).equals("null")) {
								this.keys.add(this.keys.size() - 1, key);
								this.values.add(this.values.size() - 1, value);
								return this.needToSplit();
							}
						}
						this.keys.add(key);
						this.values.add(value);
						return this.needToSplit();

					} else {
						// if its adding a value (not adding a node to the
						// values list in internal)
						int ks = this.keys.size();
						for (int i = 0; i < ks - 1; i++) {
							int cp = Strings.compareNatural(key, this.keys.get(i));
							if (key.equals(this.keys.get(i))) {
								// go over one and go down all the way to left
								long nPointerLocation = this.getPointerLocation(this.values.get(i + 1));
								Node n = this.getNode(nPointerLocation);
								if (n.set(key, value)) {
									this.splitChild(n);
									this.commit(n, nPointerLocation);
									return this.needToSplit();
								} else {
									this.commit(n, nPointerLocation);
									return this.needToSplit();
								}
							}
							if (cp < 0) {
								long nPointerLocation = this.getPointerLocation(this.values.get(i));
								Node n = this.getNode(nPointerLocation);
								if (n.set(key, value)) {
									this.splitChild(n);
									this.commit(n, nPointerLocation);
									return this.needToSplit();
								} else {
									this.commit(n, nPointerLocation);
									return this.needToSplit();
								}
							}
						}

						// if none of the keys in the list are greater go down
						// the null key's child
						long nPointerLocation = this.getPointerLocation(this.values.get(this.keys.size() - 1));
						Node n = this.getNode(nPointerLocation);
						if (n.set(key, value)) {
							this.splitChild(n);
							this.commit(n, nPointerLocation);
							return this.needToSplit();
						} else {
							this.commit(n, nPointerLocation);
							return this.needToSplit();
						}
					}
				}
			}
		}

	}
}