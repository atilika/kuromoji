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
package com.atilika.kuromoji.unidic.kanaaccent;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.viterbi.ViterbiNode;

public class Tokenizer extends AbstractTokenizer {

    public Tokenizer() {
        this(new Builder());
    }

    public Tokenizer(Builder builder) {
        configure(builder);
    }

    @Override
    protected Token createToken(int offset, ViterbiNode node, int wordId) {
        return new Token(
            wordId,
            node.getSurfaceForm(),
            node.getType(),
            offset + node.getStartIndex(),
            dictionaryMap.get(node.getType())
        );
    }


    public static class Builder extends AbstractTokenizer.Builder {

        public Builder() {
            totalFeatures = 22;
            unknownDictionaryTotalFeatures = 26;
            readingFeature = 13;
            partOfSpeechFeature = 0;
            defaultPrefix = System.getProperty(DEFAULT_DICT_PREFIX_PROPERTY, "com/atilika/kuromoji/unidic-kanaaccent/");
        }

        @Override
        public synchronized Tokenizer build() {
            return new Tokenizer(this);
        }
    }
}
