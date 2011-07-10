/**
 * Copyright © 2010-2011 Atilika Inc.  All rights reserved.
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

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.atilika.kuromoji.dict.UnknownDictionary;
import org.atilika.kuromoji.util.CSVUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnknownDictionaryTest {
	public static final String FILENAME = "unk-tokeninfo-dict.obj";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testPutCharacterCategory() {
		UnknownDictionary unkDic = new UnknownDictionary(10 * 1024 * 1024);
		
		try{
			unkDic.putCharacterCategory(0, "DUMMY_NAME");
			fail();
		} catch(Exception e) {
			
		}

		try{
			unkDic.putCharacterCategory(-1, "KATAKANA");
			fail();
		} catch(Exception e) {
			
		}
		
		unkDic.putCharacterCategory(0, "DEFAULT");
		unkDic.putCharacterCategory(1, "GREEK");
		unkDic.putCharacterCategory(2, "HIRAGANA");
		unkDic.putCharacterCategory(3, "KATAKANA");
		unkDic.putCharacterCategory(4, "KANJI");
	}
	
	@Test
	public void testPut() {
		UnknownDictionary unkDic = new UnknownDictionary(10 * 1024 * 1024);
		try{
			unkDic.put(CSVUtil.parse("KANJI,1285,11426,名詞,一般,*,*,*,*,*"));
			fail();
		} catch(Exception e){
			
		}
		
		String entry1 = "KANJI,1285,1285,11426,名詞,一般,*,*,*,*,*";
		String entry2 = "ALPHA,1285,1285,13398,名詞,一般,*,*,*,*,*";
		String entry3 = "HIRAGANA,1285,1285,13069,名詞,一般,*,*,*,*,*";
		
		unkDic.putCharacterCategory(0, "KANJI");
		unkDic.putCharacterCategory(1, "ALPHA");
		unkDic.putCharacterCategory(2, "HIRAGANA");
		
		unkDic.put(CSVUtil.parse(entry1));
		unkDic.put(CSVUtil.parse(entry2));
		unkDic.put(CSVUtil.parse(entry3));
	}

//	@Test
//	public void testLookupForInvoke() throws IOException {
//		UnknownDictionary dictionary = createDictionary();
//		String notMatch1 = "あいうえお";
//		int resultNotMatch1 = dictionary.lookupForInvoke(notMatch1);
//		assertEquals(0, resultNotMatch1);
//
//		String notMatch2 = "あイウエオ";
//		int resultNotMatch2 = dictionary.lookupForInvoke(notMatch2);
//		assertEquals(0, resultNotMatch2);
//		
//		String matchKatakana = "アイウエオ";
//		int resultMatchKatakana = dictionary.lookupForInvoke(matchKatakana);
//		assertEquals(5, resultMatchKatakana);
//
//		String matchAlpha = "ABC";
//		int resultMatchAlpha = dictionary.lookupForInvoke(matchAlpha);
//		assertEquals(3, resultMatchAlpha);
//
//		String matchKatakanaPartial = "アイウあいう";
//		int resultMatchKatakanaPartial = dictionary.lookupForInvoke(matchKatakanaPartial);
//		assertEquals(3, resultMatchKatakanaPartial);
//	}
//	
//	@Test
//	public void testLookupForNotInvoke() throws IOException {
//		UnknownDictionary dictionary = createDictionary();
//
//		String matchHiragana1 = "あ";
//		int resultMatchHiragana1 = dictionary.lookupForNotInvoke(matchHiragana1);
//		assertEquals(1, resultMatchHiragana1);
//
//		String matchHiragana2 = "あい";
//		int resultMatchHiragana2 = dictionary.lookupForNotInvoke(matchHiragana2);
//		assertEquals(2, resultMatchHiragana2);
//
//		String matchKanji1 = "漢";
//		int resultMatchKanji1 = dictionary.lookupForNotInvoke(matchKanji1);
//		assertEquals(1, resultMatchKanji1);
//
//		String matchKanji2 = "漢字";
//		int resultMatchKanji2 = dictionary.lookupForNotInvoke(matchKanji2);
//		assertEquals(1, resultMatchKanji2);
//
//		String matchKanjiMix = "漢あ";
//		int resultMatchKanjiMix = dictionary.lookupForNotInvoke(matchKanjiMix);
//		assertEquals(1, resultMatchKanjiMix);
//		
//		String notMatch = "アイウ";
//		int resultNotMatch = dictionary.lookupForNotInvoke(notMatch);
//		assertEquals(0, resultNotMatch);
//		
//		String exception = "";
//		try{
//			dictionary.lookupForNotInvoke(exception);
//			fail();
//		} catch(Exception e){
//			
//		}
//	}
	
//	@Test
//	public void testLookupWordIds() throws IOException {
//		UnknownDictionary dictionary = createDictionary();
//		String hiragana = "あい";
//		int[] hiraganaResults = dictionary.lookupWordIds(hiragana);
//		assertEquals(7, hiraganaResults.length);
//
//		String katakana = "アイ";
//		int[] katakanaResults = dictionary.lookupWordIds(katakana);
//		assertEquals(6, katakanaResults.length);
//
//		String symbol = "!";
//		int[] symbolResults = dictionary.lookupWordIds(symbol);
//		assertEquals(1, symbolResults.length);
//	}
	
	private UnknownDictionary createDictionary() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("unk.def.utf-8");
		UnknownDictionary dictionary = new UnknownDictionary();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line = null;
		while((line = reader.readLine()) != null) {
			dictionary.put(CSVUtil.parse(line));
		}
		reader.close();

		is = this.getClass().getClassLoader().getResourceAsStream("char.def.utf-8");
		reader = new BufferedReader(new InputStreamReader(is));
		
		line = null;
		while ((line = reader.readLine()) != null) {
			line = line.replaceAll("^\\s", "");
			line = line.replaceAll("\\s*#.*", "");
			line = line.replaceAll("\\s+", " ");
			
			// Skip empty line or comment line
			if(line.length() == 0) {
				continue;
			}
			
			if(line.startsWith("0x")) {	// Category mapping
				String[] values = line.split(" ", 2);	// Split only first space
				
				if(!values[0].contains("..")) {
					int cp = Integer.decode(values[0]).intValue();
					dictionary.putCharacterCategory(cp, values[1]);					
				} else {
					String[] codePoints = values[0].split("\\.\\.");
					int cpFrom = Integer.decode(codePoints[0]).intValue();
					int cpTo = Integer.decode(codePoints[1]).intValue();
					
					for(int i = cpFrom; i <= cpTo; i++){
						dictionary.putCharacterCategory(i, values[1]);					
					}
				}
			} else {	// Invoke definition
				String[] values = line.split(" "); // Consecutive space is merged above
				String characterClassName = values[0];
				int invoke = Integer.parseInt(values[1]);
				int group = Integer.parseInt(values[2]);
				int length = Integer.parseInt(values[3]);
				dictionary.putInvokeDefinition(characterClassName, invoke, group, length);
			}
			
		}
		
		reader.close();
		
		return dictionary;
	}
}
