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
package com.atilika.kuromoji.ipadic;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.PrefixDecoratorResolver;
import com.atilika.kuromoji.TokenizerRunner;
import com.atilika.kuromoji.dict.CharacterDefinitions;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;
import com.atilika.kuromoji.viterbi.ViterbiNode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A tokenizer based on the IPADIC dictionary
 * <p>
 * See {@link Token} for details on the morphological features produced by this tokenizer
 * <p>
 * The following code example demonstrates how to use the Kuromoji tokenizer:
 * <pre>{@code
 * package com.atilika.kuromoji.example;
 * import com.atilika.kuromoji.ipadic.Token;
 * import com.atilika.kuromoji.ipadic.Tokenizer;
 * import java.util.List;
 *
 * public class KuromojiExample {
 *     public static void main(String[] args) {
 *         Tokenizer tokenizer = new Tokenizer() ;
 *         List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");
 *         for (Token token : tokens) {
 *             System.out.println(token.getSurfaceForm() + "\t" + token.getAllFeatures());
 *         }
 *     }
 * }
 * }
 * </pre>
 */
public class Tokenizer extends AbstractTokenizer {

    /**
     * Class constructor constructing a default tokenizer
     */
    public Tokenizer() {
        this(new Builder());
    }

    /**
     * Class constructor constructing a customized tokenizer
     * <p>
     * See {@see com.atilika.kuromoji.ipadic.Tokenizer#Builder}
     */
    private Tokenizer(Builder builder) {
        configure(builder);
    }

    public static void main(String[] args) throws IOException {
        Tokenizer tokenizer;
        switch (args.length) {
            case 1:
                Tokenizer.Mode mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
                tokenizer = new Builder()
                    .mode(mode)
                    .build();
                break;
            case 2:
                mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
                tokenizer = new Builder()
                    .mode(mode)
                    .userDictionary(args[1])
                    .build();
                break;
            default:
                tokenizer = new Tokenizer();
                break;
        }
        new TokenizerRunner().run(tokenizer);
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

    /**
     * Builder class for creating a customized tokenizer instance
     */
    public static class Builder extends AbstractTokenizer.Builder {

        private static final int DEFAULT_KANJI_LENGTH_THRESHOLD = 2;
        private static final int DEFAULT_OTHER_LENGTH_THRESHOLD = 7;
        private static final int DEFAULT_KANJI_PENALTY = 3000;
        private static final int DEFAULT_OTHER_PENALTY = 1700;

        private int kanjiPenaltyLengthTreshold = DEFAULT_KANJI_LENGTH_THRESHOLD;
        private int kanjiPenalty = DEFAULT_KANJI_PENALTY;
        private int otherPenaltyLengthThreshold = DEFAULT_OTHER_LENGTH_THRESHOLD;
        private int otherPenalty = DEFAULT_OTHER_PENALTY;

        private boolean nakaguroSplit = false;

        /**
         * Creates a default builder
         */
        public Builder() {
            totalFeatures = 9;
            unknownDictionaryTotalFeatures = 9;
            readingFeature = 7;
            partOfSpeechFeature = 0;
            defaultPrefix = System.getProperty(DEFAULT_DICT_PREFIX_PROPERTY, "com/atilika/kuromoji/ipadic/");
        }

        /**
         * Sets the tokenization mode
         * <p>
         * The tokenization mode defines how Available modes are as follows:
         * <p><ul>
         * <li>{@link com.atilika.kuromoji.AbstractTokenizer.Mode#NORMAL} - The default mode
         * <li>{@link com.atilika.kuromoji.AbstractTokenizer.Mode#SEARCH} - Uses a heuristic to segment compound nouns (複合名詞) into their parts
         * <li>{@link com.atilika.kuromoji.AbstractTokenizer.Mode#EXTENDED} - Same as SEARCH, but emits unigram tokens for unknown terms
         * </ul><p>
         * See {@link #kanjiPenalty} and {@link #otherPenalty} for how to adjust costs used by SEARCH and EXTENDED mode
         *
         * @param mode  tokenization mode
         * @return this builder, not null
         */
        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Sets a custom kanji penalty
         * <p>
         * This is an expert feature used with {@link com.atilika.kuromoji.AbstractTokenizer.Mode#SEARCH} and {@link com.atilika.kuromoji.AbstractTokenizer.Mode#EXTENDED} modes that sets a length threshold and an additional costs used when running the Viterbi search.
         * The additional cost is applicable for kanji candidate tokens longer than the length threshold specified.
         * <p>
         * This is an expert feature and you usually would not need to change this.
         *
         * @param lengthThreshold
         * @param penalty  cost added to Viterbi nodes for long kanji candidate tokens
         * @return this builder, not null
         */
        public Builder kanjiPenalty(int lengthThreshold, int penalty) {
            this.kanjiPenaltyLengthTreshold = lengthThreshold;
            this.kanjiPenalty = penalty;
            return this;
        }

        /**
         * Sets a custom non-kanji penalty
         * <p>
         * This is an expert feature used with {@link com.atilika.kuromoji.AbstractTokenizer.Mode#SEARCH} and {@link com.atilika.kuromoji.AbstractTokenizer.Mode#EXTENDED} modes that sets a length threshold and an additional costs used when running the Viterbi search.
         * The additional cost is applicable for non-kanji candidate tokens longer than the length threshold specified.
         * <p>
         * This is an expert feature and you usually would not need to change this.
         *
         * @param lengthThreshold
         * @param penalty  cost added to Viterbi nodes for long non-kanji candidate tokens
         * @return this builder, not null
         */
        public Builder otherPenalty(int lengthThreshold, int penalty) {
            this.otherPenaltyLengthThreshold = lengthThreshold;
            this.otherPenalty = penalty;
            return this;
        }

        /**
         * Predictate that splits unknown words on the middle dot character (U+30FB KATAKANA MIDDLE DOT)
         * <p>
         * This feature is off by default.
         * This is an expert feature sometimes used with {@link com.atilika.kuromoji.AbstractTokenizer.Mode#SEARCH} and {@link com.atilika.kuromoji.AbstractTokenizer.Mode#EXTENDED} mode.
         *
         * @param split  predicate to indicate split on middle dot
         * @return this builder, not null
         */
        public Builder isSplitOnNakaguro(boolean split) {
            this.nakaguroSplit = split;
            return this;
        }

        /**
         * Creates the custom tokenizer instance
         *
         * @return tokenizer instance, not null
         */
        @Override
        public Tokenizer build() {
            return new Tokenizer(this);
        }

        @Override
        protected void loadDictionaries() {
            penalties = new ArrayList<>();
            penalties.add(kanjiPenaltyLengthTreshold);
            penalties.add(kanjiPenalty);
            penalties.add(otherPenaltyLengthThreshold);
            penalties.add(otherPenalty);

            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            try {
                doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
                connectionCosts = ConnectionCosts.newInstance(resolver);
                tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
                characterDefinitions = CharacterDefinitions.newInstance(resolver);

                if (nakaguroSplit) {
                    characterDefinitions.setCategories('・', new String[]{"SYMBOL"});
                }

                unknownDictionary = UnknownDictionary.newInstance(resolver, characterDefinitions, 9);
                insertedDictionary = new InsertedDictionary(9);
            } catch (Exception ouch) {
                throw new RuntimeException("Could not load dictionaries.", ouch);
            }
        }

    }
}
