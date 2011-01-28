/**
 * 
 */
package org.atilika.kuromoji.dict;

import javax.management.RuntimeErrorException;

import org.atilika.kuromoji.trie.DoubleArrayTrie;

public final class Dictionaries {

	private static TokenInfoDictionary dictionary;

	private static UnknownDictionary unknownDictionary;

	private static ConnectionCosts costs;

	private static DoubleArrayTrie trie;
	
	private static boolean initialized = false;
	
	static {
		load();
	}

	private static synchronized void load() {

		if (Dictionaries.initialized) {
			return;
		}

		try {
			Dictionaries.dictionary = TokenInfoDictionary.getInstance();
			Dictionaries.unknownDictionary = UnknownDictionary.getInstance();
			Dictionaries.costs = ConnectionCosts.getInstance();
			Dictionaries.trie = DoubleArrayTrie.getInstance();
			Dictionaries.initialized = true;
		} catch (Exception ex) {
			throw new RuntimeErrorException(new Error(ex.getMessage(), ex),
											"Could not load dictionaries!  Ouch, ouch, ouch...");
		}
	}

	/**
	 * @return the dictionary
	 */
	public static TokenInfoDictionary getDictionary() {
		return dictionary;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public static void setDictionary(TokenInfoDictionary dictionary) {
		Dictionaries.dictionary = dictionary;
	}

	/**
	 * @return the unknownDictionary
	 */
	public static UnknownDictionary getUnknownDictionary() {
		return unknownDictionary;
	}

	/**
	 * @param unknownDictionary the unknownDictionary to set
	 */
	public static void setUnknownDictionary(UnknownDictionary unknownDictionary) {
		Dictionaries.unknownDictionary = unknownDictionary;
	}

	/**
	 * @return the costs
	 */
	public static ConnectionCosts getCosts() {
		return costs;
	}

	/**
	 * @param costs the costs to set
	 */
	public static void setCosts(ConnectionCosts costs) {
		Dictionaries.costs = costs;
	}

	/**
	 * @return the trie
	 */
	public static DoubleArrayTrie getTrie() {
		return trie;
	}

	/**
	 * @param trie the trie to set
	 */
	public static void setTrie(DoubleArrayTrie trie) {
		Dictionaries.trie = trie;
	}
}
