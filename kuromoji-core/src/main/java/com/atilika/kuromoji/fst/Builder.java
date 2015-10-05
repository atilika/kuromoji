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
package com.atilika.kuromoji.fst;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Builder {
    // Note that FST only allows the presorted dictionaries as input.
    private Map<Integer, List<State>> statesDictionary;

    private Compiler compiler = new Compiler();

    private List<State> tempStates;

    public Builder() {
        List<State> stateList = new LinkedList<>();
        stateList.add(new State());
        this.statesDictionary = new HashMap<>();
        this.statesDictionary.put(0, stateList); // temporary setting the start state

        tempStates = new ArrayList<>();
        tempStates.add(this.getStartState()); // initial state
    }

    /**
     * Applies the transducer over the input text
     *
     * @param input input text to transduce
     * @return corresponding value on a match and -1 otherwise
     */
    public int transduce(String input) {
        State currentState = this.getStartState();
        int output = 0; // assuming that output is a int type

        // transitioning according to input
        for (int i = 0; i < input.length(); i++) {
            char currentTransition = input.charAt(i);
            Arc nextArc = currentState.findArc(currentTransition);
            if (nextArc == null) {
                return -1;
            }
            currentState = nextArc.getDestination();
            output += nextArc.getOutput();
        }

        return output;
    }

    /**
     * Get starting state. Note that only start state uses 0 as the key for states dictionary.
     *
     * @return starting state
     */
    public State getStartState() {
        return this.statesDictionary.get(0).get(0);
    }

    /**
     * For this method, once it reads the string, it throws away.
     *
     * @param reader
     * @throws IOException
     */
    public void createDictionaryIncremental(Reader reader) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(reader);
        String previousWord = "";

        int outputValue = 1; // Initialize output value

        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            line = line.replaceAll("#.*$", "");

            if (line.trim().isEmpty()) {
                continue;
            }
            String inputWord = line;
            createDictionaryCommon(inputWord, previousWord, outputValue);
            previousWord = inputWord;
            outputValue++; // allocate the next wordID
        }

        handleLastWord(previousWord);
    }


    /**
     * builds FST given input words and output values
     *
     * @param inputWords
     * @param outputValues
     */
    public void build(String[] inputWords, int[] outputValues) throws IOException {
        String previousWord = "";

        for (int inputWordIdx = 0; inputWordIdx < inputWords.length; inputWordIdx++) {
            String inputWord = inputWords[inputWordIdx];
            createDictionaryCommon(
                inputWord,
                previousWord,
                outputValues == null ? inputWordIdx + 1 : outputValues[inputWordIdx]
            );
            previousWord = inputWord;
        }

        handleLastWord(previousWord);
    }

    private void createDictionaryCommon(String inputWord, String previousWord, int currentOutput) throws IOException {

        int commonPrefixLengthPlusOne = commonPrefixIndice(previousWord, inputWord) + 1;
        // We minimize the states from the suffix of the previous word

        // Dynamically adding additional temporary states if necessary
        if (inputWord.length() >= tempStates.size()) {
            for (int j = tempStates.size(); j <= inputWord.length(); j++) {
                tempStates.add(new State());
            }
        }

        for (int i = previousWord.length(); i >= commonPrefixLengthPlusOne; i--) {
            freezeAndPointToNewState(previousWord, i);
        }

        for (int i = commonPrefixLengthPlusOne; i <= inputWord.length(); i++) {
            tempStates.set(i, new State()); // clearing and assigning new state
            tempStates.get(i - 1).setArc(inputWord.charAt(i - 1), tempStates.get(i));
        }
        tempStates.get(inputWord.length()).setFinal();

        // dealing with common prefix between previous word and the current word
        // (also note that its output must have common prefix too.)
        State currentState = tempStates.get(0);

        for (int i = 0; i < commonPrefixLengthPlusOne - 1; i++) {
            Arc nextArc = currentState.findArc(inputWord.charAt(i));
            currentOutput = excludePrefix(currentOutput, nextArc.getOutput());
            currentState = nextArc.getDestination();
        }

        // currentOutput is the difference of outputs
        State suffixHeadState = tempStates.get(commonPrefixLengthPlusOne - 1);
        suffixHeadState.findArc(inputWord.charAt(commonPrefixLengthPlusOne - 1)).setOutput(currentOutput);

    }

    /**
     * Freeze a new state if there is no equivalent state in the states dictionary.
     *
     * @param previousWord
     * @param i
     */
    private void freezeAndPointToNewState(String previousWord, int i) throws IOException {
        State state = tempStates.get(i - 1);
        char previousWordChar = previousWord.charAt(i - 1);
        int output = state.findArc(previousWordChar).getOutput();
        state.arcs.remove(state.findArc(previousWordChar));
        Arc arcToFrozenState = state.setArc(previousWordChar, output, findEquivalentState(tempStates.get(i)));

        compiler.compileState(arcToFrozenState.getDestination());
    }

    /**
     * Freezing temp states which represent the last word of the input words
     *
     * @param previousWord
     */
    private void handleLastWord(String previousWord) throws IOException {
        for (int i = previousWord.length(); i > 0; i--) {
            freezeAndPointToNewState(previousWord, i);
        }
        compileStartingState();
        findEquivalentState(tempStates.get(0)); // not necessary when compiling is enabled
    }

    /**
     * Compiles and caches the outgoing arcs from the starting state
     */
    private void compileStartingState() throws IOException {

        compiler.compileState(tempStates.get(0));
    }

    /**
     * Returns the indice of common prefix + 1
     *
     * @param prevWord
     * @param currentWord
     * @return
     */
    private int commonPrefixIndice(String prevWord, String currentWord) {
        int i = 0;

        while (i < prevWord.length() && i < currentWord.length()) {
            if (prevWord.charAt(i) != currentWord.charAt(i)) {
                break;
            }
            i += 1;
        }
        return i;
    }

    /**
     * Exclude output of the common prefix from the current output
     *
     * @param word
     * @param prefix
     * @return
     */
    private int excludePrefix(int word, int prefix) {
        return word - prefix;
    }

    /**
     * Find the equivalent state by checking its destination states to when collided.
     *
     * @param state
     * @return returns an equivalent state which is already in the stateDicitonary. If there is no equivalent state,
     * then a new state will created and put into statesDictionary.
     */
    private State findEquivalentState(State state) {
        Integer key = state.hashCode(); // this is going to be the hashCode.

        if (statesDictionary.containsKey(key)) {

            if (state.arcs.size() == 0) {
                // the dead end state (which is unique!)
                return statesDictionary.get(key).get(0);
            }

            // Here, there are multiple states that has the same hashcode. Linear Probing the collidedStates.
            for (State collidedState : statesDictionary.get(key)) {
                if (state.equals(collidedState)) {
                    return collidedState;
                }
            }
        }
        // At this point, we know that there is no equivalent compiled (finalized) node
        State newStateToDic = new State(state); // deep copy
        List<State> stateList = new LinkedList<>();
        if (statesDictionary.containsKey(key)) {
            stateList = statesDictionary.get(key);
            // adding new state to a key
        }
        stateList.add(newStateToDic);
        statesDictionary.put(key, stateList);

        return newStateToDic;
    }

    public Compiler getCompiler() {
        return compiler;
    }
}
