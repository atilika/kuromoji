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
package org.atilika.kuromoji;

import org.atilika.kuromoji.dict.Dictionary;
import org.atilika.kuromoji.viterbi.ViterbiNode.Type;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class Token {
	private final Dictionary dictionary;

	private final int wordId;
	
	private final String surfaceForm;
	
	private final int position;
	
	private final Type type;
	
	public Token(int wordId, String surfaceForm, Type type, int position, Dictionary dictionary) {
		this.wordId = wordId;
		this.surfaceForm = surfaceForm;
		this.type = type;
		this.position = position;
		this.dictionary = dictionary;
	}

	/**
	 * @return surfaceForm
	 */
	public String getSurfaceForm() {
		return surfaceForm;
	}

	/**
	 * Returns base form or null if it doens't exist, i.e. for unknown words of user dictionary terms
	 * 
	 * @return base form or null if non-existent
	 */
	public String getBaseForm() {
		return dictionary.getBaseForm(wordId);
	}

	/**
	 * @return all features
	 */
	public String getAllFeatures() {
		return dictionary.getAllFeatures(wordId);
	}

	/**
	 * @return all features as array
	 */
	public String[] getAllFeaturesArray() {
		return dictionary.getAllFeaturesArray(wordId);
	}

	/**
	 * @return reading. null if token doesn't have reading.
	 */
	public String getReading() {
		return dictionary.getReading(wordId);
	}

	/**
	 * @return part of speech.
	 */
	public String getPartOfSpeech() {
		return dictionary.getPartOfSpeech(wordId);
	}

	/**
	 * Returns true if this token is known word
	 * @return true if this token is in standard dictionary. false if not.
	 */
	public boolean isKnown() {
		return type == Type.KNOWN;
	}

	/**
	 * Returns true if this token is unknown word
	 * @return true if this token is unknown word. false if not.
	 */
	public boolean isUnknown() {
		return type == Type.UNKNOWN;
	}
	
	/**
	 * Returns true if this token is defined in user dictionary
	 * @return true if this token is in user dictionary. false if not.
	 */
	public boolean isUser() {
		return type == Type.USER;
	}
	
	/**
	 * Get index of this token in input text
	 * @return position of token
	 */
	public int getPosition() {
		return position;
	}

}