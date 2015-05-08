/**
 * Copyright 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atilika.kuromoji.entities.dict;

import com.atilika.kuromoji.dict.GenericDictionaryEntry;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.util.AbstractTokenInfoDictionaryBuilder;
import com.atilika.kuromoji.util.DictionaryEntryLineParser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenInfoDictionaryBuilder extends AbstractTokenInfoDictionaryBuilder<DictionaryEntry> {

    private final DictionaryEntryLineParser lineParser = new DictionaryEntryLineParser();

    private boolean normalizeEntries;
    private boolean addUnnormalizedEntries;

    private Pattern dictionaryFilter;

    public TokenInfoDictionaryBuilder(String encoding, boolean normalizeEntries, boolean addUnnormalizedEntries, String dictionaryFilter) {
        super(encoding);

        this.normalizeEntries = normalizeEntries;
        this.addUnnormalizedEntries = addUnnormalizedEntries;

        if (dictionaryFilter != null && !dictionaryFilter.isEmpty()) {
            this.dictionaryFilter = Pattern.compile(dictionaryFilter);
        }

        System.out.println("normalize: " + normalizeEntries);
    }

    @Override
    protected DictionaryEntry parse(String line) {
        String[] fields = DictionaryEntryLineParser.parseLine(line);
        DictionaryEntry dictionaryEntry = new DictionaryEntry(fields);
        return dictionaryEntry;
    }

    @Override
    protected GenericDictionaryEntry generateGenericDictionaryEntry(DictionaryEntry entry) {
        List<String> pos = extractPosFeatures(entry);
        List<String> features = extractOtherFeatures(entry);

        return new GenericDictionaryEntry.Builder()
            .surface(entry.getSurface())
            .leftId(entry.getLeftId())
            .rightId(entry.getRightId())
            .wordCost(entry.getWordCost())
            .pos(pos)
            .features(features)
            .build();
    }

    public List<String> extractPosFeatures(DictionaryEntry entry) {
        List<String> posFeatures = new ArrayList<>();

        posFeatures.add(entry.getPosLevel1());
        posFeatures.add(entry.getPosLevel2());
        posFeatures.add(entry.getPosLevel3());
        posFeatures.add(entry.getPosLevel4());

        posFeatures.add(entry.getConjugationType());
        posFeatures.add(entry.getConjugationForm());

        return posFeatures;
    }

    public List<String> extractOtherFeatures(DictionaryEntry entry) {
        List<String> otherFeatures = new ArrayList<>();

        otherFeatures.add(entry.getLemmaReadingForm());
        otherFeatures.add(entry.getLemma());
        otherFeatures.add(entry.getWrittenForm());

        otherFeatures.add(entry.getPronunciation());
        otherFeatures.add(entry.getWrittenBaseForm());

        otherFeatures.add(entry.getPronunciationBaseForm());
        otherFeatures.add(entry.getLanguageType());
        otherFeatures.add(entry.getInitialSoundAlterationType());
        otherFeatures.add(entry.getInitialSoundAlterationForm());
        otherFeatures.add(entry.getFinalSoundAlterationType());
        otherFeatures.add(entry.getFinalSoundAlterationForm());

        return otherFeatures;
    }

    @Override
    protected void processLine(TokenInfoDictionary dictionary, int offset, String line) {
        DictionaryEntry entry = parse(line);

        GenericDictionaryEntry dictionaryEntry = generateGenericDictionaryEntry(entry);
        if (isSkipEntry(dictionaryEntry.getSurface())) {
            return;// offset;
        }

        if (normalizeEntries) {
            String normalizedLineEntry = normalize(dictionaryEntry.getSurface());

            if (isSkipEntry(normalizedLineEntry)) {
                System.out.println("Skipping line because it normalised to a skip: " + line); // + " (normalize line is: " + normalizedLineEntry + ")");
                return;// offset;
            }

            addEntry(dictionaryEntry, dictionary, dictionaryEntries, offset, true);

            if (!isNormalized(line) && addUnnormalizedEntries) {
                addEntry(dictionaryEntry, dictionary, dictionaryEntries, offset, false);
            }
        } else {
            addEntry(dictionaryEntry, dictionary, dictionaryEntries, offset, false);
        }
//        return offset;
    }

    private void addEntry(GenericDictionaryEntry dictionaryEntry, TokenInfoDictionary dictionary, TreeMap<Integer, String> entries, int offset, boolean shouldNormalize) {
        if (shouldNormalize) {
            entries.put(offset, normalize(dictionaryEntry.getSurface()));
        } else {
            entries.put(offset, dictionaryEntry.getSurface());
        }
        dictionary.put(dictionaryEntry);
    }

    private boolean isSkipEntry(String lineEntry) {

        if (dictionaryFilter == null) {
            return false;
        }

        Matcher matcher = dictionaryFilter.matcher(lineEntry);
        return matcher.find();
    }

    private boolean isNormalized(String input) {
        return input.equals(normalize(input));
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC);
    }

}
