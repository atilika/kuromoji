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

import com.atilika.kuromoji.dict.Dictionary;
import com.atilika.kuromoji.dict.DynamicDictionaries;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.viterbi.ViterbiBuilder;
import com.atilika.kuromoji.viterbi.ViterbiLattice;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import com.atilika.kuromoji.viterbi.ViterbiSearcher;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * AbstractTokenizer main class.
 * Thread safe.
 */
public abstract class AbstractTokenizer {

    public static final String DEFAULT_DICT_PREFIX_PROPERTY = "com.atilika.kuromoji.dict.targetdir";
    public static final String DEFAULT_DICT_PREFIX = "com/atilika/kuromoji/ipadic/";

    public enum Mode {
        NORMAL, SEARCH, EXTENDED
    }

    private final ViterbiBuilder viterbiBuilder;
    private final ViterbiSearcher viterbiSearcher;
    private final boolean split;

    private final DynamicDictionaries dictionaries;

    protected final EnumMap<ViterbiNode.Type, Dictionary> dictionaryMap = new EnumMap<>(ViterbiNode.Type.class);

    protected AbstractTokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode, boolean split, List<Integer> penalties) {
        this.dictionaries = dictionaries;

        this.viterbiBuilder = new ViterbiBuilder(dictionaries.getTrie(),
            dictionaries.getDictionary(),
            dictionaries.getUnknownDictionary(),
            userDictionary,
            mode);

        this.split = split;
        setupDictionaries(dictionaries, userDictionary);

        this.viterbiSearcher = new ViterbiSearcher(this.viterbiBuilder, mode, dictionaries.getCosts(), dictionaries.getUnknownDictionary(), penalties);
    }

    public AbstractTokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary) {
        this(dictionaries, userDictionary, Mode.NORMAL, true, new ArrayList<Integer>());
    }

    private void setupDictionaries(DynamicDictionaries dictionaries, UserDictionary userDictionary) {
        dictionaryMap.put(ViterbiNode.Type.KNOWN, dictionaries.getDictionary());
        dictionaryMap.put(ViterbiNode.Type.UNKNOWN, dictionaries.getUnknownDictionary());
        dictionaryMap.put(ViterbiNode.Type.USER, userDictionary);
        dictionaryMap.put(ViterbiNode.Type.INSERTED, new InsertedDictionary());
    }

    protected AbstractTokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode, boolean split,
                                int searchModeKanjiLength, int searchModeKanjiPenalty,
                                int searchModeOtherLength, int searchModeOtherPenalty) {
        this.dictionaries = dictionaries;

        this.viterbiBuilder = new ViterbiBuilder(dictionaries.getTrie(),
            dictionaries.getDictionary(),
            dictionaries.getUnknownDictionary(),
            userDictionary,
            mode);

        this.split = split;
        setupDictionaries(dictionaries, userDictionary);

        this.viterbiSearcher = new ViterbiSearcher(this.viterbiBuilder, mode, dictionaries.getCosts(), dictionaries.getUnknownDictionary(),
            searchModeKanjiLength, searchModeKanjiPenalty, searchModeOtherLength, searchModeOtherPenalty);
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
}
