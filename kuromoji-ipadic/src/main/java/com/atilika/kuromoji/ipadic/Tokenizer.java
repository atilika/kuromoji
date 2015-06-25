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
package com.atilika.kuromoji.ipadic;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.PrefixDecoratorResolver;
import com.atilika.kuromoji.TokenizerRunner;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;
import com.atilika.kuromoji.viterbi.ViterbiNode;

import java.io.IOException;
import java.util.ArrayList;

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

    public static void main(String[] args) throws IOException {
        Tokenizer tokenizer;
        switch (args.length) {
            case 1:
                Tokenizer.Mode mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
                tokenizer = new Builder().mode(mode).build();
                break;
            case 2:
                mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
                tokenizer = new Builder().mode(mode).userDictionary(args[1]).build();
                break;
            default:
                tokenizer = new Builder().build();
                break;
        }
        new TokenizerRunner().run(tokenizer);
    }

    /**
     * Builder class used to create Tokenizer instance.
     */
    public static class Builder extends AbstractTokenizer.Builder {

        /**
         * ipadic-specific search mode settings
         */
        private Integer searchModeKanjiLength;
        private Integer searchModeKanjiPenalty;
        private Integer searchModeOtherLength;
        private Integer searchModeOtherPenalty;

        public Builder() {
            totalFeatures = 9;
            unknownDictionaryTotalFeatures = 9;
            readingFeature = 7;
            partOfSpeechFeature = 0;
            defaultPrefix = System.getProperty(DEFAULT_DICT_PREFIX_PROPERTY, "com/atilika/kuromoji/ipadic/");
        }

        /**
         * Set tokenization mode
         * Default: NORMAL
         *
         * @param mode tokenization mode
         * @return Builder
         */
        public synchronized Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public synchronized Builder penalties(int kanjiLength, int kanjiPenalty, int otherLength, int otherPenalty) {
            this.searchModeKanjiLength = kanjiLength;
            this.searchModeKanjiPenalty = kanjiPenalty;
            this.searchModeOtherLength = otherLength;
            this.searchModeOtherPenalty = otherPenalty;
            return this;
        }

        @Override
        public void loadDictionaries() {
            if (this.mode != Mode.NORMAL
                && searchModeKanjiLength != null && searchModeKanjiPenalty != null
                && searchModeOtherLength != null && searchModeOtherPenalty != null) {
                penalties = new ArrayList<Integer>();
                penalties.add(searchModeKanjiLength);
                penalties.add(searchModeKanjiPenalty);
                penalties.add(searchModeOtherLength);
                penalties.add(searchModeOtherPenalty);
            }

            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            try {
                doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
                connectionCosts = ConnectionCosts.newInstance(resolver);
                tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
                unknownDictionary = UnknownDictionary.newInstance(resolver, 9);
                insertedDictionary = new InsertedDictionary(9);
            } catch (Exception ouch) {
                throw new RuntimeException("Could not load dictionaries.", ouch);
            }
        }


        @Override
        public synchronized Tokenizer build() {
            return new Tokenizer(this);
        }

    }

}
