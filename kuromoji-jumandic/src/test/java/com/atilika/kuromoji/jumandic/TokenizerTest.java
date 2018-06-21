/**
 * Copyright © 2010-2018 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.jumandic;

import com.atilika.kuromoji.CommonCornerCasesTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.atilika.kuromoji.TestUtils.assertEqualTokenFeatureLengths;
import static com.atilika.kuromoji.TestUtils.assertTokenSurfacesEquals;
import static com.atilika.kuromoji.TestUtils.assertTokenizedStreamEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TokenizerTest {

    private static Tokenizer tokenizer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tokenizer = new Tokenizer();
    }

    @Test
    public void testSimpleSegmentation() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedSurfaceForms = {"お", "寿司", "が", "食べ", "たい"};

        assertEquals(expectedSurfaceForms.length, tokens.size());

        for (int i = 0; i < expectedSurfaceForms.length; i++) {
            assertEquals(expectedSurfaceForms[i], tokens.get(i).getSurface());
        }
    }

    @Test
    public void testSimpleMultiTokenization() {
        String input = "スペースステーションに行きます。うたがわしい。";
        List<List<Token>> tokenLists = tokenizer.multiTokenize(input, 20, 100000);

        assertEquals(20, tokenLists.size());

        for (List<Token> tokens : tokenLists) {
            StringBuilder sb = new StringBuilder();
            for (Token token : tokens) {
                sb.append(token.getSurface());
            }
            assertEquals(input, sb.toString());
        }

        String[] surfaces = {"スペースステーション", "に", "行き", "ます", "。", "うたがわしい", "。"};
        assertTokenSurfacesEquals(
                Arrays.asList(surfaces),
                tokenLists.get(0)
        );
    }

    @Test
    public void testMultiTokenizationFindsAll() {
        String input = "スペースステーション";
        List<List<Token>> tokenLists = tokenizer.multiTokenizeNBest(input, 100);
        assertEquals(9, tokenLists.size());
    }

    @Test
    public void testMultiNoOverflow() {
        String input = "バスできた。";
        List<List<Token>> tokenLists = tokenizer.multiTokenizeBySlack(input, Integer.MAX_VALUE);
        assertNotEquals(0, tokenLists.size());
    }

    @Test
    public void testMultiEmptyString() {
        String input = "";
        List<List<Token>> tokenLists = tokenizer.multiTokenize(input, 10, Integer.MAX_VALUE);
        assertEquals(1, tokenLists.size());
    }

    @Test
    public void testSimpleFeatures() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedFeatures = {
            "接頭辞,名詞接頭辞,*,*,お,お,代表表記:御/お",
            "名詞,普通名詞,*,*,寿司,すし,代表表記:鮨/すし カテゴリ:人工物-食べ物 ドメイン:料理・食事",
            "助詞,格助詞,*,*,が,が,*",
            "動詞,*,母音動詞,未然形,食べる,たべ,代表表記:食べる/たべる ドメイン:料理・食事",
            "接尾辞,形容詞性述語接尾辞,イ形容詞アウオ段,基本形,たい,たい,連語"
        };

        assertEquals(expectedFeatures.length, tokens.size());

        for (int i = 0; i < expectedFeatures.length; i++) {
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testFirstEntryCornerCase() throws Exception {
        List<Token> tokens = tokenizer.tokenize("日本だ");

        String expectedFeatures = "判定詞,*,判定詞,基本形,だ,だ,*";

        assertEquals(2, tokens.size());
        assertEquals(expectedFeatures, tokens.get(1).getAllFeatures());
    }

    @Test
    public void testNextToLastEntryCornerCase() throws Exception {
        // Note: We check the next to last entry in the dictionaries and not the last
        // The last entry's weight makes it the second best path, and since Kuromoji doesn't currently stack tokens
        // in the final result, there is no way to produce the last entry at this point.
        List<Token> tokens = tokenizer.tokenize("ＺｉｌｌｉｏｎＦｏｒｃｅ");

        String expectedFeatures = "名詞,組織名,*,*,ＺｉｌｌｉｏｎＦｏｒｃｅ,ＺｉｌｌｉｏｎＦｏｒｃｅ,自動獲得:Wikipedia Wikipedia上位語:ヴィジュアル系ロックバンド 読み不明";

        assertEquals(1, tokens.size());
        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testPosLevels() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedPosLevel1 = {"接頭辞", "名詞", "助詞", "動詞", "接尾辞"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel1[i], tokens.get(i).getPartOfSpeechLevel1());
        }

        String[] expectedPosLevel2 = {"名詞接頭辞", "普通名詞", "格助詞", "*", "形容詞性述語接尾辞"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel2[i], tokens.get(i).getPartOfSpeechLevel2());
        }

        String[] expectedPosLevel3 = {"*", "*", "*", "母音動詞", "イ形容詞アウオ段"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel3[i], tokens.get(i).getPartOfSpeechLevel3());
        }

        String[] expectedPosLevel4 = {"*", "*", "*", "未然形", "基本形"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel4[i], tokens.get(i).getPartOfSpeechLevel4());
        }

    }

    @Test
    public void testJumandicSpecificFeatures() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい！");

        String[] expectedPos = new String[]{
            "接頭辞,名詞接頭辞,*,*",
            "名詞,普通名詞,*,*",
            "助詞,格助詞,*,*",
            "動詞,*,母音動詞,未然形",
            "接尾辞,形容詞性述語接尾辞,イ形容詞アウオ段,基本形",
            "特殊,記号,*,*"
        };

        String[] expectedBaseForms = new String[]{"お", "寿司", "が", "食べる", "たい", "！"};

        String[] expectedReadings = new String[]{"お", "すし", "が", "たべ", "たい", "！"};

        String[] expectedRepresentations = new String[]{
            "代表表記:御/お",
            "代表表記:鮨/すし カテゴリ:人工物-食べ物 ドメイン:料理・食事",
            "*",
            "代表表記:食べる/たべる ドメイン:料理・食事",
            "連語",
            "*"
        };

        for (int i = 0; i < expectedRepresentations.length; i++) {
            Token token = tokens.get(i);

            assertEquals(expectedPos[i], getCombinedPartOfSpeech(token));
            assertEquals(expectedBaseForms[i], token.getBaseForm());
            assertEquals(expectedReadings[i], token.getReading());
            assertEquals(expectedRepresentations[i], token.getSemanticInformation());
        }
    }

    @Test
    public void testFeatureLengths() throws IOException {
        String userDictionary = "" +
            "gsf,gsf,ジーエスーエフ,カスタム名詞\n";

        Tokenizer tokenizer = new Tokenizer.Builder()
            .userDictionary(
                new ByteArrayInputStream(
                    userDictionary.getBytes(StandardCharsets.UTF_8)
                )
            )
            .build();

        assertEqualTokenFeatureLengths("ahgsfdajhgsfdこの丘はアクロポリスと呼ばれている。", tokenizer);
    }

    @Test
    public void testNewBocchan() throws IOException {
        assertTokenizedStreamEquals(
            getClass().getResourceAsStream("/bocchan-jumandic-features.txt"),
            getClass().getResourceAsStream("/bocchan.txt"),
            tokenizer
        );
    }

    @Test
    public void testPunctuation() {
        CommonCornerCasesTest.testPunctuation(new Tokenizer());
    }

    private String getCombinedPartOfSpeech(Token token) {
        return token.getPartOfSpeechLevel1() + "," + token.getPartOfSpeechLevel2() + "," + token.getPartOfSpeechLevel3() + "," + token.getPartOfSpeechLevel4();
    }
}
