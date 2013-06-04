/**
 * Copyright © 2010-2012 Atilika Inc.  All rights reserved.
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
package com.atilika.kuromoji;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.atilika.kuromoji.Tokenizer.Mode;

@Ignore
public class SearchTokenizerTest {

	private static Tokenizer tokenizer;

    @BeforeClass
	public static void beforeClass() throws Exception {
		tokenizer = Tokenizer.builder()
				.mode(Mode.SEARCH)
				.build();
	}

	@Test
	public void testCompoundSplitting() throws IOException {
		assertSegmentation(tokenizer, "src/test/resources/search-segmentation-tests.txt");
	}	
	
	public void assertSegmentation(Tokenizer tokenizer, String testFilename) throws IOException {
		LineNumberReader reader = new LineNumberReader  (new InputStreamReader(new FileInputStream(testFilename), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			// Remove comments
			line = line.replaceAll("#.*$", "");
			// Skip empty lines or comment lines
			if (line.trim().isEmpty()) {
				continue;
			}
			String[] fields = line.split("\t", 2);
			String text = fields[0];
			List<String> tokens = Arrays.asList(fields[1].split("\\s+"));
            System.out.println(tokens);
			assertSegmentation(tokenizer, text, tokens);
		}
	}
	
	public void assertSegmentation(Tokenizer tokenizer, String text, List<String> expectedTokens) {
		List<Token> tokens = tokenizer.tokenize(text);
        System.out.println(text);
        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm());
            System.out.println(token.getAllFeatures());
        }
//        System.out.println(tokens);
		assertEquals("Input: " + text, expectedTokens.size(), tokens.size());
		for (int i = 0; i < tokens.size(); i++) {
			assertEquals(expectedTokens.get(i), tokens.get(i).getSurfaceForm());
		}
	}
}
