/**
 * Copyright © 2010-2017 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.unidic.neologd;

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
    public void testFirstEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("¡");
        String expectedFeatures = "補助記号,一般,*,*,*,*,,¡,¡,,¡,,記号,*,*,*,*";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testLastEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("ヴィ");
        String expectedFeatures = "記号,一般,*,*,*,*,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,記号,*,*,*,*";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testKansaiInternationalAirport() {
        List<Token> tokens = tokenizer.tokenize("関西国際空港");

        String[] expectedSurfaces = {"関西国際空港"};
        String[] expectedFeatures = {
            "名詞,固有名詞,一般,*,*,*,カンサイコクサイクウコウ,関西国際空港,関西国際空港,カンサイコクサイクウコー,関西国際空港,カンサイコクサイクウコー,固,*,*,*,*"
        };

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurface());
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testNucleus() {
        List<Token> tokens = tokenizer.tokenize("核");

        String expectedSurface = "核";
        String expectedFeatures = "名詞,固有名詞,人名,姓,*,*,サネ,核,核,サネ,核,サネ,固,*,*,*,*";

        assertEquals(expectedSurface, tokens.get(0).getSurface());
        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testEmoji() {
        List<Token> tokens = tokenizer.tokenize("Σ（゜□゜）");
        String expectedFeatures = "補助記号,ＡＡ,顔文字,*,*,*,,Σ（゜□゜）,Σ（゜□゜）,,Σ（゜□゜）,,記号,*,*,*,*";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testKakeyo() {
        List<Token> tokens = tokenizer.tokenize("掛けよう");
        String expectedFeatures = "動詞,非自立可能,*,*,下一段-カ行,意志推量形,カケル,掛ける,掛けよう,カケヨー,掛ける,カケル,和,カ濁,基本形,*,*";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testPosLevels() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] posLevel1 = new String[]{"接頭辞", "名詞", "助詞", "動詞", "助動詞"};
        String[] posLevel2 = new String[]{"*", "普通名詞", "格助詞", "一般", "*"};
        String[] posLevel3 = new String[]{"*", "一般", "*", "*", "*"};
        String[] posLevel4 = new String[]{"*", "*", "*", "*", "*"};

        assertEquals(posLevel1.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(posLevel1[i], tokens.get(i).getPartOfSpeechLevel1());
            assertEquals(posLevel2[i], tokens.get(i).getPartOfSpeechLevel2());
            assertEquals(posLevel3[i], tokens.get(i).getPartOfSpeechLevel3());
            assertEquals(posLevel4[i], tokens.get(i).getPartOfSpeechLevel4());
        }
    }

    @Test
    public void testConjugationTypeAndForm() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedConjugationForms = new String[]{"*", "*", "*", "連用形-一般", "終止形-一般"};
        String[] expectedConjugationTypes = new String[]{"*", "*", "*", "下一段-バ行", "助動詞-タイ"};

        assertEquals(expectedConjugationForms.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedConjugationForms[i], tokens.get(i).getConjugationForm());
            assertEquals(expectedConjugationTypes[i], tokens.get(i).getConjugationType());
        }
    }

    @Test
    public void testLemmasAndLemmaReadings() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedReadingForms = new String[]{"オ", "スシ", "ガ", "タベル", "タイ"};
        String[] expectedLemmas = new String[]{"御", "寿司", "が", "食べる", "たい"};

        assertEquals(expectedLemmas.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedReadingForms[i], tokens.get(i).getLemmaReadingForm());
            assertEquals(expectedLemmas[i], tokens.get(i).getLemma());
        }
    }

    @Test
    public void testWrittenFormsAndWrittenBaseForms() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedWrittenForms = new String[]{"お", "寿司", "が", "食べ", "たい"};
        String[] expectedWrittenBaseForms = new String[]{"お", "寿司", "が", "食べる", "たい"};

        assertEquals(expectedWrittenForms.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedWrittenForms[i], tokens.get(i).getWrittenForm());
            assertEquals(expectedWrittenBaseForms[i], tokens.get(i).getWrittenBaseForm());
        }
    }

    @Test
    public void testPronunciationAndPronunciationBaseForms() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedPronunciations = new String[]{"オ", "スシ", "ガ", "タベ", "タイ"};
        String[] expectedPronunciationBaseForms = new String[]{"オ", "スシ", "ガ", "タベル", "タイ"};

        assertEquals(expectedPronunciations.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedPronunciations[i], tokens.get(i).getPronunciation());
            assertEquals(expectedPronunciationBaseForms[i], tokens.get(i).getPronunciationBaseForm());
        }
    }

    @Test
    public void testLanguageType() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String expectedLanguageType = "和";

        for (Token token : tokens) {
            assertEquals(expectedLanguageType, token.getLanguageType());
        }
    }

    @Test
    public void testInitialSoundAlterationTypesAndForms() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedInitialSoundAlterationTypes = new String[]{"*", "ス濁", "*", "*", "*"};
        String[] expectedInitialSoundAlterationForms = new String[]{"*", "基本形", "*", "*", "*"};

        assertEquals(expectedInitialSoundAlterationTypes.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedInitialSoundAlterationTypes[i], tokens.get(i).getInitialSoundAlterationType());
            assertEquals(expectedInitialSoundAlterationForms[i], tokens.get(i).getInitialSoundAlterationForm());
        }
    }

    @Test
    public void testFinalSoundAlterationTypesAndForms() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedFinalSoundAlterationTypes = new String[]{"促添", "*", "*", "*", "*"};
        String[] expectedFinalSoundAlterationForms = new String[]{"基本形", "*", "*", "*", "*"};

        assertEquals(expectedFinalSoundAlterationTypes.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedFinalSoundAlterationTypes[i], tokens.get(i).getFinalSoundAlterationType());
            assertEquals(expectedFinalSoundAlterationForms[i], tokens.get(i).getFinalSoundAlterationForm());
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
            getClass().getResourceAsStream("/bocchan-unidic-neologd-features.txt"),
            getClass().getResourceAsStream("/bocchan.txt"),
            tokenizer
        );
    }

    @Test
    public void testPunctuation() {
        CommonCornerCasesTest.testPunctuation(new Tokenizer());
    }
}
