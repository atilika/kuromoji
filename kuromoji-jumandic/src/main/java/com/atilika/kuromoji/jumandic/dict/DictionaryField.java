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
package com.atilika.kuromoji.jumandic.dict;

public class DictionaryField {

    /**
     *
     * Entry format for Jumandic in output from mecab:
     * 表層形\t品詞,品詞細分類1,品詞細分類2,品詞細分類3,原形,読み,代表表記
     *
     * Surface, POS, POS detailed 1, POS detailed 2, POS detailed 3, base form, reading, representation
     *
     * 0:   surface form
     * 1:   leftId
     * 2:   rightId
     * 3:   wordcost
     * 4:   Part of speech (POS)
     * 5:   POS detailed 1
     * 6:   POS detailed 2
     * 7:   POS detailed 3
     * 8:   base form
     * 9:   reading
     * 10:  representation
     *
     */

    public static final int POS_LEVEL_1 = 4;
    public static final int POS_LEVEL_2 = 5;
    public static final int POS_LEVEL_3 = 6;
    public static final int POS_LEVEL_4 = 7;
    public static final int BASE_FORM = 8;
    public static final int READING = 9;
    public static final int SEMANTIC_INFORMATION = 10;
}
