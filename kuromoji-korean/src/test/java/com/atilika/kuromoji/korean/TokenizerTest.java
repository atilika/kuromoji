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
package com.atilika.kuromoji.korean;

import com.atilika.kuromoji.Token;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class TokenizerTest {
    private static Tokenizer tokenizer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tokenizer = new Tokenizer.Builder().build();
    }

    @Ignore
    @Test
    public void testKorean() {
        List<Token> tokens = tokenizer.tokenize("한반도의 군사 분계선 이남을 통치하는 국가에 대해서는 대한민국 문서를 참조하십시오. 다른 뜻에 대해서는 한국 (동음이의) 문서를 참조하십시오.");
        
        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm() + "\t" + token.getAllFeatures());
        }
    }
}
