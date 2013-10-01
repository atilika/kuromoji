/**
 * This software is the property of Atilika Inc.  It can only be used
 * as described in the Software License Agreement and in no other way.
 *
 * Copyright © 2013 Atilika Inc.  All rights reserved.
 */

package com.atilika.kuromoji.naist.jdic;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.Token;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = new Tokenizer.Builder().build();
    }

    @Test
    public void testFirstEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize(" ");

        String expectedFeatures = "記号,空白,*,*,*,*,　,　,　,,";
        
        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
    }

    @Test
    public void testKansaiInternationalAirport() {
        List<Token> tokens = tokenizer.tokenize("関西国際空港");

        String expectedFeatures = "名詞,固有名詞,組織,*,*,*,関西国際空港,カンサイコクサイクウコウ,カンサイコクサイクーコー,,";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
    }

    @Test
    public void testSearchModeKansaiInternationalAirport() {
        tokenizer = new Tokenizer.Builder().mode(AbstractTokenizer.Mode.SEARCH).build();

        List<Token> tokens = tokenizer.tokenize("関西国際空港");

//        String expectedFeatures = "名詞,固有名詞,組織,*,*,*,関西国際空港,カンサイコクサイクウコウ,カンサイコクサイクーコー,,";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
    }

    @Test
    public void testLastEntryCornerCase() {
        List<Token> tokens = tokenizer.tokenize("鳥肉");

        String expectedFeatures = "名詞,一般,*,*,*,*,鳥肉,トリニク,トリニク,,";

        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + ": " + token.getAllFeatures());
        }
    }

}
