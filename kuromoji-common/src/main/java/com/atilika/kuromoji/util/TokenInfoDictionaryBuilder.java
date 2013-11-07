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
package com.atilika.kuromoji.util;

import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.util.DictionaryBuilder.DictionaryFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenInfoDictionaryBuilder {

    private TreeMap<Integer, String> dictionaryEntries = new TreeMap<Integer, String>(); // wordId, surface form

    private String encoding;

    private boolean normalizeEntries;

    private boolean addUnnormalizedEntries;

    private Pattern dictionaryFilter;

    private Formatter formatter;

    private int bufferOffset = 0; // Internal word id - incrementally assigned as entries are read and added (byte offset in the dictionary file)

    private TokenInfoDictionary dictionary;

    public TokenInfoDictionaryBuilder(DictionaryFormat format, String encoding, boolean normalizeEntries, boolean addUnnormalizedEntries, String dictionaryFilter) {
        if (format == DictionaryFormat.UNIDIC) {
            this.formatter = new UnidicFormatter();
        } else if (format == DictionaryFormat.KOREAN) {
            this.formatter = new KoreanFormatter();
        } else {
            this.formatter = new IpadicFormatter();
        }

        this.encoding = encoding;
        this.normalizeEntries = normalizeEntries;
        this.addUnnormalizedEntries = addUnnormalizedEntries;
        if (dictionaryFilter != null && !dictionaryFilter.isEmpty()) {
            this.dictionaryFilter = Pattern.compile(dictionaryFilter);
        }

        dictionary = new TokenInfoDictionary(10 * 1024 * 1024);

    }

    public TokenInfoDictionary build(String dirname) throws IOException {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        };

        ArrayList<File> csvFiles = new ArrayList<File>();
        for (File file : new File(dirname).listFiles(filter)) {
            csvFiles.add(file);
        }
        return buildDictionary(csvFiles);
    }

    public TokenInfoDictionary buildDictionary(List<File> csvFiles) throws IOException {

        for (File file : csvFiles) {
            Reader reader = new InputStreamReader(new FileInputStream(file), encoding);

            buildDictionary(reader);
            
            reader.close();
        }
        return dictionary;
    }

    public void buildDictionary(Reader reader) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;

        while ((line = bufferedReader.readLine()) != null) {

            if (dictionaryFilter != null) {
                Matcher matcher = dictionaryFilter.matcher(line);
                if (matcher.find()) {
                    continue;
                }
            }

            String[] entry = CSVUtil.parse(line);

//                if (entry.length < 13) {
//                    System.out.println("Entry in CSV is not valid: " + line);
//                    continue;
//                }

            if (normalizeEntries) {
                String[] normalizedEntry = normalizeEntry(entry);
                bufferOffset = addEntry(normalizedEntry, dictionary, dictionaryEntries, bufferOffset);

                if (!isNormalized(entry[0]) && addUnnormalizedEntries) {
                    bufferOffset = addEntry(entry, dictionary, dictionaryEntries, bufferOffset);
                }
            } else {
                bufferOffset = addEntry(entry, dictionary, dictionaryEntries, bufferOffset);
            }
        }
    }

    private int addEntry(String[] entry, TokenInfoDictionary dictionary, TreeMap<Integer, String> entries, int offset) {
        entries.put(offset, entry[0]);
        return dictionary.put(formatter.formatEntry(entry));
//        formatter.formatEntry(entry);
//        return 0;
    }

    private String[] normalizeEntry(String[] entry) {
        String[] normalizedEntry = new String[entry.length];

        for (int i = 0; i < entry.length; i++) {
            normalizedEntry[i] = normalize(entry[i]);
        }
        return normalizedEntry;
    }

    private boolean isNormalized(String input) {
        return input.equals(normalize(input));
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC);
    }

    public Set<Entry<Integer, String>> entrySet() {
        return dictionaryEntries.entrySet();
    }
}
