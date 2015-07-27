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
package com.atilika.kuromoji.util;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class DictionaryEntryLineParserTest {

    private DictionaryEntryLineParser parser = new DictionaryEntryLineParser();

    @Test
    public void testTrivial() {
        assertArrayEquals(
            new String[]{
                "日本経済新聞", "日本 経済 新聞", "ニホン ケイザイ シンブン", "カスタム名詞"
            },
            parser.parseLine("日本経済新聞,日本 経済 新聞,ニホン ケイザイ シンブン,カスタム名詞")
        );
    }

    @Test
    public void testQuotes() {
        assertArrayEquals(
            new String[]{
                "Java Platform, Standard Edition",
                "Java Platform, Standard Edition",
                "Java Platform, Standard Edition",
                "カスタム名詞"
            },
            parser.parseLine(
                "\"Java Platform, Standard Edition\",\"Java Platform, Standard Edition\",\"Java Platform, Standard Edition\",カスタム名詞"
            )
        );
    }

    @Ignore("To be implemented and verified")
    @Test
    public void testEscapedQuote() {
        String[] entries = parser.parseLine(
            "\\\",\\\",\\\",カスタム品詞"
        );

        System.out.println("entries: " + entries.length);

        for (String entry : entries) {
            System.out.println(entry);
        }
//        System.out.println(Arrays.toString(entries));
    }
}
