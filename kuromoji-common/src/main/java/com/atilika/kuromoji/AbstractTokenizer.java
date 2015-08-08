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
package com.atilika.kuromoji;

import com.atilika.kuromoji.dict.CharacterDefinitions;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;
import com.atilika.kuromoji.util.ResourceResolver;
import com.atilika.kuromoji.viterbi.TokenFactory;
import com.atilika.kuromoji.viterbi.ViterbiBuilder;
import com.atilika.kuromoji.viterbi.ViterbiFormatter;
import com.atilika.kuromoji.viterbi.ViterbiLattice;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import com.atilika.kuromoji.viterbi.ViterbiSearcher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * AbstractTokenizer main class.
 * Thread safe.
 */
public abstract class AbstractTokenizer {

    public enum Mode {
        NORMAL, SEARCH, EXTENDED
    }

    private ViterbiBuilder viterbiBuilder;

    private ViterbiSearcher viterbiSearcher;

    private ViterbiFormatter viterbiFormatter;

    private boolean split;

    private TokenInfoDictionary tokenInfoDictionary;

    private UnknownDictionary unknownDictionary;

    private UserDictionary userDictionary;

    private InsertedDictionary insertedDictionary;

    protected TokenFactory tokenFactory;

    protected EnumMap<ViterbiNode.Type, Dictionary> dictionaryMap = new EnumMap<>(ViterbiNode.Type.class);

    protected void configure(Builder builder) {

        builder.loadDictionaries();

        this.tokenFactory = builder.tokenFactory;

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

        this.viterbiSearcher = new ViterbiSearcher(
            builder.mode,
            builder.connectionCosts,
            unknownDictionary,
            builder.penalties
        );

        this.viterbiFormatter = new ViterbiFormatter(builder.connectionCosts);
        this.split = builder.split;

        initDictionaryMap();
    }

    private void initDictionaryMap() {
        dictionaryMap.put(ViterbiNode.Type.KNOWN, tokenInfoDictionary);
        dictionaryMap.put(ViterbiNode.Type.UNKNOWN, unknownDictionary);
        dictionaryMap.put(ViterbiNode.Type.USER, userDictionary);
        dictionaryMap.put(ViterbiNode.Type.INSERTED, insertedDictionary);
    }


    /**
     * Tokenizes the provided text and returns a list of tokens with various feature information
     * <p>
     * This method is thread safe
     *
     * @param text  text to tokenize
     * @param <T>  token type
     * @return list of Token, not null
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
     * Tokenizes the provided text and outputs the corresponding Viterbi lattice and the Viterbi path to the provided output stream
     * <p>
     * The output is written in <a href="https://en.wikipedia.org/wiki/DOT_(graph_description_language)">DOT</a> format.
     * <p>
     * This method is not thread safe
     *
     * @param outputStream  output stream to write to
     * @param text  text to tokenize
     * @throws java.io.IOException if an error occurs when writing the lattice and path
     */
    public void debugTokenize(OutputStream outputStream, String text) throws IOException {
        ViterbiLattice lattice = viterbiBuilder.build(text);
        List<ViterbiNode> bestPath = viterbiSearcher.search(lattice);

        outputStream.write(
            viterbiFormatter.format(lattice, bestPath).getBytes(StandardCharsets.UTF_8)
        );
        outputStream.flush();
    }

    /**
     * Writes the Viterbi lattice for the provided text to an output stream
     * <p>
     * The output is written in <a href="https://en.wikipedia.org/wiki/DOT_(graph_description_language)">DOT</a> format.
     * <p>
     * This method is not thread safe
     *
     * @param outputStream  output stream to write to
     * @param text  text to create lattice for
     * @throws java.io.IOException if an error occurs when writing the lattice
     */
    public void debugLattice(OutputStream outputStream, String text) throws IOException {
        ViterbiLattice lattice = viterbiBuilder.build(text);

        outputStream.write(
            viterbiFormatter.format(lattice).getBytes(StandardCharsets.UTF_8)
        );
        outputStream.flush();
    }

    public static void main(String[] args) throws IOException {
        AbstractTokenizer tokenizer;
        if (args.length == 1) {
            tokenizer = new Builder()
                .userDictionary(args[0])
                .build();
        } else {
            tokenizer = new Builder().build();
        }
        new TokenizerRunner().run(tokenizer);
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
     * @param text sentence to tokenize
     * @return list of Token
     */
    private <T extends AbstractToken> List<T> doTokenize(int offset, String text) {
        ArrayList<T> result = new ArrayList<>();

        ViterbiLattice lattice = viterbiBuilder.build(text);
        List<ViterbiNode> bestPath = viterbiSearcher.search(lattice);

        for (ViterbiNode node : bestPath) {
            int wordId = node.getWordId();
            if (node.getType() == ViterbiNode.Type.KNOWN && wordId == -1) { // Do not include BOS/EOS
                continue;
            }
            T token = (T) tokenFactory.createToken(
                wordId,
                node.getSurfaceForm(),
                node.getType(),
                offset + node.getStartIndex(),
                dictionaryMap.get(node.getType())
            );
            result.add(token);
        }

        return result;
    }

    public static class Builder {

        protected DoubleArrayTrie doubleArrayTrie;
        protected ConnectionCosts connectionCosts;
        protected TokenInfoDictionary tokenInfoDictionary;
        protected UnknownDictionary unknownDictionary;
        protected CharacterDefinitions characterDefinitions;
        protected UserDictionary userDictionary = null;
        protected InsertedDictionary insertedDictionary;

        protected Mode mode = Mode.NORMAL;
        protected boolean split = true;
        protected List<Integer> penalties = Collections.EMPTY_LIST;

        protected int totalFeatures = 1;
        protected int unknownDictionaryTotalFeatures = 1;
        protected int readingFeature = 0;
        protected int partOfSpeechFeature = 0;

        protected ResourceResolver resolver;

        protected TokenFactory tokenFactory;

        /**
         * Create Tokenizer instance
         *
         * @param <T> token type
         * @return Tokenizer
         */
        public <T extends AbstractTokenizer> T build() {
            return null;
        }

        protected void loadDictionaries() {
            try {
                doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
                connectionCosts = ConnectionCosts.newInstance(resolver);
                tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
                characterDefinitions = CharacterDefinitions.newInstance(resolver);
                unknownDictionary = UnknownDictionary.newInstance(resolver, characterDefinitions, unknownDictionaryTotalFeatures);
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
         * @throws java.io.IOException if an error occurs when reading the user dictionary
         */
        public Builder userDictionary(InputStream userDictionaryInputStream) throws IOException {
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
         * @throws java.io.IOException if an error occurs when reading the user dictionary
         */
        public Builder userDictionary(String userDictionaryPath) throws IOException {
            InputStream input = new BufferedInputStream(
                new FileInputStream(userDictionaryPath)
            );

            this.userDictionary(input);
            input.close();
            return this;
        }

        public void setSplit(boolean split) {
            this.split = split;
        }
    }
}
