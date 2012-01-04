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
package org.atilika.kuromoji.util;

import java.util.Map.Entry;
import java.util.Set;

import org.atilika.kuromoji.trie.DoubleArrayTrie;
import org.atilika.kuromoji.trie.Trie;


/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class DoubleArrayTrieBuilder {

	
	public DoubleArrayTrieBuilder() {
		
	}

	public static DoubleArrayTrie build(Set<Entry<Integer, String>> entries) {
		Trie tempTrie = buildTrie(entries);
		DoubleArrayTrie daTrie = new DoubleArrayTrie();
		daTrie.build(tempTrie);
		return daTrie;
	}
	
	public static Trie buildTrie(Set<Entry<Integer, String>> entries) {
		Trie trie = new Trie();
		for (Entry<Integer, String> entry : entries) {
			String surfaceForm = entry.getValue();
			trie.add(surfaceForm);
		}
		return trie;
	}
}
