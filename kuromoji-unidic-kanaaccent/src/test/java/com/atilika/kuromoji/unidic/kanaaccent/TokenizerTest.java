/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.unidic.kanaaccent;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.atilika.kuromoji.TestUtils.assertEqualTokenFeatureLenghts;
import static com.atilika.kuromoji.TestUtils.assertMultiThreadedTokenizedStreamEquals;
import static com.atilika.kuromoji.TestUtils.assertTokenizedStreamEquals;
import static junit.framework.Assert.assertEquals;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = new Tokenizer();
    }

    @Test
    public void testFirstEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("¡");
        String expectedFeatures = "補助記号,一般,*,*,*,*,,¡,¡,,¡,,記号,*,*,*,*,,,,,*,*,*,*,*";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testLastEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("ヴィ");
        String expectedFeatures = "記号,一般,*,*,*,*,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,記号,*,*,*,*,ヴィ,ヴィ,ヴィ,ヴィ,*,*,1,*,*";

        assertEquals(1, tokens.size());
        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testExtendedUnidic() {
        List<Token> tokens = tokenizer.tokenize("日本語の形態素解析は面白い");

        String[] expectedSurfaces = {"日本", "語", "の", "形態", "素", "解析", "は", "面白い"};

        String[] expectedFeatures = {
            "名詞,固有名詞,地名,国,*,*,ニッポン,日本,日本,ニッポン,日本,ニッポン,固,*,*,*,*,ニッポン,ニッポン,ニッポン,ニッポン,*,*,3,*,*",
            "名詞,普通名詞,一般,*,*,*,ゴ,語,語,ゴ,語,ゴ,漢,*,*,*,*,ゴ,ゴ,ゴ,ゴ,*,*,1,C3,*",
            "助詞,格助詞,*,*,*,*,ノ,の,の,ノ,の,ノ,和,*,*,*,*,ノ,ノ,ノ,ノ,*,*,*,名詞%F1,*",
            "名詞,普通名詞,一般,*,*,*,ケイタイ,形態,形態,ケータイ,形態,ケータイ,漢,*,*,*,*,ケイタイ,ケイタイ,ケイタイ,ケイタイ,*,*,0,C2,*",
            "接尾辞,名詞的,一般,*,*,*,ソ,素,素,ソ,素,ソ,漢,*,*,*,*,ソ,ソ,ソ,ソ,*,*,*,C3,*",
            "名詞,普通名詞,サ変可能,*,*,*,カイセキ,解析,解析,カイセキ,解析,カイセキ,漢,*,*,*,*,カイセキ,カイセキ,カイセキ,カイセキ,*,*,0,C2,*",
            "助詞,係助詞,*,*,*,*,ハ,は,は,ワ,は,ワ,和,*,*,*,*,ハ,ハ,ハ,ハ,*,*,*,\"動詞%F2@0,名詞%F1,形容詞%F2@-1\",*",
            "形容詞,一般,*,*,形容詞,終止形-一般,オモシロイ,面白い,面白い,オモシロイ,面白い,オモシロイ,和,*,*,*,*,オモシロイ,オモシロイ,オモシロイ,オモシロイ,*,*,4,C1,*"
        };

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurfaceForm());
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testUnknownWord() {
        List<Token> tokens = tokenizer.tokenize("Google");
        String expectedFeatures = "名詞,普通名詞,一般,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,*";

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testUserDictionary() throws IOException {
        String entries = "北斗の拳,北斗の拳,ホクトノケン,カスタム名詞";

        buildTokenizerWithUserDictionary(entries);
        List<Token> tokens = tokenizer.tokenize("北斗の拳は非常に面白かった。");

        String expectedSurface = "北斗の拳";
        String expectedFeatures = "カスタム名詞,*,*,*,*,*,*,*,*,*,*,*,*,ホクトノケン,*,*,*,*,*,*,*,*";

        assertEquals(expectedSurface, tokens.get(0).getSurfaceForm());
        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testKansaiInternationalAirport() {
        List<Token> tokens = tokenizer.tokenize("関西国際空港");

        String[] expectedSurfaces = {
            "関西",
            "国際",
            "空港"
        };

        String[] expectedFeatures = {
            "名詞,固有名詞,地名,一般,*,*,カンサイ,カンサイ,関西,カンサイ,関西,カンサイ,固,*,*,*,*,カンサイ,カンサイ,カンサイ,カンサイ,*,*,1,*,*",
            "名詞,普通名詞,一般,*,*,*,コクサイ,国際,国際,コクサイ,国際,コクサイ,漢,*,*,*,*,コクサイ,コクサイ,コクサイ,コクサイ,*,*,0,C2,*",
            "名詞,普通名詞,一般,*,*,*,クウコウ,空港,空港,クーコー,空港,クーコー,漢,*,*,*,*,クウコウ,クウコウ,クウコウ,クウコウ,*,*,0,C2,*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurfaceForm());
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
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
            assertEquals(posLevel1[i], tokens.get(i).getPosLevel1());
            assertEquals(posLevel2[i], tokens.get(i).getPosLevel2());
            assertEquals(posLevel3[i], tokens.get(i).getPosLevel3());
            assertEquals(posLevel4[i], tokens.get(i).getPosLevel4());
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
    public void testKanaAndKanaBaseAndFormAndFormBase() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        String[] expectedKana = new String[]{"オ", "スシ", "ガ", "タベ", "タイ"};
        String[] expectedKanaBase = new String[]{"オ", "スシ", "ガ", "タベル", "タイ"};
        String[] expectedForm = new String[]{"オ", "スシ", "ガ", "タベ", "タイ"};
        String[] expectedFormBase = new String[]{"オ", "スシ", "ガ", "タベル", "タイ"};

        assertEquals(expectedKana.length, tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedKana[i], tokens.get(i).getKana());
            assertEquals(expectedKanaBase[i], tokens.get(i).getKanaBase());
            assertEquals(expectedForm[i], tokens.get(i).getForm());
            assertEquals(expectedFormBase[i], tokens.get(i).getFormBase());
        }
    }

    @Test
    public void testConnectionTypes() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい");

        // Todo: Should have a more interesting test sample here
        String[] expectedInitialConnectionTypes = new String[]{"*", "*", "*", "*", "*"};
        String[] expectedFinalConnectionTypes = new String[]{"*", "*", "*", "*", "*"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedInitialConnectionTypes[i], tokens.get(i).getInitialConnectionType());
            assertEquals(expectedFinalConnectionTypes[i], tokens.get(i).getFinalConnectionType());
        }
    }

    @Test
    public void testAccentTypes() throws Exception {
        List<Token> tokens = tokenizer.tokenize("お寿司を造ろう");

        String[] expectedAccentTypes = new String[]{"*", "1,2", "*", "2"};
        String[] expectedAccentConnectionTypes = new String[]{"P2", "C3", "動詞%F2@0,名詞%F1,形容詞%F2@-1", "C1"};
        String[] expectedAccentModificationTypes = new String[]{"*", "*", "*", "M1@1"};

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedAccentTypes[i], tokens.get(i).getAccentType());
            assertEquals(expectedAccentConnectionTypes[i], tokens.get(i).getAccentConnectionType());
            assertEquals(expectedAccentModificationTypes[i], tokens.get(i).getAccentModificationType());
        }
    }

    @Test
    public void testFeatureLengths() throws IOException {
        assertEqualTokenFeatureLenghts("ahgsfdajhgsfdこの丘はアクロポリスと呼ばれている。", tokenizer);
    }

    @Test
    public void testNewBocchan() throws IOException {
        assertTokenizedStreamEquals(
            getClass().getResourceAsStream("/bocchan-unidic-kanaaccent-features.txt"),
            getClass().getResourceAsStream("/bocchan.txt"),
            tokenizer
        );
    }

    @Test
    public void testQuotedFeature() {
        List<Token> tokens = tokenizer.tokenize("合い方");

        assertEquals(1, tokens.size());

        Token token = tokens.get(0);

        // Feature is escaped in quotes ("0,4")
        assertEquals(
            "名詞,普通名詞,一般,*,*,*,アイカタ,合方,合い方,アイカタ,合い方,アイカタ,和,*,*,*,*,アイカタ,アイカタ,アイカタ,アイカタ,*,*,\"0,4\",C2,*",
            token.getAllFeatures()
        );

        // Feature is not quoted (0,4)
        assertEquals("0,4", token.getAccentType());
    }

    private void buildTokenizerWithUserDictionary(String userDictionaryEntry) throws IOException {
        tokenizer = new Tokenizer.Builder().userDictionary(getUserDictionaryFromString(userDictionaryEntry)).build();
    }

    private ByteArrayInputStream getUserDictionaryFromString(String userDictionaryEntry) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(userDictionaryEntry.getBytes("UTF-8"));
    }
}
