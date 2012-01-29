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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TokenizerTest {

	private static Tokenizer tokenizer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tokenizer = Tokenizer.builder().build();
	}

	@Test
	public void testSimpleSegmentation() {
		String input = "スペースステーションに行きます。うたがわしい。";
		String[] surfaceForms = { "スペース", "ステーション", "に", "行き", "ます", "。", "うたがわしい", "。" };
		List<Token> tokens = tokenizer.tokenize(input);
		assertTrue(tokens.size() == surfaceForms.length);
		for (int i = 0; i < tokens.size(); i++) {
			assertEquals(surfaceForms[i], tokens.get(i).getSurfaceForm());
		}
	}
	
	
	@Test
	public void testSimpleReadings() {
		List<Token> tokens = tokenizer.tokenize("寿司が食べたいです。");
		assertTrue(tokens.size() == 6);
		assertEquals(tokens.get(0).getReading(), "スシ");
		assertEquals(tokens.get(1).getReading(), "ガ");
		assertEquals(tokens.get(2).getReading(), "タベ");
		assertEquals(tokens.get(3).getReading(), "タイ");
		assertEquals(tokens.get(4).getReading(), "デス");
		assertEquals(tokens.get(5).getReading(), "。");
		
		tokens = tokenizer.tokenize("郵税");
		assertEquals(tokens.get(0).getReading(), "ユウゼイ");
	}
	
	@Test
	public void testSimpleBaseForm() {
		// Known word
		List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");
		assertTrue(tokens.size() == 6);
		assertEquals("食べ", tokens.get(3).getSurfaceForm());
		assertEquals("食べる", tokens.get(3).getBaseForm());
		
		// Unknown word
		tokens = tokenizer.tokenize("アティリカ株式会社");
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).isUnknown());
		assertNull(tokens.get(0).getBaseForm());
		assertTrue(tokens.get(1).isKnown());
		assertEquals("株式会社", tokens.get(1).getBaseForm());
	}
	
	@Test
	public void testYabottaiCornerCase() {
		List<Token> tokens = tokenizer.tokenize("やぼったい");
		assertEquals(1, tokens.size());
		assertEquals("やぼったい", tokens.get(0).getSurfaceForm());
	}
	
	@Test
	public void testTsukitoshaCornerCase() {
		List<Token> tokens = tokenizer.tokenize("突き通しゃ");
		assertEquals(1, tokens.size());
		assertEquals("突き通しゃ", tokens.get(0).getSurfaceForm());
	}
	
	@Ignore
	@Test
	public void testBocchan() throws IOException, InterruptedException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(
				this.getClass().getClassLoader().getResourceAsStream("bocchan.utf-8.txt")));
		
		String line = reader.readLine();
		reader.close();

		System.out.println("Test for Bocchan without pre-splitting sentences");
		long totalStart = System.currentTimeMillis();
		for (int i = 0; i < 100; i++){
			tokenizer.tokenize(line);
		}
		System.out.println("Total time : " + (System.currentTimeMillis() - totalStart));
		System.out.println("Test for Bocchan with pre-splitting sentences");
		String[] sentences = line.split("、|。");
		totalStart = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			for (String sentence: sentences) {
				tokenizer.tokenize(sentence);				
			}
		}
		System.out.println("Total time : " + (System.currentTimeMillis() - totalStart));
	}
}
