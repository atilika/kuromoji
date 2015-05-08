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

import com.atilika.kuromoji.dict.AbstractDictionaryEntry;
import com.atilika.kuromoji.dict.GenericDictionaryEntry;
import com.atilika.kuromoji.dict.TokenInfoDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public abstract class AbstractTokenInfoDictionaryBuilder<T extends AbstractDictionaryEntry> {

    protected TreeMap<Integer, String> dictionaryEntries = new TreeMap<>(); // wordId, surface form

    private String encoding;

    public AbstractTokenInfoDictionaryBuilder(String encoding) {
        this.encoding = encoding;
    }

    public TokenInfoDictionary build(String dirname) throws IOException {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        };

        ArrayList<File> csvFiles = new ArrayList<>();
        Collections.addAll(csvFiles, new File(dirname).listFiles(filter));
        Collections.sort(csvFiles);

        return buildDictionary(csvFiles);
    }

    public TokenInfoDictionary buildDictionary(List<File> csvFiles) throws IOException {
        TokenInfoDictionary dictionary = new TokenInfoDictionary();

        for (File file : csvFiles) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;

            while ((line = reader.readLine()) != null) {
                // TODO: remove offset parameter
                processLine(dictionary, 0, line);
            }

            reader.close();
        }

        dictionary.generateBufferEntries();

        return dictionary;
    }

    protected void processLine(TokenInfoDictionary dictionary, int offset, String line) {
        T entry = parse(line);

        GenericDictionaryEntry dictionaryEntry = this.generateGenericDictionaryEntry(entry);
        dictionary.put(dictionaryEntry);
    }

    protected abstract GenericDictionaryEntry generateGenericDictionaryEntry(T entry);

    protected abstract T parse(String line);
}
