/**
 * This software is the property of Atilika Inc.  It can only be used
 * as described in the Software License Agreement and in no other way.
 *
 * Copyright © 2013 Atilika Inc.  All rights reserved.
 */

package com.atilika.kuromoji.unidic.extended;

import com.atilika.kuromoji.Token;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = new Tokenizer.Builder().build();
    }

    @Test
    public void testFirstEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("¡");

        String expectedFeatures = "補助記号,一般,*,*,*,*,,¡,¡,,¡,,記号,*,*,*,*,,,,,*,*,*,*,*";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testLastEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("ヴィ");

        String expectedFeatures = "記号,一般,*,*,*,*,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,ヴィ,記号,*,*,*,*,ヴィ,ヴィ,ヴィ,ヴィ,*,*,1,*,*";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }

        assertEquals(expectedFeatures, tokens.get(0).getAllFeatures());
    }

    @Test
    public void testExtendedUnidic() {
        List<Token> tokens = tokenizer.tokenize("日本語の形態素解析は面白い");

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
    }

    @Test
    public void testUnknownWord() {
        List<Token> tokens = tokenizer.tokenize("Google");

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
    }

    @Test
    public void testUserDictionary() throws IOException {
        String entries = "北斗の拳,北斗の拳,ホクトノケン,カスタム名詞";

        buildTokenizerWithUserDictionary(entries);
        List<Token> tokens = tokenizer.tokenize("北斗の拳は非常に面白かった。");

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
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
            "名詞,普通名詞,一般,*,*,*,クウコウ,空港,空港,クーコー,空港,クーコー,漢,*,*,*,*,クウコウ,クウコウ,クウコウ,クウコウ,*,*,0,C2,*" };

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }

        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(expectedSurfaces[i], tokens.get(i).getSurfaceForm());
            assertEquals(expectedFeatures[i], tokens.get(i).getAllFeatures());
        }
    }

    private void buildTokenizerWithUserDictionary(String userDictionaryEntry) throws IOException {
        tokenizer = new Tokenizer.Builder().userDictionary(getUserDictionaryFromString(userDictionaryEntry)).build();
    }

    private ByteArrayInputStream getUserDictionaryFromString(String userDictionaryEntry) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(userDictionaryEntry.getBytes("UTF-8"));
    }
}
