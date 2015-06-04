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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserDictionary implements Dictionary {

    private static final int CUSTOM_DICTIONARY_WORD_ID_OFFSET = 100000000;
    private static final int WORD_COST = -100000;
    private static final int LEFT_ID = 5;
    private static final int RIGHT_ID = 5;

    private TreeMap<String, int[]> entries = new TreeMap<>();
	private HashMap<Integer, String> featureEntries = new HashMap<>();

	/**
	 * Lookup words in text
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
            if(!positions.containsKey(pos)){
                positions.put(pos, entries.get(entry));
            }
            offset += pos + entry.length();
            pos = text.indexOf(entry, offset);
        }
    }

    /**
	 * Convert Map of index and wordIdAndLength to array of {wordId, index, length}
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
				int[] token = { wordId + j - 1, current, wordIdAndLength[j] };
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
	public String getReading(int wordId) {
		return getFeature(wordId, 0);
	}

	@Override
	public String getBaseForm(int wordId) {
		return null; // NOTE: Currently unsupported
	}

	@Override
	public String getPartOfSpeech(int wordId) {
		return getFeature(wordId, 1);
	}

	@Override
	public String getAllFeatures(int wordId) {
        StringBuilder sb = new StringBuilder();
        sb.append(getPartOfSpeech(wordId));
        for (int i = 0; i < 6; i++) {
            sb.append(",").append("*");
        }
        sb.append(",").append(getReading(wordId));
        sb.append(",").append("*");
        return sb.toString();
	}

	@Override
	public String[] getAllFeaturesArray(int wordId) {
        String allFeatures = featureEntries.get(wordId);
        if(allFeatures == null) {
            return null;
        }

        List<String> features = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < allFeatures.length(); i++) {
            if (allFeatures.charAt(i) == INTERNAL_SEPARATOR) {
                features.add(allFeatures.substring(start, i));
                start = i + 1;
            }
        }
        features.add(allFeatures.substring(start, allFeatures.length())); // The last feature.
        return features.toArray(new String[features.size()]);
    }


	@Override
	public String getFeature(int wordId, int... fields) {
		String[] allFeatures = getAllFeaturesArray(wordId);
		if (allFeatures == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (fields.length == 0) { // All features
			for (String feature : allFeatures) {
				sb.append(DictionaryEntryLineParser.quoteEscape(feature)).append(",");
			}
		} else if (fields.length == 1) { // One feature doesn't need to escape value
			sb.append(allFeatures[fields[0]]).append(",");
		} else {
			for (int field : fields){
				sb.append(DictionaryEntryLineParser.quoteEscape(allFeatures[field])).append(",");
			}
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	public static UserDictionary read(String filename) throws IOException {
		return read(new FileInputStream(filename));
	}

	public static UserDictionary read(InputStream is) throws IOException {
		UserDictionary dictionary = new UserDictionary();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line;
		int wordId = CUSTOM_DICTIONARY_WORD_ID_OFFSET;

		while ((line = reader.readLine()) != null) {
			// Remove comments
			line = line.replaceAll("#.*$", "");

			// Skip empty lines or comment lines
			if (line.trim().length() == 0) {
				continue;
			}

			String[] values = DictionaryEntryLineParser.parseLine(line);
            String[] segmentation = { values[1] };
            String[] readings = { values[2] };
            String pos = values[3];

            if (isCustomSegmentationEntry(values)) {
                segmentation = splitOnSpace(values[1]);
                readings = splitOnSpace(values[2]);
            }

			if (segmentation.length != readings.length) {
                throw new RuntimeException("User dictionary entry not properly formatted: " + line);
			}

			int[] wordIdAndLength = new int[segmentation.length + 1]; // wordId offset, length, length....
			wordIdAndLength[0] = wordId;
			for (int i = 0; i < segmentation.length; i++) {
				wordIdAndLength[i + 1] = segmentation[i].length();
                dictionary.featureEntries.put(wordId, readings[i] + INTERNAL_SEPARATOR + pos);
				wordId++;
			}
            dictionary.entries.put(values[0], wordIdAndLength);

        }

		reader.close();
		return dictionary;
	}

    private static String[] splitOnSpace(String input) {
        return input.replaceAll("  *", " ").split(" ");
    }

    private static boolean isCustomSegmentationEntry(String[] values) {
        return splitOnSpace(values[1]).length > splitOnSpace(values[0]).length;
    }

}
