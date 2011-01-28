/**
 * 
 */
package org.atilika.kuromoji;

import org.junit.Test;

public class DebugTokenizerTest {

	@Test
	public void testDebug() {
//		DebugTokenizer tokenizer = DebugTokenizer.builder().userDictionary("/Users/cm/Projects/kuromoji/src/example/resources/").build();		
		DebugTokenizer tokenizer = DebugTokenizer.builder().build();		
		System.out.println(tokenizer.debugTokenize("東京都に住む。"));
	}
}
