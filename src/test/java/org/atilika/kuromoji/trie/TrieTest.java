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
package org.atilika.kuromoji.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.atilika.kuromoji.trie.Trie.Node;
import org.junit.Test;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class TrieTest {
	
	@Test
	public void testGetRoot() {
		Trie trie = new Trie();
		Node rootNode = trie.getRoot();
		assertNotNull(rootNode);
	}
	
	@Test
	public void testAdd() {
		Trie trie = new Trie();
		trie.add("aa");
		trie.add("ab");
		trie.add("bb");
		
		Node rootNode = trie.getRoot();
		assertEquals(2, rootNode.getChildren().length);
		assertEquals(2, rootNode.getChildren()[0].getChildren().length);
		assertEquals(1, rootNode.getChildren()[1].getChildren().length);
	}
	
	@Test
	public void testGetChildren() {
		Trie trie = new Trie();
		trie.add("aa");
		trie.add("ab");
		trie.add("bb");
		
		Node rootNode = trie.getRoot();
		assertEquals(2, rootNode.getChildren().length);
		assertEquals(2, rootNode.getChildren()[0].getChildren().length);
		assertEquals(1, rootNode.getChildren()[1].getChildren().length);
	}
	
	@Test
	public void testSinglePath() {
		Trie trie = new Trie();
		assertTrue(trie.getRoot().hasSinglePath());
		trie.add("abcdef");
		assertTrue(trie.getRoot().hasSinglePath());
		trie.add("abdfg");
		Node rootNode = trie.getRoot();
		assertEquals(2, rootNode.getChildren()[0].getChildren()[0].getChildren().length);
		assertTrue(rootNode.getChildren()[0].getChildren()[0].getChildren()[0].hasSinglePath());
		assertTrue(rootNode.getChildren()[0].getChildren()[0].getChildren()[1].hasSinglePath());
	}
}
