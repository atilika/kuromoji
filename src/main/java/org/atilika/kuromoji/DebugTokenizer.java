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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.atilika.kuromoji.Tokenizer.Mode;
import org.atilika.kuromoji.dict.Dictionaries;
import org.atilika.kuromoji.dict.UserDictionary;
import org.atilika.kuromoji.viterbi.ViterbiFormatter;
import org.atilika.kuromoji.viterbi.Viterbi;
import org.atilika.kuromoji.viterbi.ViterbiNode;

public class DebugTokenizer {

	private ViterbiFormatter formatter;
	
	private Viterbi viterbi;
	
	protected DebugTokenizer(UserDictionary userDictionary, Mode mode) {

		this.viterbi = new Viterbi(Dictionaries.getTrie(),
								   Dictionaries.getDictionary(),
								   Dictionaries.getUnknownDictionary(),
								   Dictionaries.getCosts(),
								   userDictionary,
								   mode);
		
		this.formatter = new ViterbiFormatter(Dictionaries.getCosts());
	}
	
	public String debugTokenize(String text) {
		ViterbiNode[][][] lattice = this.viterbi.build(text);
		List<ViterbiNode> bestPath = this.viterbi.search(lattice);
		return this.formatter.format(lattice[0], lattice[1], bestPath);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {

		private Mode mode = Mode.NORMAL;
		
		private UserDictionary userDictionary = null;
		
		public synchronized Builder mode(Mode mode) {
			this.mode = mode;
			return this;
		}
		
		public synchronized Builder userDictionary(InputStream userDictionaryInputStream)
			throws IOException {
			this.userDictionary = UserDictionary.read(userDictionaryInputStream);
			return this;
		}

		public synchronized Builder userDictionary(String userDictionaryPath)
			throws FileNotFoundException, IOException {
			this.userDictionary(new BufferedInputStream(new FileInputStream(userDictionaryPath)));
			return this;
		}
		
		public synchronized DebugTokenizer build() {
			return new DebugTokenizer(userDictionary, mode);
		}
	}
}
