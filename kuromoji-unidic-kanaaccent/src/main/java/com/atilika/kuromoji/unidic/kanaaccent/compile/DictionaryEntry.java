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
package com.atilika.kuromoji.unidic.kanaaccent.compile;

import com.atilika.kuromoji.dict.AbstractDictionaryEntry;

import static com.atilika.kuromoji.dict.DictionaryField.LEFT_ID;
import static com.atilika.kuromoji.dict.DictionaryField.RIGHT_ID;
import static com.atilika.kuromoji.dict.DictionaryField.SURFACE;
import static com.atilika.kuromoji.dict.DictionaryField.WORD_COST;

public class DictionaryEntry extends AbstractDictionaryEntry {
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

    public static final int TOTAL_FEATURES = 26;
    public static final int READING_FEATURE = 13;
    public static final int PART_OF_SPEECH_FEATURE = 0;

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

    private final String kana;
    private final String kanaBase;
    private final String form;
    private final String formBase;
    private final String initialConnectionType;
    private final String finalConnectionType;

    private final String accentType;
    private final String accentConnectionType;
    private final String accentModificationType;

    public DictionaryEntry(String[] fields) {
        super(fields[SURFACE],
            Short.parseShort(fields[LEFT_ID]),
            Short.parseShort(fields[RIGHT_ID]),
            Short.parseShort(fields[WORD_COST])
        );

        posLevel1 = fields[POS_LEVEL_1];
        posLevel2 = fields[POS_LEVEL_2];
        posLevel3 = fields[POS_LEVEL_3];
        posLevel4 = fields[POS_LEVEL_4];

        conjugationType = fields[CONJUGATION_TYPE];
        conjugationForm = fields[CONJUGATION_FORM];

        lemmaReadingForm = fields[LEMMA_READING_FORM];
        lemma = fields[LEMMA];
        writtenForm = fields[WRITTEN_FORM];

        pronunciation = fields[PRONUNCIATION];
        writtenBaseForm = fields[WRITTEN_BASE_FORM];

        pronunciationBaseForm = fields[PRONUNCIATION_BASE_FORM];
        languageType = fields[LANGUAGE_TYPE];
        initialSoundAlterationType = fields[INITIAL_SOUND_ALTERATION_TYPE];
        initialSoundAlterationForm = fields[INITIAL_SOUND_ALTERATION_FORM];
        finalSoundAlterationType = fields[FINAL_SOUND_ALTERATION_TYPE];
        finalSoundAlterationForm = fields[FINAL_SOUND_ALTERATION_FORM];

        kana = fields[KANA];
        kanaBase = fields[KANA_BASE];
        form = fields[FORM];
        formBase = fields[FORM_BASE];
        initialConnectionType = fields[INITIAL_CONNECTION_TYPE];
        finalConnectionType = fields[FINAL_CONNECTION_TYPE];

        accentType = fields[ACCENT_TYPE];
        accentConnectionType = fields[ACCENT_CONNECTION_TYPE];
        accentModificationType = fields[ACCENT_MODIFICATION_TYPE];
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

    public String getKana() {
        return kana;
    }

    public String getKanaBase() {
        return kanaBase;
    }

    public String getForm() {
        return form;
    }

    public String getFormBase() {
        return formBase;
    }

    public String getInitialConnectionType() {
        return initialConnectionType;
    }

    public String getFinalConnectionType() {
        return finalConnectionType;
    }

    public String getAccentType() {
        return accentType;
    }

    public String getAccentConnectionType() {
        return accentConnectionType;
    }

    public String getAccentModificationType() {
        return accentModificationType;
    }
}
