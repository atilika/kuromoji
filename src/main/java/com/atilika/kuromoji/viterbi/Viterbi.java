/**
 * Copyright © 2010-2012 Atilika Inc.  All rights reserved.
 *
 * Atilika Inc. licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License.  A copy of the License is distributed with this work in the
 * LICENSE.txt file.  You may also obtain a copy of the License from
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.atilika.kuromoji.viterbi;

import com.atilika.kuromoji.Tokenizer.Mode;
import com.atilika.kuromoji.dict.CharacterDefinition;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;

import java.util.LinkedList;
import java.util.List;

public class Viterbi {

    private final DoubleArrayTrie trie;

    private final TokenInfoDictionary dictionary;

    private final UnknownDictionary unkDictionary;

    private final ConnectionCosts costs;

    private final UserDictionary userDictionary;

    private final CharacterDefinition characterDefinition;

    private final boolean useUserDictionary;

    private final boolean searchMode;

    private final boolean extendedMode;

    private static final int DEFAULT_COST = 10000000;

    private static final int SEARCH_MODE_KANJI_LENGTH_DEFAULT = 2;

    private static final int SEARCH_MODE_OTHER_LENGTH_DEFAULT = 7;

    private static final int SEARCH_MODE_KANJI_PENALTY_DEFAULT = 3000;

    private static final int SEARCH_MODE_OTHER_PENALTY_DEFAULT = 1700;

    private final int searchModeKanjiPenalty;

    private final int searchModeOtherPenalty;

    private final int searchModeOtherLength;

    private final int searchModeKanjiLength;

    private static final String BOS = "BOS";

    private static final String EOS = "EOS";

    /**
     * Constructor
     *
     * @param trie
     * @param dictionary
     * @param unkDictionary
     * @param costs
     * @param userDictionary
     */
    public Viterbi(DoubleArrayTrie trie,
                   TokenInfoDictionary dictionary,
                   UnknownDictionary unkDictionary,
                   ConnectionCosts costs,
                   UserDictionary userDictionary,
                   Mode mode,
                   int searchModeKanjiPenalty,
                   int searchModeOtherPenalty,
                   int searchModeKanjiLength,
                   int searchModeOtherLength) {
        this.trie = trie;
        this.dictionary = dictionary;
        this.unkDictionary = unkDictionary;
        this.costs = costs;
        this.userDictionary = userDictionary;

        this.searchModeKanjiPenalty = searchModeKanjiPenalty;
        this.searchModeOtherPenalty = searchModeOtherPenalty;
        this.searchModeKanjiLength = searchModeKanjiLength;
        this.searchModeOtherLength = searchModeOtherLength;

        if (userDictionary == null) {
            this.useUserDictionary = false;
        } else {
            this.useUserDictionary = true;
        }

        switch (mode) {
            case SEARCH:
                searchMode = true;
                extendedMode = false;
                break;
            case EXTENDED:
                searchMode = true;
                extendedMode = true;
                break;
            default:
                searchMode = false;
                extendedMode = false;
                break;
        }

        this.characterDefinition = unkDictionary.getCharacterDefinition();
    }

    /**
     * Constructor
     *
     * @param trie
     * @param dictionary
     * @param unkDictionary
     * @param costs
     * @param userDictionary
     */
    public Viterbi(DoubleArrayTrie trie,
                   TokenInfoDictionary dictionary,
                   UnknownDictionary unkDictionary,
                   ConnectionCosts costs,
                   UserDictionary userDictionary,
                   Mode mode) {
        this(trie, dictionary, unkDictionary, costs, userDictionary, mode,
                SEARCH_MODE_KANJI_PENALTY_DEFAULT, SEARCH_MODE_OTHER_PENALTY_DEFAULT,
                SEARCH_MODE_KANJI_LENGTH_DEFAULT, SEARCH_MODE_OTHER_LENGTH_DEFAULT);
    }

    /**
     * Find best path from input lattice.
     *
     * @param lattice the result of build method
     * @return List of ViterbiNode which consist best path
     */
    public List<ViterbiNode> search(ViterbiNode[][][] lattice) {

        ViterbiNode[][] endIndexArr = calculatePathCosts(lattice);
        LinkedList<ViterbiNode> result = backtrackBestPath(endIndexArr[0][0]);

        return result;
    }

    private ViterbiNode[][] calculatePathCosts(ViterbiNode[][][] lattice) {
        ViterbiNode[][] startIndexArr = lattice[0];
        ViterbiNode[][] endIndexArr = lattice[1];

        for (int i = 1; i < startIndexArr.length; i++) {

            if (startIndexArr[i] == null || endIndexArr[i] == null) {    // continue since no array which contains ViterbiNodes exists. Or no previous node exists.
                continue;
            }

            for (ViterbiNode node : startIndexArr[i]) {
                if (node == null) {    // If array doesn't contain ViterbiNode any more, continue to next index
                    break;
                }

                updateNode(endIndexArr[i], node);
            }
        }
        return endIndexArr;
    }

    private void updateNode(ViterbiNode[] viterbiNodes, ViterbiNode node) {
        int backwardConnectionId = node.getLeftId();
        int wordCost = node.getWordCost();
        int leastPathCost = DEFAULT_COST;

        for (ViterbiNode leftNode : viterbiNodes) {
            // If array doesn't contain any more ViterbiNodes, continue to next index
            if (leftNode == null) {
                return;
//                break;
            } else {
                // cost = [total cost from BOS to previous node] + [connection cost between previous node and current node] + [word cost]
                int pathCost = leftNode.getPathCost() +
                        costs.get(leftNode.getRightId(), backwardConnectionId) +
                        wordCost;

                // Add extra cost for long nodes in "Search mode".
                if (searchMode) {
                    pathCost += getLongNodeAdditionalCost(node);
                }

                // If total cost is lower than before, set current previous node as best left node (previous means left).
                if (pathCost < leastPathCost) {
                    leastPathCost = pathCost;
                    node.setPathCost(leastPathCost);
                    node.setLeftNode(leftNode);
                }
            }
        }
    }

    private int getLongNodeAdditionalCost(ViterbiNode node) {
        int pathCost = 0;
        // System.out.print(""); // If this line exists, kuromoji runs faster for some reason when searchMode == false.
        String surfaceForm = node.getSurfaceForm();
        int length = surfaceForm.length();
        if (length > searchModeKanjiLength) {

            if (isOnlyKanji(surfaceForm)) {    // Process only Kanji keywords
                pathCost += (length - searchModeKanjiLength) * searchModeKanjiPenalty;
            } else if (length > searchModeOtherLength) {
                pathCost += (length - searchModeOtherLength) * searchModeOtherPenalty;
            }
        }
        return pathCost;
    }

    private boolean isOnlyKanji(String surfaceForm) {

        for (char c : surfaceForm.toCharArray()) {
            if (!characterDefinition.isKanji(c)) {
                return false;
            }
        }
        return true;
    }

    private LinkedList<ViterbiNode> backtrackBestPath(ViterbiNode eos) {
        // track best path
        ViterbiNode node = eos;    // EOS
        LinkedList<ViterbiNode> result = new LinkedList<ViterbiNode>();

        result.add(node);

        while (true) {
            ViterbiNode leftNode = node.getLeftNode();

            if (leftNode == null) {
                break;
            } else {
                // EXTENDED mode convert unknown word into unigram node
                if (extendedMode && leftNode.getType() == ViterbiNode.Type.UNKNOWN) {
                    LinkedList<ViterbiNode> uniGramNodes = convertUnknownWordToUnigramNode(leftNode);
                    result.addAll(uniGramNodes);
                } else {
                    result.addFirst(leftNode);
                }
                node = leftNode;
            }
        }
        return result;
    }

    private LinkedList<ViterbiNode> convertUnknownWordToUnigramNode(ViterbiNode node) {
        LinkedList<ViterbiNode> uniGramNodes = new LinkedList<ViterbiNode>();
        int unigramWordId = CharacterDefinition.CharacterClass.NGRAM.getId();
        String surfaceForm = node.getSurfaceForm();

        for (int i = surfaceForm.length(); i > 0; i--) {
            String word = surfaceForm.substring(i - 1, i);
            int startIndex = node.getStartIndex() + i - 1;

            ViterbiNode uniGramNode = new ViterbiNode(unigramWordId, word, unkDictionary, startIndex, ViterbiNode.Type.UNKNOWN);
            uniGramNodes.addFirst(uniGramNode);
        }

        return uniGramNodes;
    }


    /**
     * Build lattice from input text
     *
     * @param text
     * @return
     */
    public ViterbiNode[][][] build(String text) {
        int textLength = text.length();
        ViterbiLattice viterbiLattice = new ViterbiLattice(textLength + 2);

        viterbiLattice.addBos();

        // Process user dictionary;
        if (useUserDictionary) {
            processUserDictionary(text, viterbiLattice);
        }

        int unknownWordEndIndex = -1;    // index of the last character of unknown word

        for (int startIndex = 0; startIndex < textLength; startIndex++) {
            // If no token ends where current token starts, skip this index
            if (tokenEndsWhereCurrentTokenStarts(viterbiLattice, startIndex)) {

                String suffix = text.substring(startIndex);
                boolean found = processIndex(viterbiLattice, startIndex, suffix);

                // In the case of normal mode, it doesn't process unknown word greedily.
                if (searchMode || unknownWordEndIndex <= startIndex) {
                    unknownWordEndIndex = processUnknownWord(viterbiLattice, unknownWordEndIndex, startIndex, suffix, found);
                }
            }
        }

        viterbiLattice.addEos();

        ViterbiNode[][][] result = new ViterbiNode[][][]{viterbiLattice.getStartIndexArr(), viterbiLattice.getEndIndexArr()};

        return result;
    }

    private boolean tokenEndsWhereCurrentTokenStarts(ViterbiLattice viterbiArrays, int startIndex) {
        return viterbiArrays.getEndSizeArr()[startIndex + 1] != 0;
    }

    private boolean processIndex(ViterbiLattice viterbiArrays, int startIndex, String suffix) {
        boolean found = false;
        for (int endIndex = 1; endIndex < suffix.length() + 1; endIndex++) {
            String prefix = suffix.substring(0, endIndex);

            int result = trie.lookup(prefix);

            if (result > 0) {    // Found match in double array trie
                found = true;    // Don't produce unknown word starting from this index
                for (int wordId : dictionary.lookupWordIds(result)) {
                    ViterbiNode node = new ViterbiNode(wordId, prefix, dictionary, startIndex, ViterbiNode.Type.KNOWN);
                    addToArrays(node, startIndex + 1, startIndex + 1 + endIndex, viterbiArrays);
                }
            } else if (result < 0) {    // If result is less than zero, continue to next position
                break;
            }
        }
        return found;
    }

    private int processUnknownWord(ViterbiLattice viterbi, int unknownWordEndIndex, int startIndex, String suffix, boolean found) {
        int unknownWordLength = 0;
        char firstCharacter = suffix.charAt(0);
        boolean isInvoke = characterDefinition.isInvoke(firstCharacter);

        if (isInvoke || found == false) {    // Process "invoke"
            unknownWordLength = unkDictionary.lookup(suffix);
        }

        if (unknownWordLength > 0) {      // found unknown word
            String unkWord = suffix.substring(0, unknownWordLength);
            int characterId = characterDefinition.lookup(firstCharacter);
            int[] wordIds = unkDictionary.lookupWordIds(characterId); // characters in input text are supposed to be the same

            for (int wordId : wordIds) {
                ViterbiNode node = new ViterbiNode(wordId, unkWord, unkDictionary, startIndex, ViterbiNode.Type.UNKNOWN);
                addToArrays(node, startIndex + 1, startIndex + 1 + unknownWordLength, viterbi);
            }
            unknownWordEndIndex = startIndex + unknownWordLength;
        }
        return unknownWordEndIndex;
    }

    /**
     * Find token(s) in input text and set found token(s) in arrays as normal tokens
     *
     * @param text
     * @param lattice
     */
    private void processUserDictionary(String text, ViterbiLattice lattice) {
        int[][] result = userDictionary.lookup(text);
        for (int[] segmentation : result) {
            int wordId = segmentation[0];
            int index = segmentation[1];
            int length = segmentation[2];
            String word = text.substring(index, index + length);

            ViterbiNode node = new ViterbiNode(wordId, word, userDictionary, index, ViterbiNode.Type.USER);
            addToArrays(node, index + 1, index + 1 + length, lattice);
        }
    }

    private void addToArrays(ViterbiNode bosNode, int i, int i1, ViterbiLattice viterbi) {
        addToArrays(bosNode, i, i1, viterbi.getStartIndexArr(), viterbi.getEndIndexArr(), viterbi.getStartSizeArr(), viterbi.getEndSizeArr());
    }

    /**
     * Add node to arrays and increment count in size array
     *
     * @param node
     * @param startIndex
     * @param endIndex
     * @param startIndexArr
     * @param endIndexArr
     * @param startSizeArr
     * @param endSizeArr
     */
    private void addToArrays(ViterbiNode node, int startIndex, int endIndex, ViterbiNode[][] startIndexArr, ViterbiNode[][] endIndexArr, int[] startSizeArr, int[] endSizeArr) {
        int startNodesCount = startSizeArr[startIndex];
        int endNodesCount = endSizeArr[endIndex];

        if (startNodesCount == 0) {
            startIndexArr[startIndex] = new ViterbiNode[10];
        }

        if (endNodesCount == 0) {
            endIndexArr[endIndex] = new ViterbiNode[10];
        }

        if (startIndexArr[startIndex].length <= startNodesCount) {
            startIndexArr[startIndex] = extendArray(startIndexArr[startIndex]);
        }

        if (endIndexArr[endIndex].length <= endNodesCount) {
            endIndexArr[endIndex] = extendArray(endIndexArr[endIndex]);
        }

        startIndexArr[startIndex][startNodesCount] = node;
        endIndexArr[endIndex][endNodesCount] = node;

        startSizeArr[startIndex] = startNodesCount + 1;
        endSizeArr[endIndex] = endNodesCount + 1;
    }

    /**
     * Return twice as big array which contains value of input array
     *
     * @param array
     * @return
     */
    private ViterbiNode[] extendArray(ViterbiNode[] array) {
        //extend array
        ViterbiNode[] newArray = new ViterbiNode[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    private class ViterbiLattice {
        private final int dimension;
        private final ViterbiNode[][] startIndexArr;
        private final ViterbiNode[][] endIndexArr;
        private final int[] startSizeArr;
        private final int[] endSizeArr;

        public ViterbiLattice(int dimension) {
            this.dimension = dimension;
            startIndexArr = new ViterbiNode[dimension][];
            endIndexArr = new ViterbiNode[dimension][];
            startSizeArr = new int[dimension];
            endSizeArr = new int[dimension];
        }

        public ViterbiNode[][] getStartIndexArr() {
            return startIndexArr;
        }

        public ViterbiNode[][] getEndIndexArr() {
            return endIndexArr;
        }

        public int[] getStartSizeArr() {
            return startSizeArr;
        }

        public int[] getEndSizeArr() {
            return endSizeArr;
        }

        public void addBos() {
            ViterbiNode bosNode = new ViterbiNode(-1, BOS, 0, 0, 0, -1, ViterbiNode.Type.KNOWN);
            addToArrays(bosNode, 0, 1, this);
        }

        public void addEos() {
            ViterbiNode eosNode = new ViterbiNode(-1, EOS, 0, 0, 0, dimension - 1, ViterbiNode.Type.KNOWN);
            addToArrays(eosNode, dimension - 1, 0, this);
        }
    }
}
