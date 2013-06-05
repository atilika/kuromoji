/**
 * Copyright © 2010-2013 Atilika Inc. and contributors (CONTRIBUTORS.txt)
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
package com.atilika.kuromoji.util;

public class IpadicFormatter implements Formatter{

    public IpadicFormatter() {
    }/*
      * IPADIC features
      *
      * 0	- surface
      * 1	- left cost
      * 2	- right cost
      * 3	- word cost
      * 4-9	- pos
      * 10	- base form
      * 11	- reading
      * 12	- pronounciation
      *
      * UniDic features
      *
      * 0	- surface
      * 1	- left cost
      * 2	- right cost
      * 3	- word cost
      * 4-9	- pos
      * 10	- base form reading
      * 11	- base form
      * 12	- surface form
      * 13	- surface reading
      */

    public String[] formatEntry(String[] features) {
        return features;
//        if (tokenInfoDictionaryBuilder.getFormat() == DictionaryFormat.IPADIC) {
//            return features;
//        } else {
//            return tokenInfoDictionaryBuilder.formatUniDicEntry(features);
//        }
    }
}