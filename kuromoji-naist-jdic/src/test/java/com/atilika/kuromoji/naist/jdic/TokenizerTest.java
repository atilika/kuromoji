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

package com.atilika.kuromoji.naist.jdic;

import org.junit.Before;
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

    private Tokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = new Tokenizer();
    }

    @Test
    public void testFirstEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("　");
        String expectedFeatures = "記号,空白,*,*,*,*,　,　,　,,";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
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

        String[] surfaces = {"スペース", "ステーション", "に", "行き", "ます", "。", "うたがわしい", "。"};
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
    public void testKansaiInternationalAirport() {
        List<Token> tokens = tokenizer.tokenize("関西国際空港");
        String expectedFeatures = "名詞,固有名詞,組織,*,*,*,関西国際空港,カンサイコクサイクウコウ,カンサイコクサイクーコー,,";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testLastEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("鳥肉");
        String expectedFeatures = "名詞,一般,*,*,*,*,鳥肉,トリニク,トリニク,,";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testSurface() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");

        String[] expectedSurfaces = new String[]{"お", "寿司", "が", "食べ", "たい", "。"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurface());
        }
    }

    @Test
    public void testAllFeatures() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");

        String[] expectedFeatures = new String[]{
            "接頭詞,名詞接続,*,*,*,*,お,オ,オ,,",
            "名詞,一般,*,*,*,*,寿司,スシ,スシ,,",
            "助詞,格助詞,一般,*,*,*,が,ガ,ガ,,",
            "動詞,自立,*,*,一段,連用形,食べる,タベ,タベ,たべ/食/食べ,",
            "助動詞,*,*,*,特殊・タイ,基本形,たい,タイ,タイ,,",
            "記号,句点,*,*,*,*,。,。,。,,"
        };

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testPartOfSpeech() throws Exception {
        List<Token> tokens = tokenizer.tokenize("大きい魚が食べたい。");

        String[] expectedPos = new String[]{
            "形容詞,自立,*,*",
            "名詞,一般,*,*",
            "助詞,格助詞,一般,*",
            "動詞,自立,*,*",
            "助動詞,*,*,*",
            "記号,句点,*,*"
        };

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String partOfSpeech = token.getPartOfSpeechLevel1()
                + "," + token.getPartOfSpeechLevel2()
                + "," + token.getPartOfSpeechLevel3()
                + "," + token.getPartOfSpeechLevel4();
            assertEquals(expectedPos[i], partOfSpeech);
        }
    }

    @Test
    public void testPosLevels() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedPosLevel1 = {"接頭詞", "名詞", "助詞", "動詞", "助動詞"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel1[i], tokens.get(i).getPartOfSpeechLevel1());
        }

        String[] expectedPosLevel2 = {"名詞接続", "一般", "格助詞", "自立", "*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel2[i], tokens.get(i).getPartOfSpeechLevel2());
        }

        String[] expectedPosLevel3 = {"*", "*", "一般", "*", "*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel3[i], tokens.get(i).getPartOfSpeechLevel3());
        }

        String[] expectedPosLevel4 = {"*", "*", "*", "*", "*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPosLevel4[i], tokens.get(i).getPartOfSpeechLevel4());
        }
    }

    @Test
    public void testConjugationType() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");

        String[] expectedConjugationTypes = new String[]{"*", "*", "*", "一段", "特殊・タイ", "*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedConjugationTypes[i], tokens.get(i).getConjugationType());
        }
    }

    @Test
    public void testConjugationForm() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");

        String[] expectedConjugationForms = new String[]{"*", "*", "*", "連用形", "基本形", "*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedConjugationForms[i], tokens.get(i).getConjugationForm());
        }
    }

    @Test
    public void testBaseForm() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");

        String[] expectedBaseForms = new String[]{"お", "寿司", "が", "食べる", "たい", "。"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedBaseForms[i], tokens.get(i).getBaseForm());
        }
    }

    @Test
    public void testPronunciation() throws Exception {
        List<Token> tokens = tokenizer.tokenize("大きい魚が食べたい。");

        String[] expectedPronunciations = new String[]{"オーキイ", "サカナ", "ガ", "タベ", "タイ", "。"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPronunciations[i], tokens.get(i).getPronunciation());
        }
    }

    @Test
    public void testReading() throws Exception {
        List<Token> tokens = tokenizer.tokenize("大きい魚が食べたい。");

        String[] expectedReadings = new String[]{"オオキイ", "サカナ", "ガ", "タベ", "タイ", "。"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedReadings[i], tokens.get(i).getReading());
        }
    }

    @Test
    public void testTranscriptionVariation() throws Exception {
        List<Token> tokens = tokenizer.tokenize("弄ばれた。");
        String expected = "もてあそば/弄ば";

        assertEquals(expected, tokens.get(0).getTranscriptionVariation());
    }

    @Test
    public void testCompoundInformation() throws Exception {
        List<Token> tokens = tokenizer.tokenize("ああなったね。");
        String expected = "<w orth=\"ああなっ\" form=\"アアナッ\" pos=\"動詞-自立\" ctype=\"五段・ラ行\" cform=\"連用タ接続\" ><w orth=\"ああ\" form=\"アア\" pos=\"副詞-一般\" ctype=\"\" cform=\"\" >ああ</w><w orth=\"なっ\" form=\"ナッ\" pos=\"動詞-自立\" ctype=\"五段・ラ行\" cform=\"連用タ接続\" >なっ</w></w>";

        assertEquals(expected, tokens.get(0).getCompoundInformation());
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
            getClass().getResourceAsStream("/bocchan-naist-jdic-features.txt"),
            getClass().getResourceAsStream("/bocchan.txt"),
            tokenizer
        );
    }

    @Test
    public void testPunctuation() {
        String gerryNoHanaNoHanashi = "僕の鼻はちょっと\r\n長いだよ。";

        // A special segmentation case not covered by CommonCornerCasesTest.testPunctuation())
        assertTokenSurfacesEquals(
            Arrays.asList(
                "僕", "の", "鼻", "は", "ちょっと", "\r\n", "長い", "だ", "よ", "。"
            ),
            tokenizer.tokenize(gerryNoHanaNoHanashi)
        );
    }
}
