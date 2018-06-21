/**
 * Copyright © 2010-2018 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.unidic.neologd;

import com.atilika.kuromoji.TokenBase;
import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.unidic.neologd.compile.DictionaryEntry;
import com.atilika.kuromoji.viterbi.ViterbiNode;

/**
 * UniDic NEologd token produced by the UniDic NEologd tokenizer with various morphological features
 */
public class Token extends TokenBase {
    public Token(int wordId,
                 String surface,
                 ViterbiNode.Type type,
                 int position,
                 Dictionary dictionary) {
        super(wordId, surface, type, position, dictionary);
    }

    /**
     * Gets the 1st level part-of-speech tag for this token (品詞大分類)
     *
     * @return 1st level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel1() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_1);
    }

    /**
     * Gets the 2nd level part-of-speech tag for this token (品詞中分類)
     *
     * @return 2nd level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel2() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_2);
    }

    /**
     * Gets the 3rd level part-of-speech tag for this token (品詞小分類)
     *
     * @return 3rd level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel3() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_3);
    }

    /**
     * Gets the 4th level part-of-speech tag for this token (品詞細分類)
     *
     * @return 4th level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel4() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_4);
    }

    /**
     * Gets the conjugation form for this token (活用形), if applicable
     * <p>
     * If this token does not have a conjugation form, return *
     *
     * @return conjugation form, not null
     */
    public String getConjugationForm() {
        return getFeature(DictionaryEntry.CONJUGATION_FORM);
    }

    /**
     * Gets the conjugation type for this token (活用型), if applicable
     * <p>
     * If this token does not have a conjugation type, return *
     *
     * @return conjugation type, not null
     */
    public String getConjugationType() {
        return getFeature(DictionaryEntry.CONJUGATION_TYPE);
    }

    /**
     * Return the lemma reading form for this token (語彙素読み)
     *
     * @return lemma reading form, not null
     */
    public String getLemmaReadingForm() {
        return getFeature(DictionaryEntry.LEMMA_READING_FORM);
    }

    /**
     * Gets the lemma for this token (語彙素表記)
     *
     * @return lemma, not null
     */
    public String getLemma() {
        return getFeature(DictionaryEntry.LEMMA);
    }

    /**
     * Gets the pronunciation for this token (発音)
     *
     * @return pronunciation, not null
     */
    public String getPronunciation() {
        return getFeature(DictionaryEntry.PRONUNCIATION);
    }

    /**
     * Gets the pronunciation base form for this token (発音形基本形)
     *
     * @return pronunciation base form, not null
     */
    public String getPronunciationBaseForm() {
        return getFeature(DictionaryEntry.PRONUNCIATION_BASE_FORM);
    }

    /**
     * Gets the written form for this token (書字形)
     *
     * @return written form, not null
     */
    public String getWrittenForm() {
        return getFeature(DictionaryEntry.WRITTEN_FORM);
    }

    /**
     * Gets the written base form of this token (書字形出現形)
     *
     * @return written base form, not null
     */
    public String getWrittenBaseForm() {
        return getFeature(DictionaryEntry.WRITTEN_BASE_FORM);
    }

    /**
     * Returns the language type of this token (語種)
     *
     * @return language type, not null
     */
    public String getLanguageType() {
        return getFeature(DictionaryEntry.LANGUAGE_TYPE);
    }

    /**
     * Returns the initial sound alteration type for the token (語頭変化型)
     *
     * @return initial sound alteration type, not null
     */
    public String getInitialSoundAlterationType() {
        return getFeature(DictionaryEntry.INITIAL_SOUND_ALTERATION_TYPE);
    }

    /**
     * Returns the initial sound alteration form for the token (語頭変化形)
     *
     * @return initial sound alteration form, not null
     */
    public String getInitialSoundAlterationForm() {
        return getFeature(DictionaryEntry.INITIAL_SOUND_ALTERATION_FORM);
    }

    /**
     * Returns the final sound alteration type for the token (語末変化型)
     *
     * @return final sound alteration type, not null
     */
    public String getFinalSoundAlterationType() {
        return getFeature(DictionaryEntry.FINAL_SOUND_ALTERATION_TYPE);
    }

    /**
     * Returns the final sound alteration form for the token (語末変化形)
     *
     * @return final sound alteration form, not null
     */
    public String getFinalSoundAlterationForm() {
        return getFeature(DictionaryEntry.FINAL_SOUND_ALTERATION_FORM);
    }
}
