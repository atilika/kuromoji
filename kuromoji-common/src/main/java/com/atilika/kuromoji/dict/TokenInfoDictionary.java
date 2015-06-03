/**
 * Copyright © 2010-2013 Atilika Inc. and contributors (CONTRIBUTORS.txt)
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
package com.atilika.kuromoji.dict;

import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.ResourceResolver;
import com.atilika.kuromoji.util.DictionaryEntryLineParser;
import com.atilika.kuromoji.util.FeatureInfoMap;
import com.atilika.kuromoji.util.StringValueMapBuffer;
import com.atilika.kuromoji.util.TokenInfoBuffer;
import com.atilika.kuromoji.util.WordIdMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TokenInfoDictionary implements Dictionary {

    private static final String TOKEN_INFO_DICTIONARY_FILENAME = "tid.dat";
    private static final String FEATURE_MAP_FILENAME = "tid_fet.dat";
    private static final String POS_MAP_FILENAME = "tid_pos.dat";
    private static final String TARGETMAP_FILENAME = "tid_map.dat";

    private static final int LEFT_ID = 0;
    private static final int RIGHT_ID = 1;
    private static final int WORD_COST = 2;
    private static final int TOKEN_INFO_OFFSET = 3;

    protected WordIdMap wordIdMap;
    protected Map<String, Short> pos;
    protected FeatureInfoMap posInfo;
    protected FeatureInfoMap otherInfo;
    protected TokenInfoBuffer tokenInfoBuffer;
    protected StringValueMapBuffer stringValues;
    protected StringValueMapBuffer posValues;

    private List<BufferEntry> entries;
    private List<String> surfaces;
    private List<GenericDictionaryEntry> dictionaryEntries;

    public TokenInfoDictionary() {
        entries = new ArrayList<>();
        surfaces = new ArrayList<>();
        posInfo = new FeatureInfoMap();
        otherInfo = new FeatureInfoMap();
        dictionaryEntries = new ArrayList<>();
        wordIdMap = new WordIdMap();
    }

    /**
     * put the entry in map
     *
     * @param dictionaryEntry
     * 
     */
    public void put(GenericDictionaryEntry dictionaryEntry) {
        dictionaryEntries.add(dictionaryEntry);
    }


    public void generateBufferEntries() {
        for (GenericDictionaryEntry dictionaryEntry : dictionaryEntries) {
            posInfo.mapFeatures(dictionaryEntry.getPosFeatures());
        }

        int entryCount = posInfo.getEntryCount();

        for (GenericDictionaryEntry dictionaryEntry : dictionaryEntries) {

            short leftId = dictionaryEntry.getLeftId();
            short rightId = dictionaryEntry.getRightId();
            short wordCost = dictionaryEntry.getWordCost();

            List<String> allPosFeatures = dictionaryEntry.getPosFeatures();

            List<Integer> posFeatureIds = posInfo.mapFeatures(allPosFeatures);

            List<String> featureList = dictionaryEntry.getFeatures();
            List<Integer> otherFeatureIds = otherInfo.mapFeatures(featureList);

            BufferEntry bufferEntry = new BufferEntry();
            bufferEntry.tokenInfo.add(leftId);
            bufferEntry.tokenInfo.add(rightId);
            bufferEntry.tokenInfo.add(wordCost);

            if (entriesFitInAByte(entryCount)) {
                List<Byte> posFeatureIdBytes = createPosFeatureIds(posFeatureIds);
                bufferEntry.posInfo.addAll(posFeatureIdBytes);
            } else {
                for (Integer posFeatureId : posFeatureIds) {
                    bufferEntry.tokenInfo.add(posFeatureId.shortValue());
                }
            }

            bufferEntry.features.addAll(otherFeatureIds);

            entries.add(bufferEntry);
            surfaces.add(dictionaryEntry.getSurface());
        }
    }

    private boolean entriesFitInAByte(int entryCount) {
        return entryCount <= 0xff;
    }

    private List<Byte> createPosFeatureIds(List<Integer> posFeatureIds) {
        List<Byte> posFeatureIdBytes = new ArrayList<>();
        for (Integer posFeatureId : posFeatureIds) {
            posFeatureIdBytes.add(posFeatureId.byteValue());
        }
        return posFeatureIdBytes;
    }

    public void addMapping(int sourceId, int wordId) {
        wordIdMap.addMapping(sourceId, wordId);
    }

    public int[] lookupWordIds(int sourceId) {
        return wordIdMap.lookUp(sourceId);
    }

    @Override
    public int getLeftId(int wordId) {
        return tokenInfoBuffer.lookupTokenInfo(wordId, LEFT_ID);
    }

    @Override
    public int getRightId(int wordId) {
        return tokenInfoBuffer.lookupTokenInfo(wordId, RIGHT_ID);
    }


    @Override
    public int getWordCost(int wordId) {
        return tokenInfoBuffer.lookupTokenInfo(wordId, WORD_COST);
    }

    @Override
    public String[] getAllFeaturesArray(int wordId) {
        BufferEntry bufferEntry = tokenInfoBuffer.lookupEntry(wordId);

        int posLength = bufferEntry.posInfos.length;
        int featureLength = bufferEntry.featureInfos.length;

        if (posLength == 0) {
            posLength = bufferEntry.tokenInfos.length - TOKEN_INFO_OFFSET;
        }
        String[] result = new String[posLength + featureLength];

        for (int i = 0; i < bufferEntry.posInfos.length; i++) {
            int feature = bufferEntry.posInfos[i] & 0xff;
            String s = posValues.get(feature);
            result[i] = s;
        }

        if (bufferEntry.posInfos.length == 0) {
            posLength = bufferEntry.tokenInfos.length - TOKEN_INFO_OFFSET;

            for (int i = 0; i < posLength; i++) {
                int feature = bufferEntry.tokenInfos[i + TOKEN_INFO_OFFSET];
                String s = posValues.get(feature);
                result[i] = s;
            }
        }

        for (int i = 0; i < featureLength; i++) {
            int feature = bufferEntry.featureInfos[i];
            String s = stringValues.get(feature);
            result[i + posLength] = s;
        }

        return result;
    }

    @Override
    public String getFeature(int wordId, int... fields) {
        if (fields.length == 1) {
            return extractSingleFeature(wordId, fields[0]);
        }

        return extractMultipleFeatures(wordId, fields);
    }

    private String extractSingleFeature(int wordId, int field) {
        String feature;

        if (tokenInfoBuffer.isPosFeature(field)) {
            int featureId = tokenInfoBuffer.lookupPosFeature(wordId, field);
            feature = posValues.get(featureId);
        } else {
            int featureId = tokenInfoBuffer.lookupFeature(wordId, field);
            feature = stringValues.get(featureId);
        }

        return feature;
    }

    private String extractMultipleFeatures(int wordId, int[] fields) {
        String[] allFeatures = getAllFeaturesArray(wordId);
        StringBuilder sb = new StringBuilder();

        if (fields.length == 0) { // All features
            for (String feature : allFeatures) {
                sb.append(DictionaryEntryLineParser.quoteEscape(feature)).append(",");
            }
        } else {
            for (int field : fields) {
                sb.append(DictionaryEntryLineParser.quoteEscape(allFeatures[field])).append(",");
            }
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    @Override
    public String getReading(int wordId) {
        return getFeature(wordId, 7);
    }

    @Override
    public String getAllFeatures(int wordId) {
        return getFeature(wordId);
    }

    @Override
    public String getPartOfSpeech(int wordId) {
        return getFeature(wordId, 0, 1, 2, 3);
    }

    @Override
    public String getBaseForm(int wordId) {
        return getFeature(wordId, 6);
    }

    /**
     * Write dictionary in file
     * Dictionary format is:
     * [Size of dictionary(int)], [entry:{left id(short)}{right id(short)}{word cost(short)}{length of pos info(short)}{pos info(char)}], [entry...], [entry...].....
     *
     * @param directoryName
     * @throws IOException
     */
    public void write(String directoryName) throws IOException {
        writeDictionary(directoryName + File.separator + TOKEN_INFO_DICTIONARY_FILENAME);
        writeMap(directoryName + File.separator + POS_MAP_FILENAME, posInfo);
        writeMap(directoryName + File.separator + FEATURE_MAP_FILENAME, otherInfo);
        writeTargetMap(directoryName + File.separator + TARGETMAP_FILENAME);
    }


    protected void writeMap(String filename, FeatureInfoMap map) throws IOException {
        TreeMap<Integer, String> features = map.invert();

        StringValueMapBuffer mapBuffer = new StringValueMapBuffer(features);
        FileOutputStream fos = new FileOutputStream(filename);
        mapBuffer.write(fos);
    }

    protected void writeDictionary(String filename) throws IOException {
        TokenInfoBuffer tokenInfoBuffer = new TokenInfoBuffer(entries);
        FileOutputStream fos = new FileOutputStream(filename);
        tokenInfoBuffer.write(fos);
    }

    protected void writeTargetMap(String filename) throws IOException {
        wordIdMap.write(new FileOutputStream(filename));
    }

    /**
     * Read dictionary into directly allocated buffer.
     *
     * @return TokenInfoDictionary instance
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static TokenInfoDictionary newInstance(ResourceResolver resolver) throws IOException, ClassNotFoundException {
        TokenInfoDictionary dictionary = new TokenInfoDictionary();
        dictionary.setup(resolver);
        return dictionary;
    }

    private void setup(ResourceResolver resolver) throws IOException, ClassNotFoundException {
        tokenInfoBuffer = new TokenInfoBuffer(resolver.resolve(TOKEN_INFO_DICTIONARY_FILENAME));
        stringValues = new StringValueMapBuffer(resolver.resolve(FEATURE_MAP_FILENAME));
        posValues = new StringValueMapBuffer(resolver.resolve(POS_MAP_FILENAME));
        wordIdMap = new WordIdMap(resolver.resolve(TARGETMAP_FILENAME));
    }

    public static TokenInfoDictionary newInstance() throws IOException, ClassNotFoundException {
        return newInstance(new ClassLoaderResolver(TokenInfoDictionary.class));
    }

    public List<String> getSurfaces() {
        return surfaces;
    }
}
