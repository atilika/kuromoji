/**
 * Copyright 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.naist.jdic.dict;

public class DictionaryField {

    /**
     * Entry format for NAIST-JDIC in output from mecab:
     * 表層形\t品詞,品詞細分類1,品詞細分類2,品詞細分類3,活用型,活用形,原形,読み,発音,表記ゆれ,複合語情報
     *
     * Surface, POS, POS detailed 1, POS detailed 2, POS detailed 3, conjugation type, conjugation form, base form reading, pronunciation
     *
     * 0:   surface form
     * 1:   leftId
     * 2:   rightId
     * 3:   wordcost
     * 4:   Part of speech (POS)
     * 5:   POS detailed 1
     * 6:   POS detailed 2
     * 7:   POS detailed 3
     * 8:   conjugation type
     * 9:   conjugation form
     * 10:  base form
     * 11:  reading
     * 12:  pronunciation
     * 13:  transcription variation
     * 14:  compound information
     *
     * Surface form, leftId, rightId and wordCost (fields 0, 1, 2, 3) are handled in the core
     * and do not need to be specified here.
     *
     */

    public static final int POS_LEVEL_1 = 4;
    public static final int POS_LEVEL_2 = 5;
    public static final int POS_LEVEL_3 = 6;
    public static final int POS_LEVEL_4 = 7;
    public static final int CONJUGATION_TYPE = 8;
    public static final int CONJUGATION_FORM = 9;
    public static final int BASE_FORM = 10;
    public static final int READING = 11;
    public static final int PRONUNCIATION = 12;
    public static final int TRANSCRIPTION_VARIATION = 13;
    public static final int COMPOUND_INFORMATION = 14;
}
