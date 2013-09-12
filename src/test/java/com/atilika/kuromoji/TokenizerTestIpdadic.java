/**
 * This software is the property of Atilika Inc.  It can only be used
 * as described in the Software License Agreement and in no other way.
 *
 * Copyright © 2013 Atilika Inc.  All rights reserved.
 */

package com.atilika.kuromoji;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenizerTestIpdadic {
    private static Tokenizer tokenizer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tokenizer = Tokenizer
            .builder()
            .build();
    }

    @Test
    public void testBocchanTokenizationForIpadic() throws IOException {
        int numberOfTokens = 10000;
        String expected = expect("bocchan-tokenized-features-ipadic.utf-8.txt", numberOfTokens);
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
                result += token.getSurfaceForm().trim() + "\t" + token.getAllFeatures() + "\n";
            }
            numberOfLines++;
        }
        return result;
    }}
