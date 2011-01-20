/**
 * Copyright © 2010-2011 Atilika Inc.  All rights reserved.
 *
 * See the NOTICE.txt file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.atilika.kuromoji.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.atilika.kuromoji.dict.TokenInfoDictionary;


/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class TokenInfoDictionaryBuilder {
	private static final String DEFAULT_DICTIONARY_ENCODING = "euc-jp";

	/** Internal word id - incrementally assigned as entries are read and added. This will be byte offset of dictionary file*/
	private int offset = 4; // Start from 4. First 4 bytes are used to store size of dictionary file.
	
	private TreeMap<Integer, String> dictionaryEntries; // wordId, surface form
	
	public TokenInfoDictionaryBuilder() {
		dictionaryEntries = new TreeMap<Integer, String>();
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
		TokenInfoDictionary dictionary = new TokenInfoDictionary(100 * 1024 * 1024); // 100MB should be enough
		
		for(File file : csvFiles){
			FileInputStream inputStream = new FileInputStream(file);
			InputStreamReader streamReader = new InputStreamReader(inputStream, DEFAULT_DICTIONARY_ENCODING);
			BufferedReader reader = new BufferedReader(streamReader);

			String line = null;
			while ((line = reader.readLine()) != null) {
				if(!isValid(line)) {
					System.out.println("Entry in CSV is not valid :" + line);
					continue;
				}
				
				int next = dictionary.put(line);
				if(next == offset){
					System.out.println("Failed to process line :" + line);
					continue;
				}
				
				dictionaryEntries.put(offset, line.split(",")[0]);
				offset = next;
				
//				if(line.contains("，")){
//					continue;
//				}
//				String normzlizedLine = Normalizer.normalize(line, Normalizer.Form.NFKC);
//				size = dictionary.put(offset, normzlizedLine);
//				offset = offset + size;
			}
		}
		
		return dictionary;
	}
	
	public Set<Entry<Integer, String>> entrySet() {
		return dictionaryEntries.entrySet();
	}
	
	private boolean isValid(String entry) {
		return entry.split(",").length >= 13;
	}
}
