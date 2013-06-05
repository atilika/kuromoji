/**
 * This software is the property of Atilika Inc.  It can only be used
 * as described in the Software License Agreement and in no other way.
 *
 * Copyright © 2013 Atilika Inc.  All rights reserved.
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
