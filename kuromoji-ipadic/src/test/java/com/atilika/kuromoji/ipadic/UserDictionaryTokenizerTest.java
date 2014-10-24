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
package com.atilika.kuromoji.ipadic;

import com.atilika.kuromoji.Token;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserDictionaryTokenizerTest {

    private static Tokenizer tokenizer;

    @Before
    public void setUp() throws IOException {
        String userDictionary = "クロ,クロ,クロ,カスタム名詞,100\n"
            + "真救世主,真救世主,シンキュウセイシュ,カスタム名詞,100\n"
            + "真救世主伝説,真救世主伝説,シンキュウセイシュデンセツ,カスタム名詞,100\n"
            + "北斗の拳,北斗の拳,ホクトノケン,カスタム名詞,100";

        buildTokenizerWithUserDictionary(userDictionary);
    }

    @Test
    public void testWhitespace() throws IOException {
        String entry = "iPhone4 S,iPhone4 S,iPhone4 S,カスタム名詞,100";
        buildTokenizerWithUserDictionary(entry);
        List<Token> tokens = tokenizer.tokenize("iPhone4 S");

        assertEquals("iPhone4 S", tokens.get(0).getSurfaceForm());
    }

    @Test(expected = RuntimeException.class)
    public void testBadlyFormattedEntry() throws IOException {
        String entry = "関西国際空港,関西 国際 空,カンサイ コクサイクウコウ,カスタム名詞,100";
        buildTokenizerWithUserDictionary(entry);
    }

    @Test
    public void testAcropolisWithLowCost() throws IOException {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞,100";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        String input = "アクロポリス";
        String[] surfaceForms = {"ア", "クロ", "ポリス"};

        List<Token> tokens = tokenizer.tokenize(input);

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(surfaceForms[i], tokens.get(i).getSurfaceForm());
        }
    }

    @Test
    public void testAcropolisWithHighCost() throws IOException {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞,100000";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        String input = "アクロポリス";
        String[] surfaceForms = {"アクロポリス"};

        List<Token> tokens = tokenizer.tokenize(input);

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(surfaceForms[i], tokens.get(i).getSurfaceForm());
        }
    }

    @Test
    public void testAllFeatures() {
        String input = "シロクロ";
        String[] surfaceForms = {"シロ", "クロ"};
        List<Token> tokens = tokenizer.tokenize(input);

        assertEquals(surfaceForms.length, tokens.size());
        Token token = tokens.get(1);
        String actual = token.getSurfaceForm() + "\t" + token.getAllFeatures();
        assertEquals("クロ\tカスタム名詞,*,*,*,*,*,*,クロ,*", actual);
    }


    @Test
    public void testAcropolisInSentence() throws IOException {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞,100";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        String input = "この丘はアクロポリスと呼ばれている。";
        String[] surfaceForms = {"この", "丘", "は", "ア", "クロ", "ポリス", "と", "呼ば", "れ", "て", "いる", "。"};
        List<Token> tokens = tokenizer.tokenize(input);

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(surfaceForms[i], tokens.get(i).getSurfaceForm());
        }

        assertEquals(surfaceForms.length, tokens.size());
    }

    @Test
    public void testLatticeBrokenAfterUserDictEntry() throws IOException {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞,-10000";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        String input = "アクロア";
        String[] surfaceForms = {"ア", "クロ", "ア"};
        String[] features = {
            "*,*,*,*,*,*,*,*,*",
            "カスタム名詞,*,*,*,*,*,*,クロ,*",
            "*,*,*,*,*,*,*,*,*"
        };
        List<Token> tokens = tokenizer.tokenize(input);

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(surfaceForms[i], tokens.get(i).getSurfaceForm());
            assertEquals(features[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testLatticeBrokenAfterUserDictEntryInSentence() throws IOException {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞,-10000";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        String input = "この丘の名前はアクロアだ。";
        String[] surfaceForms = {"この", "丘", "の", "名前", "は", "ア", "クロ", "ア", "だ", "。"};
        String[] features = {
            "連体詞,*,*,*,*,*,この,コノ,コノ",
            "名詞,一般,*,*,*,*,丘,オカ,オカ",
            "助詞,連体化,*,*,*,*,の,ノ,ノ",
            "名詞,一般,*,*,*,*,名前,ナマエ,ナマエ",
            "助詞,係助詞,*,*,*,*,は,ハ,ワ",
            "*,*,*,*,*,*,*,*,*",
            "カスタム名詞,*,*,*,*,*,*,クロ,*",
            "*,*,*,*,*,*,*,*,*",
            "助動詞,*,*,*,特殊・ダ,基本形,だ,ダ,ダ",
            "記号,句点,*,*,*,*,。,。,。"
        };
        List<Token> tokens = tokenizer.tokenize(input);

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(surfaceForms[i], tokens.get(i).getSurfaceForm());
            assertEquals(features[i], tokens.get(i).getAllFeatures());
        }
    }


    @Test
    public void testShinKyuseishu() throws IOException {
        String userDictionaryEntry = "真救世主,真救世主,シンキュウセイシュ,カスタム名詞,100";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        assertEquals("シンキュウセイシュ", given("真救世主伝説"));
    }

    @Test
    public void testShinKyuseishuDensetsu() throws IOException {
        String userDictionaryEntry = "真救世主伝説,真救世主伝説,シンキュウセイシュデンセツ,カスタム名詞,100";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        assertEquals("シンキュウセイシュデンセツ", given("真救世主伝説"));
    }

    @Test
    public void testCheckDifferentSpelling() throws IOException {
        String input = "北斗の拳は真救世主伝説の名曲である。";
        List<Token> tokens = tokenizer.tokenize(input);
        String[] expectedReadings = {"ホクトノケン", "ハ", "シンキュウセイシュデンセツ", "ノ", "メイキョク", "デ", "アル", "。"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedReadings[i], tokens.get(i).getReading());
        }
    }

    @Test
    public void testLongestActualJapaneseWord() throws IOException {
        String userDictionaryEntry = "竜宮の乙姫の元結の切り外し,竜宮の乙姫の元結の切り外し,リュウグウノオトヒメノモトユイノキリハズシ,カスタム名詞,100";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        assertEquals("リュウグウノオトヒメノモトユイノキリハズシ", given("竜宮の乙姫の元結の切り外し"));
    }

    @Test
    public void testLongestMovieTitle() throws IOException {
        String userDictionaryEntry = "マルキ・ド・サドの演出のもとにシャラントン精神病院患者たちによって演じられたジャン＝ポール・マラーの迫害と暗殺,"
            + "マルキ・ド・サドの演出のもとにシャラントン精神病院患者たちによって演じられたジャン＝ポール・マラーの迫害と暗殺,"
            + "マルキ・ド・サドノエンシュツノモトニシャラントンセイシンビョウインカンジャタチニヨッテエンジラレタジャン＝ポール・マラーノハクガイトアンサツ,"
            + "カスタム名詞,"
            + "100";
        buildTokenizerWithUserDictionary(userDictionaryEntry);

        assertEquals("マルキ・ド・サドノエンシュツノモトニシャラントンセイシンビョウインカンジャタチニヨッテエンジラレタジャン＝ポール・マラーノハクガイトアンサツ",
            given("マルキ・ド・サドの演出のもとにシャラントン精神病院患者たちによって演じられたジャン＝ポール・マラーの迫害と暗殺"));
    }

    private String given(String input) {
        return tokenizer.tokenize(input).get(0).getReading();
    }

    private void buildTokenizerWithUserDictionary(String userDictionaryEntry) throws IOException {
        tokenizer = new Tokenizer.Builder().userDictionary(getUserDictionaryFromString(userDictionaryEntry)).build();
    }

    private ByteArrayInputStream getUserDictionaryFromString(String userDictionaryEntry) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(userDictionaryEntry.getBytes("UTF-8"));
    }
}
