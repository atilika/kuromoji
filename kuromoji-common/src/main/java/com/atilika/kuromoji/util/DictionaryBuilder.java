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

import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;

public class DictionaryBuilder {
	
	public enum DictionaryFormat { IPADIC, UNIDIC, UNIDIC_EXTENDED, NAIST_JDIC }
		
	public void build(DictionaryFormat format,
                      String inputDirname,
                      String outputDirname,
                      String encoding,
                      boolean normalizeEntries,
                      boolean addUnnormalizedEntries,
                      boolean compactTries,
                      String dictionaryFilter) throws IOException {
        File outputDir = new File(outputDirname);
        outputDir.mkdir();
        buildTokenInfoDictionary(format, inputDirname, outputDirname, encoding, normalizeEntries, addUnnormalizedEntries, compactTries, dictionaryFilter);
        buildUnknownWordDictionary(inputDirname, outputDirname, encoding);
        buildConnectionCosts(inputDirname, outputDirname);
	}

    private void buildTokenInfoDictionary(DictionaryFormat format, String inputDirname, String outputDirname, String encoding, boolean normalizeEntries, boolean addUnnormalizedEntries, boolean compactTrie, String dictionaryFilter) throws IOException {
        System.out.println("building tokeninfo dict...");
        TokenInfoDictionaryBuilder tokenInfoBuilder = new TokenInfoDictionaryBuilder(format, encoding, normalizeEntries, addUnnormalizedEntries, dictionaryFilter);
        TokenInfoDictionary tokenInfoDictionary = tokenInfoBuilder.build(inputDirname);

        System.out.print("  building double array trie...");
        DoubleArrayTrie trie = DoubleArrayTrieBuilder.build(tokenInfoBuilder.entrySet(), compactTrie);
        trie.write(outputDirname);
        System.out.println("  done");

        System.out.print("  processing target map...");
        for (Entry<Integer, String> entry : tokenInfoBuilder.entrySet()) {
            int tokenInfoId = entry.getKey();
            String surfaceForm = entry.getValue();
            int doubleArrayId = trie.lookup(surfaceForm);
            assert doubleArrayId > 0;
            tokenInfoDictionary.addMapping(doubleArrayId, tokenInfoId);
        }
        tokenInfoDictionary.write(outputDirname);

        System.out.println("done");
        System.out.println("done");
    }

    private void buildUnknownWordDictionary(String inputDirname, String outputDirname, String encoding) throws IOException {
        System.out.print("building unknown word dict...");
        UnknownDictionaryBuilder unkBuilder = new UnknownDictionaryBuilder(encoding);
        UnknownDictionary unkDictionary = unkBuilder.build(inputDirname);
        unkDictionary.write(outputDirname);
        System.out.println("done");
    }

    private void buildConnectionCosts(String inputDirname, String outputDirname) throws IOException {
        System.out.print("building connection costs...");
        ConnectionCosts connectionCosts = ConnectionCostsBuilder.build(inputDirname + File.separator + "matrix.def");
        OutputStream os = new FileOutputStream(outputDirname + File.separator + ConnectionCosts.FILENAME);
        connectionCosts.write(os);
        os.close();
        System.out.println("done");
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
		DictionaryFormat format;
		if (args[0].equalsIgnoreCase("ipadic")) {
			format = DictionaryFormat.IPADIC;
		} else if (args[0].equalsIgnoreCase("unidic")) {
			format = DictionaryFormat.UNIDIC;
		} else if (args[0].equalsIgnoreCase("unidic-extended")) {
            format = DictionaryFormat.UNIDIC_EXTENDED;
        } else if (args[0].equalsIgnoreCase("naist-jdic")) {
            format = DictionaryFormat.NAIST_JDIC;
        } else {
			System.err.println("Illegal format " + args[0] + " using ipadic instead");
			format = DictionaryFormat.IPADIC;
		}

		String inputDirname = args[1];
		String outputDirname = args[2];
		String inputEncoding = args[3];
		boolean normalizeEntries = Boolean.parseBoolean(args[4]);
		boolean addUnnormalizedEntries = Boolean.parseBoolean(args[5]);
        boolean compactTries = Boolean.parseBoolean(args[6]);

        String dictionaryFilter = "";
        if (args.length == 8) {
            dictionaryFilter = args[7];
        }
		
		DictionaryBuilder builder = new DictionaryBuilder();
		System.out.println("dictionary builder");
		System.out.println("");
		System.out.println("dictionary format: " + format);
		System.out.println("input directory: " + inputDirname);
		System.out.println("output directory: " + outputDirname);
		System.out.println("input encoding: " + inputEncoding);
		System.out.println("normalize entries: " + normalizeEntries);
		System.out.println("add unnormalised entries: " + addUnnormalizedEntries);
        System.out.println("compact tries: " + compactTries);
        System.out.println("dictionary filter: " + dictionaryFilter);
        System.out.println("");
		builder.build(format, inputDirname, outputDirname, inputEncoding, normalizeEntries, addUnnormalizedEntries, compactTries, dictionaryFilter);
	}
	
}
