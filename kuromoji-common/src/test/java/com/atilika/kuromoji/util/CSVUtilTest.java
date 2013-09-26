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
package com.atilika.kuromoji.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CSVUtilTest {

    // TODO: these tests should be checked, right now they are documenting what is happening.
    @Test
    public void testParseInputString() throws Exception {
        String input = "日本経済新聞,1292,1292,4980,名詞,固有名詞,組織,*,*,*,日本経済新聞,ニホンケイザイシンブン,ニホンケイザイシンブン";
        String expected = Arrays.deepToString(new String[]{"日本経済新聞", "1292", "1292", "4980",
            "名詞", "固有名詞", "組織", "*", "*", "*", "日本経済新聞", "ニホンケイザイシンブン", "ニホンケイザイシンブン"});
        assertEquals(expected, given(input));
    }

    @Test
    public void testParseInputStringWithQuotes() throws Exception {
        String input = "日本経済新聞,1292,1292,4980,名詞,固有名詞,組織,*,*,\"1,0\",日本経済新聞,ニホンケイザイシンブン,ニホンケイザイシンブン";
        String expected = Arrays.deepToString(new String[]{"日本経済新聞", "1292", "1292", "4980",
            "名詞", "固有名詞", "組織", "*", "*", "1,0", "日本経済新聞", "ニホンケイザイシンブン", "ニホンケイザイシンブン"});
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
