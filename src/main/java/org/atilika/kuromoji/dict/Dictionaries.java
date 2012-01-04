/**
 * Copyright © 2010-2012 Atilika Inc.  All rights reserved.
 *
 * Atilika Inc. licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License.  A copy of the License is distributed with this work in the
 * LICENSE.txt file.  You may also obtain a copy of the License from
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.atilika.kuromoji.dict;

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
			throw new RuntimeException("Could not load dictionaries!  Ouch, ouch, ouch...", ex);
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
