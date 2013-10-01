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
import sun.misc.Regexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenInfoDictionaryBuilder {

    /**
     * Internal word id - incrementally assigned as entries are read and added. This will be byte offset of dictionary file
     */
    private int offset = 0;

    private TreeMap<Integer, String> dictionaryEntries; // wordId, surface form

    private String encoding = "euc-jp";

    private boolean shouldAddNormalizedEntries = false;

    private Pattern dictionaryFilter = null;

    //    private DictionaryFormat format = DictionaryFormat.IPADIC;
    private Formatter formatter = new IpadicFormatter();

    public TokenInfoDictionaryBuilder(DictionaryFormat format, String encoding, boolean shouldAddNormalizedEntries, String dictionaryFilter) {
//        this.format = format;
        if (format == DictionaryFormat.UNIDIC) {
            this.formatter = new UnidicFormatter();
        }
        this.encoding = encoding;
        this.dictionaryEntries = new TreeMap<Integer, String>();
        this.shouldAddNormalizedEntries = shouldAddNormalizedEntries;
        if (dictionaryFilter != null && !dictionaryFilter.isEmpty()) {
            this.dictionaryFilter = Pattern.compile(dictionaryFilter);
        }
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
        TokenInfoDictionary dictionary = new TokenInfoDictionary(10 * 1024 * 1024);

        for (File file : csvFiles) {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(inputStream, encoding);
            BufferedReader reader = new BufferedReader(streamReader);

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (dictionaryFilter != null) {
                    Matcher matcher = dictionaryFilter.matcher(line);
                    if (matcher.find()) {
//                    System.out.println("Skips line: " + line);
                        continue;
                    }
                }
                String[] entry = CSVUtil.parse(line);
                if (entry.length < 13) {
                    System.out.println("Entry in CSV is not valid: " + line);
                    continue;
                }

                int next = dictionary.put(formatter.formatEntry(entry));

                if (next == offset) {
                    System.out.println("Failed to process line: " + line);
                    continue;
                }

                dictionaryEntries.put(offset, entry[0]);
                offset = next;

                // NFKC normalize dictionary entry
                if (shouldAddNormalizedEntries) {
                    addNormalizedEntry(dictionary, entry);
                }
            }
        }

        return dictionary;
    }

    private void addNormalizedEntry(TokenInfoDictionary dictionary, String[] entry) {

        if (!isNormalized(entry[0])) {
            int next;

            String[] normalizedEntry = new String[entry.length];

            for (int i = 0; i < entry.length; i++) {
                normalizedEntry[i] = Normalizer.normalize(entry[i], Normalizer.Form.NFKC);
            }

            next = dictionary.put(formatter.formatEntry(normalizedEntry));
            dictionaryEntries.put(offset, normalizedEntry[0]);
            offset = next;
        }
    }

    private boolean isNormalized(String s) {
        return s.equals(Normalizer.normalize(s, Normalizer.Form.NFKC));
    }

    public Set<Entry<Integer, String>> entrySet() {
        return dictionaryEntries.entrySet();
    }
}
