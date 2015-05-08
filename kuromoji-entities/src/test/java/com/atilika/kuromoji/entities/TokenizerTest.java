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

package com.atilika.kuromoji.entities;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = new Tokenizer.Builder().build();
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

        String[] expectedSurfaces = {"関西", "国際", "空港"};

        String[] expectedFeatures = {
            "名詞,固有名詞,地名,一般,*,*,カンサイ,カンサイ,関西,カンサイ,関西,カンサイ,固,*,*,*,*",
            "名詞,普通名詞,一般,*,*,*,コクサイ,国際,国際,コクサイ,国際,コクサイ,漢,*,*,*,*",
            "名詞,普通名詞,一般,*,*,*,クウコウ,空港,空港,クーコー,空港,クーコー,漢,*,*,*,*"
        };

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurfaceForm());
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    @Test
    public void testRomajiSegmentation() {
        List<Token> tokens = tokenizer.tokenize("1234");

        // If any of the assertions below fail, the dictionary filter wasn't applies correctly
        assertEquals(1, tokens.size());

        Token token = tokens.get(0);

        assertEquals("1234", token.getSurfaceForm());
        assertTrue(token.isUnknown());
    }
}
