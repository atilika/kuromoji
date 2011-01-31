/**
 * Copyright © 2010-2011 Atilika Inc.  All rights reserved.
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
package org.atilika.kuromoji.viterbi;

import java.io.IOException;

import org.atilika.kuromoji.dict.ConnectionCosts;
import org.atilika.kuromoji.dict.TokenInfoDictionary;
import org.junit.Test;

/**
 * @author Masaru Hasegawa
 * @author Christian Moen
 */
public class ViterbiTest {
	
	private static Viterbi viterbi;
	
	private static TokenInfoDictionary dictionary;
	
	private static ConnectionCosts costs;
	
	@Test
	public void dummyTest() throws IOException, ClassNotFoundException{
//		System.out.print("reading tokeninfo dict...");
//		long dictStart = System.currentTimeMillis();
////		TokenInfoDictionary dictionary = TokenInfoDictionary.read(getClass().getClassLoader().getResourceAsStream(TokenInfoDictionary.FILENAME));
//		System.out.println("done in " + (System.currentTimeMillis() - dictStart) + " ms");
//
//		System.out.print("reading connection costs...");
//		long costStart = System.currentTimeMillis();			
//		ConnectionCosts costs = ConnectionCosts.read(getClass().getClassLoader().getResourceAsStream(ConnectionCosts.FILENAME));
//		System.out.println("done in " + (System.currentTimeMillis() - costStart) + " ms");
//
//		GraphvizFormatter formatter = new GraphvizFormatter(dictionary, costs);
//		
//		Viterbi viterbi = new Tokenizer().initialize(null, Mode.EXTENDED);
//		ViterbiNode[][][] graph = viterbi.build("ピタゴラスイッチ");
//
//		File viterbiDebug = File.createTempFile("debug-viterbi-", ".gv"); 
//		System.out.println("Writing to output file " + viterbiDebug.getCanonicalPath());
//		PrintWriter outputStream = new PrintWriter(new FileOutputStream(viterbiDebug));
//
//		outputStream.println(formatter.format(graph[0], graph[1], viterbi.search(graph)));
//		outputStream.close();
	}
}
