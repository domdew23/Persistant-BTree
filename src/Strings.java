import java.text.Collator;
import java.util.Comparator;

public final class Strings {

	private Strings() {
	}

	private static final Comparator<String> NATURAL_COMPARATOR_ASCII = new Comparator<String>() {
		public int compare(String o1, String o2) {
			return compareNaturalAscii(o1, o2);
		}
	};

	private static final Comparator<String> IGNORE_CASE_NATURAL_COMPARATOR_ASCII = new Comparator<String>() {
		public int compare(String o1, String o2) {
			return compareNaturalIgnoreCaseAscii(o1, o2);
		}
	};

	public static Comparator<String> getNaturalComparator() {
		Collator collator = Collator.getInstance();
		return getNaturalComparator(collator);
	}

	public static Comparator<String> getNaturalComparator(final Collator collator) {
		if (collator == null) {
			// it's important to explicitly handle this here - else the bug will
			// manifest anytime later in possibly
			// unrelated code that tries to use the comparator
			throw new NullPointerException("collator must not be null");
		}
		return new Comparator<String>() {
			public int compare(String o1, String o2) {
				return compareNatural(collator, o1, o2);
			}
		};
	}

	public static Comparator<String> getNaturalComparatorAscii() {
		return NATURAL_COMPARATOR_ASCII;
	}

	public static Comparator<String> getNaturalComparatorIgnoreCaseAscii() {
		return IGNORE_CASE_NATURAL_COMPARATOR_ASCII;
	}

	public static int compareNatural(String s, String t) {
		return compareNatural(s, t, false, Collator.getInstance());
	}

	public static int compareNatural(Collator collator, String s, String t) {
		return compareNatural(s, t, true, collator);
	}

	public static int compareNaturalAscii(String s, String t) {
		return compareNatural(s, t, true, null);
	}

	public static int compareNaturalIgnoreCaseAscii(String s, String t) {
		return compareNatural(s, t, false, null);
	}

	private static int compareNatural(String s, String t, boolean caseSensitive, Collator collator) {
		int sIndex = 0;
		int tIndex = 0;

		int sLength = s.length();
		int tLength = t.length();

		while (true) {
			// both character indices are after a subword (or at zero)

			// Check if one string is at end
			if (sIndex == sLength && tIndex == tLength) {
				return 0;
			}
			if (sIndex == sLength) {
				return -1;
			}
			if (tIndex == tLength) {
				return 1;
			}

			// Compare sub word
			char sChar = s.charAt(sIndex);
			char tChar = t.charAt(tIndex);

			boolean sCharIsDigit = Character.isDigit(sChar);
			boolean tCharIsDigit = Character.isDigit(tChar);

			if (sCharIsDigit && tCharIsDigit) {
				// Compare numbers

				// skip leading 0s
				int sLeadingZeroCount = 0;
				while (sChar == '0') {
					++sLeadingZeroCount;
					++sIndex;
					if (sIndex == sLength) {
						break;
					}
					sChar = s.charAt(sIndex);
				}
				int tLeadingZeroCount = 0;
				while (tChar == '0') {
					++tLeadingZeroCount;
					++tIndex;
					if (tIndex == tLength) {
						break;
					}
					tChar = t.charAt(tIndex);
				}
				boolean sAllZero = sIndex == sLength || !Character.isDigit(sChar);
				boolean tAllZero = tIndex == tLength || !Character.isDigit(tChar);
				if (sAllZero && tAllZero) {
					continue;
				}
				if (sAllZero && !tAllZero) {
					return -1;
				}
				if (tAllZero) {
					return 1;
				}

				int diff = 0;
				do {
					if (diff == 0) {
						diff = sChar - tChar;
					}
					++sIndex;
					++tIndex;
					if (sIndex == sLength && tIndex == tLength) {
						return diff != 0 ? diff : sLeadingZeroCount - tLeadingZeroCount;
					}
					if (sIndex == sLength) {
						if (diff == 0) {
							return -1;
						}
						return Character.isDigit(t.charAt(tIndex)) ? -1 : diff;
					}
					if (tIndex == tLength) {
						if (diff == 0) {
							return 1;
						}
						return Character.isDigit(s.charAt(sIndex)) ? 1 : diff;
					}
					sChar = s.charAt(sIndex);
					tChar = t.charAt(tIndex);
					sCharIsDigit = Character.isDigit(sChar);
					tCharIsDigit = Character.isDigit(tChar);
					if (!sCharIsDigit && !tCharIsDigit) {
						// both number sub words have the same length
						if (diff != 0) {
							return diff;
						}
						break;
					}
					if (!sCharIsDigit) {
						return -1;
					}
					if (!tCharIsDigit) {
						return 1;
					}
				} while (true);
			} else {
				// Compare words
				if (collator != null) {
					// To use the collator the whole subwords have to be
					// compared - character-by-character comparision
					// is not possible. So find the two subwords first
					int aw = sIndex;
					int bw = tIndex;
					do {
						++sIndex;
					} while (sIndex < sLength && !Character.isDigit(s.charAt(sIndex)));
					do {
						++tIndex;
					} while (tIndex < tLength && !Character.isDigit(t.charAt(tIndex)));

					String as = s.substring(aw, sIndex);
					String bs = t.substring(bw, tIndex);
					int subwordResult = collator.compare(as, bs);
					if (subwordResult != 0) {
						return subwordResult;
					}
				} else {
					// No collator specified. All characters should be ascii
					// only. Compare character-by-character.
					do {
						if (sChar != tChar) {
							if (caseSensitive) {
								return sChar - tChar;
							}
							sChar = Character.toUpperCase(sChar);
							tChar = Character.toUpperCase(tChar);
							if (sChar != tChar) {
								sChar = Character.toLowerCase(sChar);
								tChar = Character.toLowerCase(tChar);
								if (sChar != tChar) {
									return sChar - tChar;
								}
							}
						}
						++sIndex;
						++tIndex;
						if (sIndex == sLength && tIndex == tLength) {
							return 0;
						}
						if (sIndex == sLength) {
							return -1;
						}
						if (tIndex == tLength) {
							return 1;
						}
						sChar = s.charAt(sIndex);
						tChar = t.charAt(tIndex);
						sCharIsDigit = Character.isDigit(sChar);
						tCharIsDigit = Character.isDigit(tChar);
					} while (!sCharIsDigit && !tCharIsDigit);
				}
			}
		}
	}
}
