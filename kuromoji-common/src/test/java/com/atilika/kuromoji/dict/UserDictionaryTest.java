/**
 * Copyright 2010-2013 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.dict;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserDictionaryTest {

	@Test
	public void testLookup() throws IOException {
		UserDictionary dictionary = UserDictionary.read(getResource("userdict.txt"));// "../resources/userdict.txt");
		int[][] dictionaryEntryResult = dictionary.locateUserDefinedWordsInText("関西国際空港に行った");
		// Length should be three 関西, 国際, 空港
		assertEquals(3, dictionaryEntryResult.length);

		// Test positions
		assertEquals(0, dictionaryEntryResult[0][1]); // index of 関西
		assertEquals(2, dictionaryEntryResult[1][1]); // index of 国際
		assertEquals(4, dictionaryEntryResult[2][1]); // index of 空港

		// Test lengths
		assertEquals(2, dictionaryEntryResult[0][2]); // length of 関西
		assertEquals(2, dictionaryEntryResult[1][2]); // length of 国際
		assertEquals(2, dictionaryEntryResult[2][2]); // length of 空港

		int[][] dictionaryEntryResult2 = dictionary.locateUserDefinedWordsInText("関西国際空港と関西国際空港に行った");
		// Length should be six 
		assertEquals(6, dictionaryEntryResult2.length);
	}

    @Test
	public void testReadings() throws IOException {
		UserDictionary dictionary = UserDictionary.read(getResource("userdict.txt"));
		int wordIdNihon = 100000000; // wordId of 日本 in 日本経済新聞
		assertEquals("ニホン", dictionary.getReading(wordIdNihon));

		int wordIdAsashoryu = 100000006; // wordId for 朝青龍
		assertEquals("アサショウリュウ", dictionary.getReading(wordIdAsashoryu));

		int wordIdNotExist = 1;
		assertNull(dictionary.getReading(wordIdNotExist));
	}

	@Test
	public void testPartOfSpeech() throws IOException {
		UserDictionary dictionary = UserDictionary.read(getResource("userdict.txt"));
		int wordIdKeizai = 100000001; // wordId of 経済 in 日本経済新聞
		assertEquals("カスタム名詞", dictionary.getPartOfSpeech(wordIdKeizai));
	}

	@Test
	public void testRead() throws IOException {
		UserDictionary dictionary = UserDictionary.read(getResource("userdict.txt"));
		assertNotNull(dictionary);
	}

    @Test
    public void testUserDictionaryEntries() throws IOException {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞";
        UserDictionary dictionary = UserDictionary.read(new ByteArrayInputStream(userDictionaryEntry.getBytes("UTF-8")));
        int[][] positions = dictionary.locateUserDefinedWordsInText("この丘はアクロポリスと呼ばれている");
        int indexForKuro = 5;
        int calculatedIndex = positions[0][1];
        assertEquals(indexForKuro, calculatedIndex);
    }

    @Test
    public void testOverlappingUserDictionaryEntries() throws IOException {
        String userDictionaryEntries = "クロ,クロ,クロ,カスタム名詞\n" +
            "アクロ,アクロ,アクロ,カスタム名詞";
        UserDictionary dictionary = UserDictionary.read(new ByteArrayInputStream(userDictionaryEntries.getBytes("UTF-8")));
        int[][] positions = dictionary.locateUserDefinedWordsInText("この丘はアクロポリスと呼ばれている");
        int indexForAcro = 4;
        int calculatedIndex = positions[0][1];
        assertEquals(indexForAcro, calculatedIndex);
        assertEquals(2, positions.length);

    }

    private InputStream getResource(String s) {
        return this.getClass().getClassLoader().getResourceAsStream(s);
    }

}
