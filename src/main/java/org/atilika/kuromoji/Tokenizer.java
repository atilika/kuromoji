/**
 * Copyright © 2010-2011 Atilika Inc.  All rights reserved.
 *
 * See the NOTICE.txt file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.atilika.kuromoji;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.atilika.kuromoji.dict.ConnectionCosts;
import org.atilika.kuromoji.dict.Dictionary;
import org.atilika.kuromoji.dict.TokenInfoDictionary;
import org.atilika.kuromoji.dict.UnknownDictionary;
import org.atilika.kuromoji.dict.UserDictionary;
import org.atilika.kuromoji.trie.DoubleArrayTrie;
import org.atilika.kuromoji.viterbi.Viterbi;
import org.atilika.kuromoji.viterbi.ViterbiNode;
import org.atilika.kuromoji.viterbi.ViterbiNode.Type;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class Tokenizer {
	public enum Mode {
		NORMAL, SEARCH, EXTENDED
	}

	private final Viterbi viterbi;
	
	private final EnumMap<Type, Dictionary> dictionaryMap = new EnumMap<Type, Dictionary>(Type.class);
	
	/**
	 * 
	 * @param dictionary
	 * @param costs
	 * @param trie
	 * @param unkDictionary
	 * @param userDictionary
	 * @param mode
	 */
	protected Tokenizer(TokenInfoDictionary dictionary, ConnectionCosts costs, DoubleArrayTrie trie, UnknownDictionary unkDictionary, UserDictionary userDictionary, Mode mode) {
		viterbi = new Viterbi(trie, dictionary, unkDictionary, costs, userDictionary, mode);
		dictionaryMap.put(Type.KNOWN, dictionary);
		dictionaryMap.put(Type.UNKNOWN, unkDictionary);
		dictionaryMap.put(Type.USER, userDictionary);
	}


	/**
	 * Tokenize input text
	 * @param text
	 * @return list of Token
	 */
	public List<Token> tokenize(String text) {

		List<Integer> splitPositions = getSplitPositions(text);

		if(splitPositions.size() == 0) {
			return doTokenize(0, text);
		}
		
		ArrayList<Token> result = new ArrayList<Token>();
		int offset = 0;
		for(int position : splitPositions) {
			result.addAll(doTokenize(offset, text.substring(offset, position + 1)));
			offset = position + 1;
		}
		
		if(offset < text.length()) {
			result.addAll(doTokenize(offset, text.substring(offset)));
		}
		
		return result;
	}
	
	/**
	 * Split input text at 句読点, which is 。 and 、
	 * @param text
	 * @return list of split position
	 */
	private List<Integer> getSplitPositions(String text) {
		ArrayList<Integer> splitPositions = new ArrayList<Integer>();
		
		int position = 0;
		int currentPosition = 0;

		while(true) {
			int indexOfMaru = text.indexOf("。", currentPosition);
			int indexOfTen = text.indexOf("、", currentPosition);
			
			if(indexOfMaru < 0 || indexOfTen < 0) {
				position = Math.max(indexOfMaru, indexOfTen);;
			} else {
				position = Math.min(indexOfMaru, indexOfTen);				
			}
			
			if(position >= 0) {
				splitPositions.add(position);
				currentPosition = position + 1;
			} else {
				break;
			}
		}
		
		return splitPositions;
	}
	
	/**
	 * Tokenize input sentence.
	 * @param offset offset of sentence in original input text
	 * @param sentence sentence to tokenize
	 * @return list of Token
	 */
	private List<Token> doTokenize(int offset, String sentence) {
		ArrayList<Token> result = new ArrayList<Token>();
		
		ViterbiNode[][][] lattice = viterbi.build(sentence);
		List<ViterbiNode> bestPath = viterbi.search(lattice);
		for(ViterbiNode node : bestPath) {
			int wordId = node.getWordId();
			if(node.getType() == Type.KNOWN && wordId == 0){ // Do not include BOS/EOS 
				continue;
			}
			
			Token token = new Token(wordId, node.getSurfaceForm(), node.getType(), offset + node.getStartIndex(), dictionaryMap.get(node.getType()));	// Pass different dictionary based on the type of node
			result.add(token);
		}
		
		return result;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private static TokenInfoDictionary dictionary = null;
		private static ConnectionCosts costs = null;
		private static DoubleArrayTrie trie = null;
		private static UnknownDictionary unkDictionary = null;
		private Mode mode = Mode.NORMAL;
		private UserDictionary userDictionary = null;
		private static boolean initialized = false;

		protected Builder() {
			loadDictionaries();
		}
		
		private static synchronized void loadDictionaries() {
			if(initialized == true) {
				return;
			}
			
			try {
				dictionary = TokenInfoDictionary.getInstance();
				costs = ConnectionCosts.getInstance();
				trie = DoubleArrayTrie.getInstance();
				unkDictionary = UnknownDictionary.getInstance();
				initialized = true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public synchronized Builder mode(Mode mode) {
			this.mode = mode;
			return this;
		}
		
		public synchronized Builder userDictionary(InputStream userDictionaryInputStream) {
			if (userDictionaryInputStream != null) {
				try {
					userDictionary = UserDictionary.read(userDictionaryInputStream);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return this;
		}
		
		public synchronized Builder userDictionary(String userDicitonaryPath) {
            if(userDicitonaryPath != null && userDicitonaryPath.length() > 0) {
            	try {
					userDictionary(new BufferedInputStream(new FileInputStream(userDicitonaryPath)));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            
            return this;
		}
		
		public Tokenizer build() {
			return new Tokenizer(dictionary, costs, trie, unkDictionary, userDictionary, mode);
		}
	}
}
