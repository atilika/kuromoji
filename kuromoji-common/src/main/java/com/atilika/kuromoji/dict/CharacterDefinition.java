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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

public final class CharacterDefinition {

    // invoke, group, length
    private final EnumMap<CharacterClass, int[]> invokeDefinitionMap;
    private final CharacterClass[] characterCategoryMap;

    public static enum CharacterClass {
        NGRAM, DEFAULT, SPACE, SYMBOL, NUMERIC, ALPHA, CYRILLIC, GREEK, HIRAGANA, KATAKANA, KANJI, KANJINUMERIC, HANGUL, HANJA, HANJANUMERIC;

        public int getId() {
            return ordinal();
        }
    }

    public CharacterDefinition() {
        characterCategoryMap = new CharacterClass[65536];
        invokeDefinitionMap = new EnumMap<CharacterClass, int[]>(CharacterClass.class);

        for (int i = 0; i < characterCategoryMap.length; i++) {
            characterCategoryMap[i] = CharacterClass.DEFAULT;
        }
    }

    /**
     * @see #read(InputStream)
     */
    private CharacterDefinition(CharacterClass[] charCatMap, EnumMap<CharacterClass, int[]> invokeMap) {
        this.characterCategoryMap = charCatMap;
        this.invokeDefinitionMap = invokeMap;
    }


    public int lookup(char c) {
        return characterCategoryMap[c].getId();
    }

    public boolean isInvoke(char c) {
        CharacterClass characterClass = characterCategoryMap[c];
        int[] invokeDefinition = invokeDefinitionMap.get(characterClass);
        return invokeDefinition[0] == 1;
    }

    public boolean isGroup(char c) {
        CharacterClass characterClass = characterCategoryMap[c];
        int[] invokeDefinition = invokeDefinitionMap.get(characterClass);
        return invokeDefinition[1] == 1;
    }

    public boolean isKanji(char c) {
        return characterCategoryMap[c] == CharacterClass.KANJI ||
            characterCategoryMap[c] == CharacterClass.KANJINUMERIC;
    }

    /**
     * Put mapping from unicode code point to character class.
     *
     * @param codePoint          code point
     * @param characterClassName character class name
     */
    public void putCharacterCategory(int codePoint, String characterClassName) {
        characterClassName = characterClassName.split(" ")[0]; // use first
        // category
        // class

        // Override Nakaguro
        if (codePoint == 0x30FB) {
            characterClassName = "SYMBOL";
        }
        characterCategoryMap[codePoint] = CharacterClass.valueOf(characterClassName);
    }

    public void putInvokeDefinition(String characterClassName, int invoke, int group, int length) {
        CharacterClass characterClass = CharacterClass
            .valueOf(characterClassName);
        int[] values = {invoke, group, length};
        invokeDefinitionMap.put(characterClass, values);
    }

    /**
     * Write the contents of this object to a stream.
     */
    void write(OutputStream os) throws IOException {
        DataOutputStream daos = new DataOutputStream(os);

        daos.writeInt(characterCategoryMap.length);
        for (CharacterClass cc : characterCategoryMap) {
            daos.writeByte(cc.ordinal());
        }

        daos.writeInt(invokeDefinitionMap.size());
        for (Map.Entry<CharacterClass, int[]> e : invokeDefinitionMap.entrySet()) {
            daos.writeByte(e.getKey().ordinal());
            int[] arr = e.getValue();
            daos.writeInt(arr.length);
            for (int i : arr) daos.writeInt(arr[i]);
        }
    }

    /**
     * Reconstruct an instance of this class from a stream.
     */
    static CharacterDefinition read(InputStream is) throws IOException {
        DataInputStream dais = new DataInputStream(new BufferedInputStream(is));

        CharacterClass[] fromOrdinal = new CharacterClass[CharacterClass.values().length];
        for (CharacterClass cc : CharacterClass.values()) {
            fromOrdinal[cc.ordinal()] = cc;
        }

        CharacterClass[] charCatMap = new CharacterClass[dais.readInt()];
        for (int i = 0; i < charCatMap.length; i++) {
            charCatMap[i] = fromOrdinal[dais.readByte() & 0xFF];
        }

        EnumMap<CharacterClass, int[]> invokeMap =
            new EnumMap<CharacterClass, int[]>(CharacterClass.class);

        for (int entries = dais.readInt(); --entries >= 0; ) {
            CharacterClass cc = fromOrdinal[dais.readByte() & 0xFF];
            int[] arr = new int[dais.readInt()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = dais.readInt();
            }
            invokeMap.put(cc, arr);
        }

        return new CharacterDefinition(charCatMap, invokeMap);
    }
}
