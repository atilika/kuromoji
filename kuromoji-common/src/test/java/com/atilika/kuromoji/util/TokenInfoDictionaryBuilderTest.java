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

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class TokenInfoDictionaryBuilderTest {

    @Test
    public void testKorean() throws IOException {

        TokenInfoDictionaryBuilder builder = new TokenInfoDictionaryBuilder(
            DictionaryBuilder.DictionaryFormat.KOREAN,
            "UTF-8",
            false,
            false,
            null
        );
        
        String input = ""
            + "ㄹ게요,546,802,2990,JX,F,ㄹ게요,*,*,*,*,*\n"
            + "가감승합제,727,1480,3036,NN,F,가감승합제,Compound,*,*,가감승+합제,가감승/NN/1/1+가감승합제/Compound/0/2+합제/NN/1/1\n"
            + "가까와짐,1714,3,0,VA+EC+VX+EC+VX+ETN,T,가까와짐,Inflect,VA,EC,가깝/VA+어/EC+오/VX+아/EC+지/VX+ᄆ/ETN,*";
        
        StringReader reader = new StringReader(input);
        
        builder.buildDictionary(reader);
    }
}
