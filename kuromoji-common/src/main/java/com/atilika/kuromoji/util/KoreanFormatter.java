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

public class KoreanFormatter implements Formatter {

    /*
     * mecab-ko features
     * 
     * 0	- surface
     * 1	- left cost
     * 2	- right cost
     * 3	- word cost
     * 4	- pos
     * 5	- hangul jongseong
     * 6	- reading form
     * 7	- type (inflect, compound: compound noun, preanalysis)
     * 8	- start pos tag (inflect or preanalysis only)
     * 9	- end pos tag (inflect or preanalysis only)
     * 10	- compound expression
     * 11	- compound expression with token position info
     */

    public String[] formatEntry(String[] features) {

        int posInjections = 5;

        String[] koreanFeatures = new String[19];

        for (int i = 0; i < 5; i++) {
            koreanFeatures[i] = features[i];
        }

        // Inject two empty pos tags
        for (int i = 0; i < posInjections; i++) {
            koreanFeatures[5 + i] = "*";
        }

        // Use surface form as base form
        koreanFeatures[10] = features[0];

        // Use reading and pronounciation
        koreanFeatures[11] = features[6];
        koreanFeatures[12] = features[6];

        // Hangul jongseong
        koreanFeatures[13] = features[6];

        koreanFeatures[14] = features[7];
        koreanFeatures[15] = features[8];
        koreanFeatures[16] = features[9];
        koreanFeatures[17] = features[10];
        koreanFeatures[18] = features[11];

        return koreanFeatures;
    }
}