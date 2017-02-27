/**
 * Copyright © 2010-2017 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

public class ScriptUtils {

    /**
     * Predicate denoting if input is all katakana characters
     *
     * @param string  input string
     * @return true if input is all katakana characters
     */
    public static boolean isKatakana(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!isFullWidthKatakana(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Full-width katakana predicate
     *
     * @param c  character to test
     * @return true if and only if c is a full-width katakana character
     */
    public static boolean isFullWidthKatakana(char c) {
        return isUnicodeBlock(c, Character.UnicodeBlock.KATAKANA);
    }

    /**
     * Predicate denoting if input is all hiragana characters
     *
     * @param string  input string
     * @return true if input is all hiragana characters
     */
    public static boolean isHiragana(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!isHiragana(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Hiragana predicate
     *
     * @param c  character to test
     * @return true if and only if c is a hiragana character
     */
    public static boolean isHiragana(char c) {
        return isUnicodeBlock(c, Character.UnicodeBlock.HIRAGANA);
    }

    /**
     * Unicode block predicate
     *
     * @param c  character to test
     * @param block  Unicode block to test
     * @return true if and only if c is of Unicode block block
     */
    private static boolean isUnicodeBlock(char c, Character.UnicodeBlock block) {
        return Character.UnicodeBlock.of(c) == block;
    }
}
