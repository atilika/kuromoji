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

package com.atilika.kuromoji.unidic;

import com.atilika.kuromoji.Token;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import static junit.framework.Assert.assertEquals;

// TODO: Check witch version of unidic is used for test result. Currently there are some segmentation differences.
@Ignore
public class TokenizerTest {

    private static Tokenizer tokenizer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tokenizer = new Tokenizer
            .Builder()
//            .prefix("unidic/")
            .build();
    }

    @Test
    public void testFirstEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("¡");

        String expectedFeatures = "補助記号,一般,*,*,*,*,,¡,¡,,¡,,記号,*,*,*,*";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testLastEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("ヴィ");

        String expectedFeatures = "記号,一般,*,*,*,*,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,記号,*,*,*,*";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
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
            "名詞,固有名詞,地名,一般,*,*,カンサイ,カンサイ,関西,カンサイ,関西,カンサイ,固,*,*,*,*",
            "名詞,普通名詞,一般,*,*,*,コクサイ,国際,国際,コクサイ,国際,コクサイ,漢,*,*,*,*",
            "名詞,普通名詞,一般,*,*,*,クウコウ,空港,空港,クーコー,空港,クーコー,漢,*,*,*,*"
        };

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurfaceForm());
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testNucleus() {
        List<Token> tokens = tokenizer.tokenize("核");

        String expectedSurface = "核";
        String expectedFeatures = "名詞,普通名詞,一般,*,*,*,カク,核,核,カク,核,カク,漢,*,*,ク促,基本形";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }

        assertEquals(expectedSurface, tokens.get(0).getSurfaceForm());
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
    public void testBocchanTokenizationForUniDic() throws IOException {
        int numberOfTokens = 10000;
        String expected = expect("bocchan-tokenized-unidic.utf-8.txt", numberOfTokens);
        assertEquals(expected, given("bocchan.utf-8.txt", numberOfTokens));
    }

    private String expect(String s, int numberOfTokens) throws IOException {
        String expected = "";
        String line;
        int numberOfLines = 0;
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(s)));
        while ((line = reader.readLine()) != null && (numberOfLines < numberOfTokens || numberOfTokens == 0)) {
            expected += line.trim() + "\n";
            numberOfLines++;
        }
        reader.close();
        return expected;
    }

    private String given(String input, int numberOfTokens) throws IOException {
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(input)));
        String content = reader.readLine();
        reader.close();
        List<Token> tokens = tokenizer.tokenize(content);
        String result = "";
        int numberOfLines = 0;
        for (Token token : tokens) {
            if (numberOfTokens == 0 || numberOfLines < numberOfTokens) {
                result += token.getSurfaceForm().trim() + "\n";
            }
            numberOfLines++;
        }
        return result;
    }
}
