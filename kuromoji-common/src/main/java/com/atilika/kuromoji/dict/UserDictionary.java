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
package com.atilika.kuromoji.dict;

import com.atilika.kuromoji.util.DictionaryEntryLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UserDictionary implements Dictionary {

    private static final String DEFAULT_FEATURE = "*";

    private static final String FEATURE_SEPARATOR = ",";

    private static final int CUSTOM_DICTIONARY_WORD_ID_OFFSET = 100000000;

    private static final int WORD_COST = -100000;

    private static final int LEFT_ID = 5;

    private static final int RIGHT_ID = 5;

    private int wordId = CUSTOM_DICTIONARY_WORD_ID_OFFSET;

    // The word id below is the word id for the source string
    // surface string => [ word id, 1st token length, 2nd token length, ... , nth token length
    private TreeMap<String, int[]> entries = new TreeMap<>();

    // Maps wordId to reading
    private Map<Integer, String> readings = new HashMap<>();

    // Maps wordId to part-of-speech
    private Map<Integer, String> partOfSpeech = new HashMap<>();

    private final int readingFeature;

    private final int partOfSpeechFeature;

    private final int totalFeatures;

    public UserDictionary(InputStream inputStream, int totalFeatures, int readingFeauture, int partOfSpeechFeature) throws IOException {
        this.totalFeatures = totalFeatures;
        this.readingFeature = readingFeauture;
        this.partOfSpeechFeature = partOfSpeechFeature;
        read(inputStream);
    }

    // TODO: This contructor should be removes - used by DebugTokenizer
    public UserDictionary(InputStream inputStream) throws IOException {
        this(inputStream, 2, 0, 1);
    }

    /**
     * Lookup words in text
     *
     * @param text
     * @return array of {wordId, position, length}
     */
    public int[][] locateUserDefinedWordsInText(String text) {
        TreeMap<Integer, int[]> positions = new TreeMap<>(); // index, [length, length...]

        for (String entry : entries.descendingKeySet()) {
            checkUserDefinedWord(text, entry, positions);
        }

        return toIndexArray(positions);
    }

    private void checkUserDefinedWord(final String text, final String entry, TreeMap<Integer, int[]> positions) {
        int offset = 0;
        int pos = text.indexOf(entry, offset);
        while (offset < text.length() && pos >= 0) {
            if (!positions.containsKey(pos)) {
                positions.put(pos, entries.get(entry));
            }
            offset += pos + entry.length();
            pos = text.indexOf(entry, offset);
        }
    }

    /**
     * Convert Map of index and wordIdAndLength to array of {wordId, index, length}
     *
     * @param input
     * @return array of {wordId, index, length}
     */
    private int[][] toIndexArray(Map<Integer, int[]> input) {
        ArrayList<int[]> result = new ArrayList<>();
        for (int i : input.keySet()) {
            int[] wordIdAndLength = input.get(i);
            int wordId = wordIdAndLength[0];
            // convert length to index
            int current = i;
            for (int j = 1; j < wordIdAndLength.length; j++) { // first entry is wordId offset
                int[] token = {wordId + j - 1, current, wordIdAndLength[j]};
                result.add(token);
                current += wordIdAndLength[j];
            }
        }
        return result.toArray(new int[result.size()][]);
    }

    @Override
    public int getLeftId(int wordId) {
        return LEFT_ID;
    }

    @Override
    public int getRightId(int wordId) {
        return RIGHT_ID;
    }

    @Override
    public int getWordCost(int wordId) {
        return WORD_COST;
    }

    @Override
    public String[] getAllFeaturesArray(int wordId) {
        String[] features = new String[totalFeatures];

        for (int i = 0; i < totalFeatures; i++) {
            features[i] = getFeature(wordId, i);
        }

        return features;
    }

    @Override
    public String getAllFeatures(int wordId) {
        return join(getAllFeaturesArray(wordId));
    }

    @Override
    public String getFeature(int wordId, int... fields) {

        if (fields.length == 0 || fields.length == totalFeatures) {
            return getAllFeatures(wordId);
        }

        String[] features = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {

            int featureNumber = fields[i];

            if (featureNumber == readingFeature) {
                features[i] = readings.get(wordId);
            } else if (featureNumber == partOfSpeechFeature) {
                features[i] = partOfSpeech.get(wordId);
            } else {
                features[i] = DEFAULT_FEATURE;
            }
        }

        return join(features);

    }

    public void read(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String line;

        while ((line = reader.readLine()) != null) {
            // Remove comments and trim leading and trailing whitespace
            line = line.replaceAll("#.*$", "");
            line = line.trim();

            // Skip empty lines or comment lines
            if (line.isEmpty()) {
                continue;
            }

            addEntry(line);
        }

        reader.close();
    }

    public void addEntry(String entry) {
        String[] values = DictionaryEntryLineParser.parseLine(entry);

        String surface = values[0];
        String segmentationValue = values[1];
        String readingsValue = values[2];
        String partOfSpeech = values[3];

        String[] segmentation;
        String[] readings;

        if (isCustomSegmentation(surface, segmentationValue)) {
            segmentation = split(segmentationValue);
            readings = split(readingsValue);
        } else {
            segmentation = new String[]{ segmentationValue };
            readings = new String[]{ readingsValue };
        }

        if (segmentation.length != readings.length) {
            throw new RuntimeException("User dictionary entry not properly formatted: " + entry);
        }

        int[] wordIdAndLengths = new int[segmentation.length + 1]; // wordId offset, length, length....

        wordIdAndLengths[0] = wordId;

        for (int i = 0; i < segmentation.length; i++) {
            wordIdAndLengths[i + 1] = segmentation[i].length();

            this.readings.put(wordId, readings[i]);
            this.partOfSpeech.put(wordId, partOfSpeech);

            wordId++;
        }
        entries.put(surface, wordIdAndLengths);
    }

    private boolean isCustomSegmentation(String surface, String segmentation) {
        return !surface.equals(segmentation);
    }

    private String join(String[] values) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            builder.append(values[i]);

            if (i < values.length - 1) {
                builder.append(FEATURE_SEPARATOR);
            }
        }

        return builder.toString();
    }

    private String[] split(String input) {
        return input.split("\\s+");
    }
}
