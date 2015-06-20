/**
 * Copyright 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.dict;

public final class CharacterDefinitions {

    public static final int INVOKE = 0;

    public static final int GROUP = 1;

    private static final int LENGTH = 2; // Not used as of now

    private final int[][] categoryDefinitions;

    private final int[][] codepointMappings;

    private final String[] categorySymbols;

    public CharacterDefinitions(int[][] categoryDefinitions,
                                int[][] codepointMappings,
                                String[] categorySymbols) {
        this.categoryDefinitions = categoryDefinitions;
        this.codepointMappings = codepointMappings;
        this.categorySymbols = categorySymbols;
    }

    // TODO: Override Nakaguro
//    if (codePoint == 0x30FB) {
//        characterClassName = "SYMBOL";
//    }


    public int[] lookup(char c) {
        return codepointMappings[c];
    }

    public int[] lookupCategories(char c) {
        return codepointMappings[c];
    }

    public int[] lookupDefinition(int category) {
        return categoryDefinitions[category];
    }

    public boolean isInvoke(int[] definition) {
        return definition[0] == 1;
    }

    public boolean isGroup(int[] definition) {
        return definition[1] == 1;
    }

    public int[] isInvoke(char c) {
        return checkDefinition(c, INVOKE);
    }

    public int[] isGroup(char c) {
        return checkDefinition(c, GROUP);
    }

    private int[] checkDefinition(char c, int aspect) {
        int[] categoryIds = codepointMappings[c];

        int[] result = new int[categoryIds.length];

        for (int i = 0; i < categoryIds.length; i++) {
            int categoryId = categoryIds[i];
            int[] definition = categoryDefinitions[categoryId];

            result[i] = definition[aspect];
        }

        return result;
    }
}
