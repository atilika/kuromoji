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
package org.atilika.kuromoji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.atilika.kuromoji.Tokenizer.Mode;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class TokenizerRunner {
	public static void main(String[] args) throws IOException {
		Tokenizer tokenizer;
		if (args.length == 1) {
			Mode mode = Mode.valueOf(args[0].toUpperCase());
			tokenizer = Tokenizer.builder().mode(mode).build();
		} else if (args.length == 2) {
			Mode mode = Mode.valueOf(args[0].toUpperCase());
			tokenizer = Tokenizer.builder().mode(mode).userDictionary(args[1]).build();
		} else {
			tokenizer = Tokenizer.builder().build();
		}
		
		System.out.println("Tokenizer ready.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = reader.readLine()) != null) {
			List<Token> result = tokenizer.tokenize(line);
			for(Token token : result) {
				System.out.println(token.getSurfaceForm() + "\t" + token.getAllFeatures());
			}
		}
	}
}
