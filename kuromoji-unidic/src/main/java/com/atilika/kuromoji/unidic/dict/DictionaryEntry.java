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
package com.atilika.kuromoji.unidic.dict;

import com.atilika.kuromoji.dict.AbstractDictionaryEntry;

import static com.atilika.kuromoji.dict.DictionaryField.LEFT_ID;
import static com.atilika.kuromoji.dict.DictionaryField.RIGHT_ID;
import static com.atilika.kuromoji.dict.DictionaryField.SURFACE;
import static com.atilika.kuromoji.dict.DictionaryField.WORD_COST;

public class DictionaryEntry extends AbstractDictionaryEntry {

    private final String posLevel1;
    private final String posLevel2;
    private final String posLevel3;
    private final String posLevel4;

    private final String conjugationForm;
    private final String conjugationType;

    private final String lemmaReadingForm;
    private final String lemma;
    private final String writtenForm;

    private final String pronunciation;
    private final String writtenBaseForm;
    private final String pronunciationBaseForm;
    private final String languageType;

    private final String initialSoundAlterationType;
    private final String initialSoundAlterationForm;
    private final String finalSoundAlterationType;
    private final String finalSoundAlterationForm;

    public DictionaryEntry(String[] fields) {
        super(fields[SURFACE],
            Short.parseShort(fields[LEFT_ID]),
            Short.parseShort(fields[RIGHT_ID]),
            Short.parseShort(fields[WORD_COST])
        );

        posLevel1 = fields[DictionaryField.POS_LEVEL_1];
        posLevel2 = fields[DictionaryField.POS_LEVEL_2];
        posLevel3 = fields[DictionaryField.POS_LEVEL_3];
        posLevel4 = fields[DictionaryField.POS_LEVEL_4];

        conjugationType = fields[DictionaryField.CONJUGATION_TYPE];
        conjugationForm = fields[DictionaryField.CONJUGATION_FORM];

        lemmaReadingForm = fields[DictionaryField.LEMMA_READING_FORM];
        lemma = fields[DictionaryField.LEMMA];
        writtenForm = fields[DictionaryField.WRITTEN_FORM];

        pronunciation = fields[DictionaryField.PRONUNCIATION];
        writtenBaseForm = fields[DictionaryField.WRITTEN_BASE_FORM];

        pronunciationBaseForm = fields[DictionaryField.PRONUNCIATION_BASE_FORM];
        languageType = fields[DictionaryField.LANGUAGE_TYPE];
        initialSoundAlterationType = fields[DictionaryField.INITIAL_SOUND_ALTERATION_TYPE];
        initialSoundAlterationForm = fields[DictionaryField.INITIAL_SOUND_ALTERATION_FORM];
        finalSoundAlterationType = fields[DictionaryField.FINAL_SOUND_ALTERATION_TYPE];
        finalSoundAlterationForm = fields[DictionaryField.FINAL_SOUND_ALTERATION_FORM];
    }


    public String getPosLevel1() {
        return posLevel1;
    }

    public String getPosLevel2() {
        return posLevel2;
    }

    public String getPosLevel3() {
        return posLevel3;
    }

    public String getPosLevel4() {
        return posLevel4;
    }

    public String getConjugationForm() {
        return conjugationForm;
    }

    public String getConjugationType() {
        return conjugationType;
    }

    public String getLemmaReadingForm() {
        return lemmaReadingForm;
    }

    public String getLemma() {
        return lemma;
    }

    public String getWrittenForm() {
        return writtenForm;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getWrittenBaseForm() {
        return writtenBaseForm;
    }

    public String getPronunciationBaseForm() {
        return pronunciationBaseForm;
    }

    public String getLanguageType() {
        return languageType;
    }

    public String getInitialSoundAlterationType() {
        return initialSoundAlterationType;
    }

    public String getInitialSoundAlterationForm() {
        return initialSoundAlterationForm;
    }

    public String getFinalSoundAlterationType() {
        return finalSoundAlterationType;
    }

    public String getFinalSoundAlterationForm() {
        return finalSoundAlterationForm;
    }
}
