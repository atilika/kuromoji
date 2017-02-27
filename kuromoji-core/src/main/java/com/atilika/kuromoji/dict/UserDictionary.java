/**
 * Copyright © 2010-2017 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

import com.atilika.kuromoji.trie.PatriciaTrie;
import com.atilika.kuromoji.util.DictionaryEntryLineParser;
import com.atilika.kuromoji.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDictionary implements Dictionary {

    private static final int SIMPLE_USERDICT_FIELDS = 4;

    private static final int WORD_COST_BASE = -100000;

    public static final int MINIMUM_WORD_COST = Integer.MIN_VALUE / 2;

    private static final int LEFT_ID = 5;

    private static final int RIGHT_ID = 5;

    private static final String DEFAULT_FEATURE = "*";

    private static final String FEATURE_SEPARATOR = ",";
    // List of user dictionary entries
    private final List<UserDictionaryEntry> entries = new ArrayList<>();
    private final int readingFeature;
    private final int partOfSpeechFeature;
    private final int totalFeatures;
    // The word id below is the word id for the source string
    // surface string => [ word id, 1st token length, 2nd token length, ... , nth token length
    private PatriciaTrie<int[]> surfaces = new PatriciaTrie<>();

    public UserDictionary(InputStream input,
                          int totalFeatures,
                          int readingFeature,
                          int partOfSpeechFeature) throws IOException {
        this.totalFeatures = totalFeatures;
        this.readingFeature = readingFeature;
        this.partOfSpeechFeature = partOfSpeechFeature;
        read(input);
    }

    /**
     * Lookup words in text
     *
     * @param text text to look up user dictionary matches for
     * @return list of UserDictionaryMatch, not null
     */
    public List<UserDictionaryMatch> findUserDictionaryMatches(String text) {
        List<UserDictionaryMatch> matchInfos = new ArrayList<>();
        int startIndex = 0;

        while (startIndex < text.length()) {
            int matchLength = 0;
            int endIndex = 0;

            while (currentInputContainsPotentialMatch(text, startIndex, endIndex)) {
                String matchCandidate = text.substring(startIndex, startIndex + endIndex);

                if (surfaces.containsKey(matchCandidate)) {
                    matchLength = endIndex;
                }

                endIndex++;
            }

            if (matchLength > 0) {
                String match = text.substring(startIndex, startIndex + matchLength);
                int[] details = surfaces.get(match);

                if (details != null) {
                    matchInfos.addAll(
                        makeMatchDetails(startIndex, details)
                    );
                }
            }

            startIndex++;
        }

        return matchInfos;
    }

    private boolean currentInputContainsPotentialMatch(String text, int startIndex, int endIndex) {
        return startIndex + endIndex <= text.length() && surfaces.containsKeyPrefix(text.substring(startIndex, startIndex + endIndex));
    }

    @Override
    public int getLeftId(int wordId) {
        UserDictionaryEntry entry = entries.get(wordId);
        return entry.getLeftId();
    }

    @Override
    public int getRightId(int wordId) {
        UserDictionaryEntry entry = entries.get(wordId);
        return entry.getRightId();
    }

    @Override
    public int getWordCost(int wordId) {
        UserDictionaryEntry entry = entries.get(wordId);
        return entry.getWordCost();
    }

    @Override
    public String getAllFeatures(int wordId) {
        UserDictionaryEntry entry = entries.get(wordId);
        return entry.getAllFeatures();
    }

    @Override
    public String[] getAllFeaturesArray(int wordId) {
        UserDictionaryEntry entry = entries.get(wordId);
        return entry.getAllFeaturesArray();
    }

    @Override
    public String getFeature(int wordId, int... fields) {
        UserDictionaryEntry entry = entries.get(wordId);
        return entry.getFeature(fields);
    }

    private List<UserDictionaryMatch> makeMatchDetails(int matchStartIndex, int[] details) {
        List<UserDictionaryMatch> matchDetails = new ArrayList<>(details.length - 1);

        int wordId = details[0];
        int startIndex = 0;

        for (int i = 1; i < details.length; i++) {
            int matchLength = details[i];

            matchDetails.add(
                new UserDictionaryMatch(wordId, matchStartIndex + startIndex, matchLength)
            );

            startIndex += matchLength;
            wordId++;
        }
        return matchDetails;
    }

    private void read(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(input, StandardCharsets.UTF_8)
        );
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
    }

    public void addEntry(String entry) {
        String[] values = DictionaryEntryLineParser.parseLine(entry);

        if (values.length == SIMPLE_USERDICT_FIELDS) {
            addSimpleEntry(values);
        } else if (values.length == totalFeatures + 4) { // 4 = surface, left id, right id, word cost
            addFullEntry(values);
        } else {
            throw new RuntimeException("Illegal user dictionary entry " + entry);
        }
    }

    private void addFullEntry(String[] values) {

        String surface = values[0];
        int[] costs = new int[]{
            Integer.parseInt(values[1]),
            Integer.parseInt(values[2]),
            Integer.parseInt(values[3])
        };

        String[] features = Arrays.copyOfRange(values, 4, values.length);

        UserDictionaryEntry entry = new UserDictionaryEntry(
            surface, costs, features
        );

        int[] wordIdAndLengths = new int[1 + 1]; // Surface and a single length - the length of surface
        wordIdAndLengths[0] = entries.size();
        wordIdAndLengths[1] = surface.length();

        entries.add(entry);

        surfaces.put(surface, wordIdAndLengths);
    }

    private void addSimpleEntry(String[] values) {
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
            segmentation = new String[]{segmentationValue};
            readings = new String[]{readingsValue};
        }

        if (segmentation.length != readings.length) {
            throw new RuntimeException("User dictionary entry not properly formatted: " + Arrays.asList(values));
        }

        // { wordId, 1st token length, 2nd token length, ... , nth token length
        int[] wordIdAndLengths = new int[segmentation.length + 1];

        int wordId = entries.size();
        wordIdAndLengths[0] = wordId;

        for (int i = 0; i < segmentation.length; i++) {

            wordIdAndLengths[i + 1] = segmentation[i].length();

            String[] features = makeSimpleFeatures(partOfSpeech, readings[i]);
            int[] costs = makeCosts(surface.length());

            UserDictionaryEntry entry = new UserDictionaryEntry(
                segmentation[i], costs, features
            );

            entries.add(entry);
        }

        surfaces.put(surface, wordIdAndLengths);
    }

    private int[] makeCosts(int length) {
        int wordCost = WORD_COST_BASE * length;
        if (wordCost < MINIMUM_WORD_COST) {
            wordCost = MINIMUM_WORD_COST;
        }

        return new int[]{LEFT_ID, RIGHT_ID, wordCost};
    }

    private String[] makeSimpleFeatures(String partOfSpeech, String reading) {
        String[] features = emptyFeatureArray();
        features[partOfSpeechFeature] = partOfSpeech;
        features[readingFeature] = reading;
        return features;
    }

    private String[] emptyFeatureArray() {
        String[] features = new String[totalFeatures];

        for (int i = 0; i < features.length; i++) {
            features[i] = DEFAULT_FEATURE;
        }

        return features;
    }

    private boolean isCustomSegmentation(String surface, String segmentation) {
        return !surface.equals(segmentation);
    }

    private String[] split(String input) {
        return input.split("\\s+");
    }

    public static class UserDictionaryMatch {

        private final int wordId;

        private final int matchStartIndex;

        private final int matchLength;

        public UserDictionaryMatch(int wordId, int matchStartIndex, int matchLength) {
            this.wordId = wordId;
            this.matchStartIndex = matchStartIndex;
            this.matchLength = matchLength;
        }

        public int getWordId() {
            return wordId;
        }

        public int getMatchStartIndex() {
            return matchStartIndex;
        }

        public int getMatchLength() {
            return matchLength;
        }

        @Override
        public String toString() {
            return "UserDictionaryMatch{" +
                "wordId=" + wordId +
                ", matchStartIndex=" + matchStartIndex +
                ", matchLength=" + matchLength +
                '}';
        }
    }

    private class UserDictionaryEntry {

        private String surface;

        private int[] costs;

        private String[] features;

        public UserDictionaryEntry(String surface, int[] costs, String[] features) {
            this.surface = surface;
            this.costs = costs;
            this.features = features;
        }

        public String getSurface() {
            return surface;
        }

        public int getLeftId() {
            return costs[0];
        }

        public int getRightId() {
            return costs[1];
        }

        public int getWordCost() {
            return costs[2];
        }

        public String[] getAllFeaturesArray() {
            return features;
        }

        public String getAllFeatures() {
            return StringUtils.join(features, FEATURE_SEPARATOR);
        }

        public String getFeature(int... fields) {
            String[] f = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                int featureNumber = fields[i];
                f[i] = features[featureNumber];
            }

            return StringUtils.join(f, FEATURE_SEPARATOR);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(surface);
            builder.append(FEATURE_SEPARATOR);
            builder.append(costs[0]);
            builder.append(FEATURE_SEPARATOR);
            builder.append(costs[1]);
            builder.append(FEATURE_SEPARATOR);
            builder.append(costs[2]);
            builder.append(FEATURE_SEPARATOR);
            builder.append(
                StringUtils.join(features, FEATURE_SEPARATOR)
            );
            return builder.toString();
        }
    }
}
