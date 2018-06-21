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
package com.atilika.kuromoji.jumandic;

import com.atilika.kuromoji.TokenBase;
import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.jumandic.compile.DictionaryEntry;
import com.atilika.kuromoji.viterbi.ViterbiNode;

/**
 * JUMANDIC token produced by the JUMANDIC tokenizer with various morphological features
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
     * Gets the 1st level part-of-speech tag for this token (品詞細分類1)
     *
     * @return 1st level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel1() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_1);
    }

    /**
     * Gets the 2nd level part-of-speech tag for this token (品詞細分類2)
     *
     * @return 2nd level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel2() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_2);
    }

    /**
     * Gets the 3rd level part-of-speech tag for this token (品詞細分類3)
     *
     * @return 3rd level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel3() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_3);
    }

    /**
     * Gets the 4th level part-of-speech tag for this token (品詞細分類4)
     *
     * @return 4th level part-of-speech tag, not null
     */
    public String getPartOfSpeechLevel4() {
        return getFeature(DictionaryEntry.PART_OF_SPEECH_LEVEL_4);
    }

    /**
     * Gets the base form (also called dictionary form) for this token (基本形)
     *
     * @return base form, not null
     */
    public String getBaseForm() {
        return getFeature(DictionaryEntry.BASE_FORM);
    }

    /**
     * Gets the reading for this token (読み) in katakana script
     *
     * @return reading, not null
     */
    public String getReading() {
        return getFeature(DictionaryEntry.READING);
    }

    /**
     * Gets the semantic information for this token (代表表記)
     *
     * @return semantic information, not null
     */
    public String getSemanticInformation() {
        return getFeature(DictionaryEntry.SEMANTIC_INFORMATION);
    }
}
