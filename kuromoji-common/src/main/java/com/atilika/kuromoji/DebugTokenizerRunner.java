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
package com.atilika.kuromoji;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class DebugTokenizerRunner {
	public static void main(String[] args) throws IOException {
//		DebugTokenizer tokenizer;
//		if (args.length == 1) {
//			Mode mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
//			tokenizer = DebugTokenizer.builder().mode(mode).build();
//		} else if (args.length == 2) {
//			Mode mode = Mode.valueOf(args[0].toUpperCase());
//			tokenizer = DebugTokenizer.builder().mode(mode).userDictionary(args[1]).build();
//		} else {
//			tokenizer = DebugTokenizer.builder().build();
//		}

        if (args.length > 2) {
            usage();
        }
        
        PrintStream out;
        if (args.length == 0) {
            out = System.out;
        } else {
            out = new PrintStream(new File(args[0]), "utf-8");
        }


        DebugTokenizer tokenizer = DebugTokenizer.builder().userDictionary(args[1]).build();

        // Read entire input from stdin into a String
        String input = new Scanner(System.in, "utf-8").useDelimiter("\\A").next();

        String output = tokenizer.debugTokenize(input);

        out.print(output);
        out.flush();
	}
    
    public static void usage() {
        System.out.println("Usage: DebugTokenizerRunner [OUTPUT FILENAME]");
    }
}
