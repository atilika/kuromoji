/**
 * This software is the property of Atilika Inc.  It can only be used
 * as described in the Software License Agreement and in no other way.
 *
 * Copyright © 2012 Atilika Inc.  All rights reserved.
 */

package com.atilika.kuromoji.util;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class CSVUtilTest {

    // TODO: these tests should be checked, right now they are documenting what is happening.
    @Test
    public void testParseInputString() throws Exception {
        String input = "日本経済新聞,1292,1292,4980,名詞,固有名詞,組織,*,*,*,日本経済新聞,ニホンケイザイシンブン,ニホンケイザイシンブン";
        String expected = Arrays.deepToString(new String[]{"日本経済新聞" ,"1292" ,"1292", "4980",
                "名詞", "固有名詞", "組織", "*", "*", "*", "日本経済新聞", "ニホンケイザイシンブン" ,"ニホンケイザイシンブン"});
        assertEquals(expected, given(input));
    }

    @Test
    public void testParseInputStringWithQuotes() throws Exception {
        String input = "日本経済新聞,1292,1292,4980,名詞,固有名詞,組織,*,*,\"1,0\",日本経済新聞,ニホンケイザイシンブン,ニホンケイザイシンブン";
        String expected = Arrays.deepToString(new String[]{"日本経済新聞" ,"1292" ,"1292", "4980",
                "名詞", "固有名詞", "組織", "*", "*", "1,0", "日本経済新聞", "ニホンケイザイシンブン" ,"ニホンケイザイシンブン"});
        assertEquals(expected, given(input));
    }

    @Test
    public void testQuoteEscape() throws Exception {
        String input = "日本経済新聞,1292,1292,4980,名詞,固有名詞,組織,*,*,\"1,0\",日本経済新聞,ニホンケイザイシンブン,ニホンケイザイシンブン";
        String expected = "\"日本経済新聞,1292,1292,4980,名詞,固有名詞,組織,*,*,\"\"1,0\"\",日本経済新聞,ニホンケイザイシンブン,ニホンケイザイシンブン\"";
        assertEquals(expected, CSVUtil.quoteEscape(input));
    }

    private String given(String input) {
        return Arrays.deepToString(CSVUtil.parse(input));
    }
}
