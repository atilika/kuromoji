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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.atilika.kuromoji.trie.DoubleArrayTrie;
import org.atilika.kuromoji.trie.Trie;
import org.junit.Test;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class DoubleArrayTrieTest {

	@Test
	public void buildTest() {		
		Trie trie = getTrie();
		DoubleArrayTrie doubleArrayTrie = new DoubleArrayTrie();
		doubleArrayTrie.build(trie);
	}

	@Test
	public void writeTest() throws IOException {
		Trie trie = getTrie();
		
		DoubleArrayTrie doubleArrayTrie = new DoubleArrayTrie();
		doubleArrayTrie.build(trie);
		
		try{
			doubleArrayTrie.write("/some/path/which/is/not/exist");
			fail();
		} catch(IOException e){
			
		}
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpDir + File.separator + "datmp");
		dir.mkdir();
		doubleArrayTrie.write(dir.getCanonicalPath());
		dir.deleteOnExit();
		for(File file : dir.listFiles()) {
			file.deleteOnExit();
		}
		
		assertTrue(dir.list().length > 0);
	}

	@Test
	public void lookupTest() throws IOException {
		Trie trie = getTrie();
		
		DoubleArrayTrie doubleArrayTrie = new DoubleArrayTrie();
		doubleArrayTrie.build(trie);

		String tmpDir = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpDir + File.separator + "datmp");
		dir.mkdir();
		doubleArrayTrie.write(dir.getCanonicalPath());
		dir.deleteOnExit();
		for(File file : dir.listFiles()) {
			file.deleteOnExit();
		}

		doubleArrayTrie = DoubleArrayTrie.read(new FileInputStream(dir.getCanonicalPath() + File.separator + DoubleArrayTrie.FILENAME));
		
		assertEquals(0, doubleArrayTrie.lookup("a"));
		assertTrue(doubleArrayTrie.lookup("abc") > 0);
		assertTrue(doubleArrayTrie.lookup("あいう") > 0);
		assertTrue(doubleArrayTrie.lookup("xyz") < 0);
	}
	
	private Trie getTrie() {
		Trie trie = new Trie();
		trie.add("abc");
		trie.add("abd");
		trie.add("あああ");
		trie.add("あいう");
		return trie;
	}
}
