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

package com.atilika.kuromoji.unidic.kanaaccent.dict;

public class DictionaryField {

    /**
     * Entry format for UNIDIC-extended in output from mecab:
     *
     * f[0]:  pos1
     * f[1]:  pos2
     * f[2]:  pos3
     * f[3]:  pos4
     * f[4]:  cType     conjugation type
     * f[5]:  cForm     conjugation form
     * f[6]:  lForm     lemma reading form
     * f[7]:  lemma     lemma (base form)
     * f[8]:  orth      written form (orthographic form?)
     * f[9]:  pron      pronunciation
     * f[10]: orthBase  orthographic base form
     * f[11]: pronBase  pronunciation base form
     * f[12]: goshu     language type
     * f[13]: iType     initial sound alteration type
     * f[14]: iForm     initial sound alteration form
     * f[15]: fType     final sound alteration type
     * f[16]: fForm     final sound alteration form
     * f[17]: kana
     * f[18]: kanaBase
     * f[19]: form
     * f[20]: formBase
     * f[21]: iConType
     * f[22]: fConType
     * f[23]: aType
     * f[24]: aConType
     * f[25]: aModType
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

    public static final int LEMMA_READING_FORM = 10;
    public static final int LEMMA = 11;

    public static final int WRITTEN_FORM = 12;
    public static final int PRONUNCIATION = 13;
    public static final int WRITTEN_BASE_FORM = 14;
    public static final int PRONUNCIATION_BASE_FORM = 15;

    public static final int LANGUAGE_TYPE = 16;
    public static final int INITIAL_SOUND_ALTERATION_TYPE = 17;
    public static final int INITIAL_SOUND_ALTERATION_FORM = 18;
    public static final int FINAL_SOUND_ALTERATION_TYPE = 19;
    public static final int FINAL_SOUND_ALTERATION_FORM = 20;

    public static final int KANA = 21;
    public static final int KANA_BASE = 22;
    public static final int FORM = 23;
    public static final int FORM_BASE = 24;
    public static final int INITIAL_CONNECTION_TYPE = 25;
    public static final int FINAL_CONNECTION_TYPE = 26;

    public static final int ACCENT_TYPE = 27;
    public static final int ACCENT_CONNECTION_TYPE = 28;
    public static final int ACCENT_MODIFICATION_TYPE = 29;
}
