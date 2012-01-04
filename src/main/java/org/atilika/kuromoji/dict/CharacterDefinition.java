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

import java.io.Serializable;
import java.util.EnumMap;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public final class CharacterDefinition implements Serializable {
	private static final long serialVersionUID = -1436753619176638532L;
	
	private final CharacterClass[] characterCategoryMap = new CharacterClass[65536];

	private final EnumMap<CharacterClass, int[]> invokeDefinitionMap =
		new EnumMap<CharacterClass, int[]>(CharacterClass.class); // invoke, group, length

	public enum CharacterClass {
		NGRAM, DEFAULT, SPACE, SYMBOL, NUMERIC, ALPHA, CYRILLIC, GREEK, HIRAGANA, KATAKANA, KANJI, KANJINUMERIC;

		public int getId() {
			return ordinal();
		}
	}

	/**
	 * Constructor
	 */
	public CharacterDefinition() {
		for (int i = 0; i < characterCategoryMap.length; i++) {
			characterCategoryMap[i] = CharacterClass.DEFAULT;
		}
	}

	public int lookup(char c) {
		return characterCategoryMap[c].getId();
	}

	public CharacterClass getCharacterClass(char c) {
		return characterCategoryMap[c];
	}

	public boolean isInvoke(char c) {
		CharacterClass characterClass = characterCategoryMap[c];
		int[] invokeDefinition = invokeDefinitionMap.get(characterClass);
		return invokeDefinition[0] == 1;
	}

	public boolean isGroup(char c) {
		CharacterClass characterClass = characterCategoryMap[c];
		int[] invokeDefinition = invokeDefinitionMap.get(characterClass);
		return invokeDefinition[1] == 1;
	}

	public boolean isKanji(char c) {
		return characterCategoryMap[c] == CharacterClass.KANJI ||
			   characterCategoryMap[c] == CharacterClass.KANJINUMERIC;
	}

	/**
	 * Put mapping from unicode code point to character class.
	 * 
	 * @param codePoint
	 *            code point
	 * @param class character class name
	 */
	public void putCharacterCategory(int codePoint, String characterClassName) {
		characterClassName = characterClassName.split(" ")[0]; // use first
																// category
																// class

		// Override Nakaguro
		if (codePoint == 0x30FB) {
			characterClassName = "SYMBOL";
		}
		characterCategoryMap[codePoint] = CharacterClass.valueOf(characterClassName);
	}

	public void putInvokeDefinition(String characterClassName, int invoke, int group, int length) {
		CharacterClass characterClass = CharacterClass
				.valueOf(characterClassName);
		int[] values = { invoke, group, length };
		invokeDefinitionMap.put(characterClass, values);
	}
}
