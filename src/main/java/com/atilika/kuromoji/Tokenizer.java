/**
 * Copyright 2010-2013 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.dict.DynamicDictionaries;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.viterbi.ViterbiBuilder;
import com.atilika.kuromoji.viterbi.ViterbiLattice;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import com.atilika.kuromoji.viterbi.ViterbiSearcher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Tokenizer main class.
 * Thread safe.
 */
public class Tokenizer {
	/** @see Builder#defaultPrefix */
    public static final String DEFAULT_DICT_PREFIX_PROPERTY = "kuromoji.dict.targetdir";

    /** @see Builder#defaultPrefix */
    public static final String DEFAULT_DICT_PREFIX = "ipadic/";

    public enum Mode {
        NORMAL, SEARCH, EXTENDED
    }

    private final ViterbiBuilder viterbiBuilder;

    private final ViterbiSearcher viterbiSearcher;

    private final EnumMap<ViterbiNode.Type, Dictionary> dictionaryMap = new EnumMap<ViterbiNode.Type, Dictionary>(ViterbiNode.Type.class);

    private final boolean split;

    protected Tokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode, boolean split) {

        this.viterbiBuilder = new ViterbiBuilder(dictionaries.getTrie(),
                dictionaries.getDictionary(),
                dictionaries.getUnknownDictionary(),
                userDictionary,
                mode);

        this.split = split;

        this.viterbiSearcher = new ViterbiSearcher(this.viterbiBuilder, mode, dictionaries.getCosts(), dictionaries.getUnknownDictionary());
        setupDictionaries(dictionaries, userDictionary);
    }

    private void setupDictionaries(DynamicDictionaries dictionaries, UserDictionary userDictionary) {
        dictionaryMap.put(ViterbiNode.Type.KNOWN, dictionaries.getDictionary());
        dictionaryMap.put(ViterbiNode.Type.UNKNOWN, dictionaries.getUnknownDictionary());
        dictionaryMap.put(ViterbiNode.Type.USER, userDictionary);
        dictionaryMap.put(ViterbiNode.Type.INSERTED, new InsertedDictionary());
    }

    protected Tokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode, boolean split,
                        int searchModeKanjiLength, int searchModeKanjiPenalty,
                        int searchModeOtherLength, int searchModeOtherPenalty) {

        this.viterbiBuilder = new ViterbiBuilder(dictionaries.getTrie(),
                dictionaries.getDictionary(),
                dictionaries.getUnknownDictionary(),
                userDictionary,
                mode);

        this.split = split;

        this.viterbiSearcher = new ViterbiSearcher(this.viterbiBuilder, mode, dictionaries.getCosts(), dictionaries.getUnknownDictionary(),
                searchModeKanjiLength, searchModeKanjiPenalty, searchModeOtherLength, searchModeOtherPenalty);
        setupDictionaries(dictionaries, userDictionary);
    }

    /**
     * Tokenize input text
     *
     * @param text
     * @return list of Token
     */
    public List<Token> tokenize(String text) {

        if (!split) {
            return doTokenize(0, text);
        }

        List<Integer> splitPositions = getSplitPositions(text);

        if (splitPositions.size() == 0) {
            return doTokenize(0, text);
        }

        ArrayList<Token> result = new ArrayList<Token>();
        int offset = 0;
        for (int position : splitPositions) {
            result.addAll(doTokenize(offset, text.substring(offset, position + 1)));
            offset = position + 1;
        }

        if (offset < text.length()) {
            result.addAll(doTokenize(offset, text.substring(offset)));
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
        ArrayList<Integer> splitPositions = new ArrayList<Integer>();

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
    private List<Token> doTokenize(int offset, String sentence) {
        ArrayList<Token> result = new ArrayList<Token>();

        ViterbiLattice lattice = viterbiBuilder.build(sentence);
        List<ViterbiNode> bestPath = viterbiSearcher.search(lattice);
        for (ViterbiNode node : bestPath) {
            int wordId = node.getWordId();
            if (node.getType() == ViterbiNode.Type.KNOWN && wordId == -1) { // Do not include BOS/EOS
                continue;
            }
            Token token = new Token(wordId, node.getSurfaceForm(), node.getType(), offset + node.getStartIndex(), dictionaryMap.get(node.getType()));    // Pass different dictionary based on the type of node
            result.add(token);
        }

        return result;
    }

    /**
     * Get Builder to create Tokenizer instance.
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class used to create Tokenizer instance.
     */
    public static class Builder {
        private Mode mode = Mode.NORMAL;
        private boolean split = true;
        private UserDictionary userDictionary = null;
        private Integer searchModeKanjiLength;
        private Integer searchModeKanjiPenalty;
        private Integer searchModeOtherLength;
        private Integer searchModeOtherPenalty;

        /**
         * The default resource prefix, also configurable via
         * system property <code>kuromoji.dict.targetdir</code>.
         */
        private String defaultPrefix = System.getProperty(
                DEFAULT_DICT_PREFIX_PROPERTY,
                DEFAULT_DICT_PREFIX);

        /**
         * The default resource resolver (relative to this class).
         */
        private ResourceResolver resolver = new ClassLoaderResolver(this.getClass());

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

        /**
         * Set if tokenizer should split input string at "。" and "、" before tokenize to increase performance.
         * Splitting shouldn't change the result of tokenization most of the cases.
         * Default: true
         *
         * @param split whether tokenizer should split input string
         * @return Builder
         */
        public synchronized Builder split(boolean split) {
            this.split = split;
            return this;
        }

        /**
         * Set user dictionary input stream
         *
         * @param userDictionaryInputStream dictionary file as input stream
         * @return Builder
         * @throws IOException
         */
        public synchronized Builder userDictionary(InputStream userDictionaryInputStream) throws IOException {
            this.userDictionary = UserDictionary.read(userDictionaryInputStream);
            return this;
        }

        /**
         * Set user dictionary path
         *
         * @param userDictionaryPath path to dictionary file
         * @return Builder
         * @throws IOException
         * @throws FileNotFoundException
         */
        public synchronized Builder userDictionary(String userDictionaryPath) throws IOException {
            if (userDictionaryPath != null && !userDictionaryPath.isEmpty()) {
                this.userDictionary(new BufferedInputStream(new FileInputStream(userDictionaryPath)));
            }
            return this;
        }

        /**
         * Sets the default prefix applied to resources at lookup time if classloader-relative
         * {@link ResourceResolver} is used.
         */
        public synchronized Builder prefix(String resourcePrefix) {
            this.defaultPrefix = resourcePrefix;
            return this;
        }

        /**
         * Sets the default {@link ResourceResolver} used to locate dictionaries.
         * @see #prefix(String)
         */
        public void resolver(ResourceResolver resolver) {
            if (resolver == null) throw new IllegalArgumentException();
            this.resolver = resolver;
        }

        public synchronized Builder penalties(int kanjiLength, int kanjiPenalty, int otherLength, int otherPenalty) {
            this.searchModeKanjiLength = kanjiLength;
            this.searchModeKanjiPenalty = kanjiPenalty;
            this.searchModeOtherLength = otherLength;
            this.searchModeOtherPenalty = otherPenalty;
            return this;
        }

        /**
         * Create Tokenizer instance
         *
         * @return Tokenizer
         */
        public synchronized Tokenizer build() {
            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            DynamicDictionaries dictionaries = new DynamicDictionaries(resolver);

            if (this.mode != Mode.NORMAL
                    && searchModeKanjiLength != null && searchModeKanjiPenalty != null
                    && searchModeOtherLength != null && searchModeOtherPenalty != null) {

                return new Tokenizer(dictionaries, userDictionary, mode, split,
                        searchModeKanjiLength, searchModeKanjiPenalty,
                        searchModeOtherLength, searchModeOtherPenalty);
            } else {
                return new Tokenizer(dictionaries, userDictionary, mode, split);
            }
        }
    }
}
