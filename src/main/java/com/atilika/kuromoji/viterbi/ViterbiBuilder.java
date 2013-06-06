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
package com.atilika.kuromoji.viterbi;

import com.atilika.kuromoji.Tokenizer.Mode;
import com.atilika.kuromoji.dict.CharacterDefinition;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;

public class ViterbiBuilder {

    private final DoubleArrayTrie trie;

    private final TokenInfoDictionary dictionary;

    private final UnknownDictionary unkDictionary;

    private final UserDictionary userDictionary;

    private final CharacterDefinition characterDefinition;

    private final boolean useUserDictionary;

    private boolean searchMode;

    public CharacterDefinition getCharacterDefinition() {
        return characterDefinition;
    }


    /**
     * Constructor
     *
     * @param trie
     * @param dictionary
     * @param unkDictionary
     * @param userDictionary
     */
    public ViterbiBuilder(DoubleArrayTrie trie,
                          TokenInfoDictionary dictionary,
                          UnknownDictionary unkDictionary,
                          UserDictionary userDictionary,
                          Mode mode) {
        this.trie = trie;
        this.dictionary = dictionary;
        this.unkDictionary = unkDictionary;
        this.userDictionary = userDictionary;


        if (userDictionary == null) {
            this.useUserDictionary = false;
        } else {
            this.useUserDictionary = true;
        }

        if (mode == Mode.SEARCH || mode == Mode.EXTENDED) {
            searchMode = true;
        }
        this.characterDefinition = unkDictionary.getCharacterDefinition();
    }


    /**
     * Build lattice from input text
     *
     *
     * @param text
     * @return
     */
    public ViterbiLattice build(String text) {
        int textLength = text.length();
        ViterbiLattice lattice = new ViterbiLattice(textLength + 2);

        lattice.addBos();

        int unknownWordEndIndex = -1;    // index of the last character of unknown word

        for (int startIndex = 0; startIndex < textLength; startIndex++) {
            // If no token ends where current token starts, skip this index
            if (lattice.tokenEndsWhereCurrentTokenStarts(startIndex)) {

                String suffix = text.substring(startIndex);
                boolean found = processIndex(lattice, startIndex, suffix);

                // In the case of normal mode, it doesn't process unknown word greedily.
                if (searchMode || unknownWordEndIndex <= startIndex) {
                    unknownWordEndIndex = processUnknownWord(lattice, unknownWordEndIndex, startIndex, suffix, found);
                }
            }
        }

        if (useUserDictionary) {
            processUserDictionary(text, lattice);
        }

        lattice.addEos();

        return lattice;
    }

    private boolean processIndex(ViterbiLattice lattice, int startIndex, String suffix) {
        boolean found = false;
        for (int endIndex = 1; endIndex < suffix.length() + 1; endIndex++) {
            String prefix = suffix.substring(0, endIndex);

            int result = trie.lookup(prefix);

            if (result > 0) {    // Found match in double array trie
                found = true;    // Don't produce unknown word starting from this index
                for (int wordId : dictionary.lookupWordIds(result)) {
                    ViterbiNode node = new ViterbiNode(wordId, prefix, dictionary, startIndex, ViterbiNode.Type.KNOWN);
                    lattice.addNode(node, startIndex + 1, startIndex + 1 + endIndex);
                }
            } else if (result < 0) {    // If result is less than zero, continue to next position
                break;
            }
        }
        return found;
    }

    private int processUnknownWord(ViterbiLattice lattice, int unknownWordEndIndex, int startIndex, String suffix, boolean found) {
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
                lattice.addNode(node, startIndex + 1, startIndex + 1 + unknownWordLength);
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
    private void processUserDictionary(final String text, ViterbiLattice lattice) {
        int[][] result = userDictionary.locateUserDefinedWordsInText(text);
        for (int[] segmentation : result) {
            int wordId = segmentation[0];
            int index = segmentation[1];
            int length = segmentation[2];
            String word = text.substring(index, index + length);

            ViterbiNode node = new ViterbiNode(wordId, word, userDictionary, index, ViterbiNode.Type.USER);
            lattice.addNode(node, index + 1, index + 1 + length);
            // TODO: Check here that user dictionary term doesn't break graph. Compensate if necessary.
        }
    }


}
