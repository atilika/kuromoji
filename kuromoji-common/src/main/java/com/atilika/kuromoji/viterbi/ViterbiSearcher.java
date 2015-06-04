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
package com.atilika.kuromoji.viterbi;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.dict.CharacterDefinition;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.UnknownDictionary;

import java.util.LinkedList;
import java.util.List;

public class ViterbiSearcher {

    private static final int SEARCH_MODE_KANJI_LENGTH_DEFAULT = 2;
    private static final int SEARCH_MODE_OTHER_LENGTH_DEFAULT = 7;
    private static final int SEARCH_MODE_KANJI_PENALTY_DEFAULT = 3000;
    private static final int SEARCH_MODE_OTHER_PENALTY_DEFAULT = 1700;
    private static final int DEFAULT_COST = 10000000;

    private final ViterbiBuilder viterbi;
    private final ConnectionCosts costs;
    private final UnknownDictionary unknownDictionary;

    private final boolean extendedMode;
    private final boolean searchMode;

    private final int searchModeKanjiPenalty;
    private final int searchModeOtherPenalty;
    private final int searchModeOtherLength;
    private final int searchModeKanjiLength;

    public ViterbiSearcher(ViterbiBuilder viterbi,
                           AbstractTokenizer.Mode mode,
                           ConnectionCosts costs,
                           UnknownDictionary unknownDictionary,
                           List<Integer> penalties) {
        this.viterbi = viterbi;
        if (penalties.isEmpty()) {
            this.searchModeKanjiLength = SEARCH_MODE_KANJI_LENGTH_DEFAULT;
            this.searchModeKanjiPenalty = SEARCH_MODE_KANJI_PENALTY_DEFAULT;
            this.searchModeOtherLength = SEARCH_MODE_OTHER_LENGTH_DEFAULT;
            this.searchModeOtherPenalty = SEARCH_MODE_OTHER_PENALTY_DEFAULT;

        } else {
            this.searchModeKanjiLength = penalties.get(0);
            this.searchModeKanjiPenalty = penalties.get(1);
            this.searchModeOtherLength = penalties.get(2);
            this.searchModeOtherPenalty = penalties.get(3);
        }
        this.costs = costs;
        this.unknownDictionary = unknownDictionary;

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
    }

    public ViterbiSearcher(ViterbiBuilder viterbi,
                           AbstractTokenizer.Mode mode,
                           ConnectionCosts costs,
                           UnknownDictionary unknownDictionary,
                           int searchModeKanjiLength,
                           int searchModeKanjiPenalty,
                           int searchModeOtherLength,
                           int searchModeOtherPenalty) {
        this.viterbi = viterbi;
        this.searchModeKanjiLength = searchModeKanjiLength;
        this.searchModeKanjiPenalty = searchModeKanjiPenalty;
        this.searchModeOtherLength = searchModeOtherLength;
        this.searchModeOtherPenalty = searchModeOtherPenalty;
        this.costs = costs;
        this.unknownDictionary = unknownDictionary;

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
    }

    public ViterbiSearcher(ViterbiBuilder viterbi, AbstractTokenizer.Mode mode, ConnectionCosts costs, UnknownDictionary unknownDictionary) {
        this(viterbi, mode, costs, unknownDictionary,
            SEARCH_MODE_KANJI_LENGTH_DEFAULT, SEARCH_MODE_KANJI_PENALTY_DEFAULT,
            SEARCH_MODE_OTHER_LENGTH_DEFAULT, SEARCH_MODE_OTHER_PENALTY_DEFAULT);
    }

    /**
     * Find best path from input lattice.
     *
     * @param lattice the result of build method
     * @return List of ViterbiNode which consist best path
     */
    public List<ViterbiNode> search(ViterbiLattice lattice) {

        ViterbiNode[][] endIndexArr = calculatePathCosts(lattice);
        LinkedList<ViterbiNode> result = backtrackBestPath(endIndexArr[0][0]);

        return result;
    }

    ViterbiNode[][] calculatePathCosts(ViterbiLattice lattice) {
        ViterbiNode[][] startIndexArr = lattice.getStartIndexArr();
        ViterbiNode[][] endIndexArr = lattice.getEndIndexArr();

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

    void updateNode(ViterbiNode[] viterbiNodes, ViterbiNode node) {
        int backwardConnectionId = node.getLeftId();
        int wordCost = node.getWordCost();
        int leastPathCost = DEFAULT_COST;

        for (ViterbiNode leftNode : viterbiNodes) {
            // If array doesn't contain any more ViterbiNodes, continue to next index
            if (leftNode == null) {
                return;
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

    int getLongNodeAdditionalCost(ViterbiNode node) {
        int pathCost = 0;
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

    boolean isOnlyKanji(String surfaceForm) {

        for (char c : surfaceForm.toCharArray()) {
            if (!viterbi.getCharacterDefinition().isKanji(c)) {
                return false;
            }
        }
        return true;
    }

    LinkedList<ViterbiNode> backtrackBestPath(ViterbiNode eos) {
        // track best path
        ViterbiNode node = eos; // EOS
        LinkedList<ViterbiNode> result = new LinkedList<>();

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

    LinkedList<ViterbiNode> convertUnknownWordToUnigramNode(ViterbiNode node) {
        LinkedList<ViterbiNode> uniGramNodes = new LinkedList<>();
        int unigramWordId = CharacterDefinition.CharacterClass.NGRAM.getId();
        String surfaceForm = node.getSurfaceForm();

        for (int i = surfaceForm.length(); i > 0; i--) {
            String word = surfaceForm.substring(i - 1, i);
            int startIndex = node.getStartIndex() + i - 1;

            ViterbiNode uniGramNode = new ViterbiNode(unigramWordId, word, unknownDictionary, startIndex, ViterbiNode.Type.UNKNOWN);
            uniGramNodes.addFirst(uniGramNode);
        }

        return uniGramNodes;
    }

}