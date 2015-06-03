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

package com.atilika.kuromoji.naist.jdic;

import com.atilika.kuromoji.AbstractToken;
import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.naist.jdic.dict.DictionaryField;
import com.atilika.kuromoji.viterbi.ViterbiNode;

public class Token extends AbstractToken {

    public Token(int wordId, String surfaceForm, ViterbiNode.Type type, int position, Dictionary dictionary) {
        super(wordId, surfaceForm, type, position, dictionary);
    }

    public String getPronunciation() {
        return this.getFeature(DictionaryField.PRONUNCIATION);
    }

    public String getPosLevel1() {
        return this.getFeature(DictionaryField.POS_LEVEL_1);
    }

    public String getPosLevel2() {
        return this.getFeature(DictionaryField.POS_LEVEL_2);
    }

    public String getPosLevel3() {
        return this.getFeature(DictionaryField.POS_LEVEL_3);
    }

    public String getPosLevel4() {
        return this.getFeature(DictionaryField.POS_LEVEL_4);
    }

    public String getConjugationType() {
        return this.getFeature(DictionaryField.CONJUGATION_TYPE);
    }

    public String getConjugationForm() {
        return this.getFeature(DictionaryField.CONJUGATION_FORM);
    }

    public String getTranscriptionVariation() {
        return this.getFeature(DictionaryField.TRANSCRIPTION_VARIATION);
    }

    public String getCompoundInformation() {
        return this.getFeature(DictionaryField.COMPOUND_INFORMATION);
    }
}
