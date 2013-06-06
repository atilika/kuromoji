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
package com.atilika.kuromoji;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class UserDictionaryTokenizerTest {

    private static Tokenizer tokenizer;
    @Before
    public void setUp() throws Exception {
        String userDictionaryEntry = "クロ,クロ,クロ,カスタム名詞";
        tokenizer = Tokenizer.builder().userDictionary(getUserDictionaryFromString(userDictionaryEntry)).build();
    }

    private ByteArrayInputStream getUserDictionaryFromString(String userDictionaryEntry) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(userDictionaryEntry.getBytes("UTF-8"));
    }

    @Ignore
    @Test
    public void testAcropolis() {
        String input = "この丘はアクロポリスと呼ばれている。";
        String[] surfaceForms = {"この", "丘", "は", "ア", "クロ", "ポリス", "と", "呼ば", "れ", "て", "いる", "。"};
        List<Token> tokens = tokenizer.tokenize(input);
        System.out.println(tokens);
        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm());
        }
        assertEquals(surfaceForms.length, tokens.size());
    }

}
