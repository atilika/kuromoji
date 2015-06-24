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
package com.atilika.kuromoji;

import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;
import com.atilika.kuromoji.viterbi.ViterbiBuilder;
import com.atilika.kuromoji.viterbi.ViterbiLattice;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import com.atilika.kuromoji.viterbi.ViterbiSearcher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * AbstractTokenizer main class.
 * Thread safe.
 */
public abstract class AbstractTokenizer {

    public static final String DEFAULT_DICT_PREFIX_PROPERTY = "com.atilika.kuromoji.dict.targetdir";

    public enum Mode {
        NORMAL, SEARCH, EXTENDED
    }

    private ViterbiBuilder viterbiBuilder;
    private ViterbiSearcher viterbiSearcher;

    private boolean split;

    private TokenInfoDictionary tokenInfoDictionary;

    private UnknownDictionary unknownDictionary;

    private UserDictionary userDictionary;

    private InsertedDictionary insertedDictionary;

    protected EnumMap<ViterbiNode.Type, Dictionary> dictionaryMap = new EnumMap<>(ViterbiNode.Type.class);

    protected AbstractTokenizer() {
    }

    public void configure(Builder builder) {

        builder.loadDictionaries();

        this.tokenInfoDictionary = builder.tokenInfoDictionary;
        this.unknownDictionary = builder.unknownDictionary;
        this.userDictionary = builder.userDictionary;
        this.insertedDictionary = builder.insertedDictionary;

        this.viterbiBuilder = new ViterbiBuilder(
            builder.doubleArrayTrie,
            tokenInfoDictionary,
            unknownDictionary,
            userDictionary,
            builder.mode
        );

        this.split = builder.split;
        this.viterbiSearcher = new ViterbiSearcher(
            builder.mode,
            builder.connectionCosts,
            unknownDictionary,
            builder.penalties
        );

        initDictionaryMap();
    }


    private void initDictionaryMap() {
        dictionaryMap.put(ViterbiNode.Type.KNOWN, tokenInfoDictionary);
        dictionaryMap.put(ViterbiNode.Type.UNKNOWN, unknownDictionary);
        dictionaryMap.put(ViterbiNode.Type.USER, userDictionary);
        dictionaryMap.put(ViterbiNode.Type.INSERTED, insertedDictionary);
    }


    /**
     * Tokenize input text
     *
     * @param text
     * @return list of Token
     */
    public <T extends AbstractToken> List<T> tokenize(String text) {

        if (!split) {
            return doTokenize(0, text);
        }

        List<Integer> splitPositions = getSplitPositions(text);

        if (splitPositions.size() == 0) {
            return doTokenize(0, text);
        }

        ArrayList<T> result = new ArrayList<>();

        int offset = 0;

        for (int position : splitPositions) {
            result.addAll(this.<T>doTokenize(offset, text.substring(offset, position + 1)));
            offset = position + 1;
        }

        if (offset < text.length()) {
            result.addAll(this.<T>doTokenize(offset, text.substring(offset)));
        }

        return result;
    }

    /**
     * Split input text at 句読点, which is 。 and 、
     *
     * @param text
     * @return list of split position
     */
    private List<Integer> getSplitPositions(String text) {
        ArrayList<Integer> splitPositions = new ArrayList<>();
        int position = 0;
        int currentPosition = 0;

        while (true) {
            int indexOfMaru = text.indexOf("。", currentPosition);
            int indexOfTen = text.indexOf("、", currentPosition);

            if (indexOfMaru < 0 || indexOfTen < 0) {
                position = Math.max(indexOfMaru, indexOfTen);
            } else {
                position = Math.min(indexOfMaru, indexOfTen);
            }

            if (position >= 0) {
                splitPositions.add(position);
                currentPosition = position + 1;
            } else {
                break;
            }
        }

        return splitPositions;
    }

    /**
     * Tokenize input sentence.
     *
     * @param offset   offset of sentence in original input text
     * @param sentence sentence to tokenize
     * @return list of Token
     */
    private <T extends AbstractToken> List<T> doTokenize(int offset, String sentence) {
        ArrayList<T> result = new ArrayList<>();

        ViterbiLattice lattice = viterbiBuilder.build(sentence);
        List<ViterbiNode> bestPath = viterbiSearcher.search(lattice);

        for (ViterbiNode node : bestPath) {
            int wordId = node.getWordId();
            if (node.getType() == ViterbiNode.Type.KNOWN && wordId == -1) { // Do not include BOS/EOS
                continue;
            }
            T token = createToken(offset, node, wordId);    // Pass different dictionary based on the type of node
            result.add(token);
        }

        return result;
    }

    protected abstract <T extends AbstractToken> T createToken(int offset, ViterbiNode node, int wordId);

    public static void main(String[] args) throws IOException {
        AbstractTokenizer tokenizer;
        if (args.length == 1) {
            tokenizer = new Builder().userDictionary(args[0]).build();
        } else {
            tokenizer = new Builder().build();
        }
        new TokenizerRunner().run(tokenizer);
    }

    public static class Builder {

        protected DoubleArrayTrie doubleArrayTrie;
        protected ConnectionCosts connectionCosts;
        protected TokenInfoDictionary tokenInfoDictionary;
        protected UnknownDictionary unknownDictionary;
        protected UserDictionary userDictionary = null;
        protected InsertedDictionary insertedDictionary;

        protected Mode mode = Mode.NORMAL;
        protected boolean split = true;
        protected List<Integer> penalties = Collections.EMPTY_LIST;

        protected String defaultPrefix;

        protected int totalFeatures = 1;
        protected int unknownDictionaryTotalFeatures = 1;
        protected int readingFeature = 0;
        protected int partOfSpeechFeature = 0;

        protected ResourceResolver resolver = new ClassLoaderResolver(this.getClass());

        public <T extends AbstractTokenizer> T build() {
            return null;
        }

        /**
         * Default Tokenizer builder, returning null
         */
        protected void loadDictionaries() {
            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            try {
                doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
                connectionCosts = ConnectionCosts.newInstance(resolver);
                tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
                unknownDictionary = UnknownDictionary.newInstance(resolver, unknownDictionaryTotalFeatures);
                insertedDictionary = new InsertedDictionary(totalFeatures);
            } catch (Exception ouch) {
                throw new RuntimeException("Could not load dictionaries.", ouch);
            }
        }

        /**
         * Set user dictionary input stream
         *
         * @param userDictionaryInputStream dictionary file as input stream
         * @return Builder
         * @throws java.io.IOException
         */
        public synchronized Builder userDictionary(InputStream userDictionaryInputStream) throws IOException {
            this.userDictionary = new UserDictionary(
                userDictionaryInputStream, totalFeatures, readingFeature, partOfSpeechFeature
            );
            return this;
        }

        /**
         * Set user dictionary path
         *
         * @param userDictionaryPath path to dictionary file
         * @return Builder
         * @throws IOException
         * @throws java.io.FileNotFoundException
         */
        public synchronized Builder userDictionary(String userDictionaryPath) throws IOException {
            if (userDictionaryPath != null && !userDictionaryPath.isEmpty()) {
                this.userDictionary(new BufferedInputStream(new FileInputStream(userDictionaryPath)));
            }
            return this;
        }

        public synchronized Builder prefix(String resourcePrefix) {
            this.defaultPrefix = resourcePrefix;
            return this;
        }

        public void setSplit(boolean split) {
            this.split = split;
        }

    }
}
