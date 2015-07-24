/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convenient and compact structure for storing key-value pairs and quickly looking them up,
 * including doing prefix searches
 * <p>
 * Implements <code>Map<String, V></code> interface
 * <p>
 * Note that <code>values()</code>, <code>keySet()</code>, <code>entrySet()</code> and <code>containsValue()</code> have
 * naive implementations
 *
 * @param <V>
 */
public class PatriciaTrie<V> implements Map<String, V> {

    /** Root value is left -- right is unused */
    protected PatriciaNode<V> root;

    /** Number of entries in the trie */
    protected int entries = 0;

    /** Maps String keys to bits */
    private final KeyMapper<String> keyMapper = new StringKeyMapper();

    /**
     * Constructor
     */
    public PatriciaTrie() {
        clear();
    }

    /**
     * Gets value associated with key
     *
     * @param key    key to retrieve value for
     * @return value or null if non-existent
     */
    @Override
    public V get(Object key) {
        // Keys can not be null
        if (key == null) {
            throw new NullPointerException("Key can not be null");
        }
        if (!(key instanceof String)) {
            throw new ClassCastException("Only String keys are supported -- got " + key.getClass());
        }
        // Empty keys are stored in the root
        if (key.equals("")) {
            if (root.getRight() == null) {
                return null;
            } else {
                return root.getRight().getValue();
            }
        }

        // Find nearest node
        PatriciaNode<V> nearest = findNearestNode((String) key);

        // If the nearest node matches key, we have a match
        if (key.equals(nearest.getKey())) {
            return nearest.getValue();
        } else {
            return null;
        }
    }

    /**
     * Puts value into trie identifiable by key. Key needs to be non-null
     *
     * @param key    key to associate with value
     * @param value    value be inserted
     * @return value inserted
     * @throws NullPointerException in case key is null
     */
    @Override
    public V put(String key, V value) {
        // Keys can not be null
        if (key == null) {
            throw new NullPointerException("Key can not be null");
        }

        // Empty keys are stored in the root
        if (key.equals("")) {
            PatriciaNode<V> node = new PatriciaNode<>(key, value, -1);
            node.setValue(value);
            root.setRight(node);
            entries++;
            return value;
        }

        // Find nearest node
        PatriciaNode<V> nearest = findNearestNode(key);

        // Key already exist, replace value and return
        if (key.equals(nearest.getKey())) {
            nearest.setValue(value);
            return value;
        }

        // Find differing bit and create new node to insert
        int bit = findFirstDifferingBit(key, nearest.getKey());
        PatriciaNode<V> node = new PatriciaNode<>(key, value, bit);

        // Insert new node
        insertNode(node);

        entries++;

        return value;
    }

    /**
     * Inserts all key and values entries in a map into the trie
     *
     * @param map    map with entries to insert
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes entry identified by key from the trie - currently unsupported
     *
     * @param key    to remove
     * @return value
     * @throws UnsupportedOperationException
     */
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Remove is currently unsupported");
    }

    /**
     * Test trie membership
     *
     * @param key    to test if exists
     * @return true if trie has key
     */
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key can not be null");
        }
        if (!(key instanceof String)) {
            throw new ClassCastException("Only String keys are supported -- got " + key.getClass());
        }

        return get(key) != null;
    }

    /**
     *  Test trie prefix membership (prefix search using key)
     *
     * @param prefix    key prefix to search
     * @return true if trie contains key prefix
     */
    public boolean containsKeyPrefix(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("Prefix key can not be null");
        }

        // An empty string is a prefix of everything
        if (prefix.equals("")) {
            return true;
        }

        // Find nearest node
        PatriciaNode<V> nearest = findNearestNode(prefix);

        // If no nearest node exist, there isn't any prefix match either
        if (nearest == null) {
            return false;
        }

        // The nearest is the root, so no match
        if (nearest.getKey() == null) {
            return false;
        }

        // Test prefix match
        return nearest.getKey().startsWith(prefix);
    }

    /**
     * @return number of entries in trie
     */
    @Override
    public int size() {
        return entries;
    }

    /**
     * @return true if and only iff the trie is empty
     */
    @Override
    public boolean isEmpty() {
        return entries == 0;
    }

    /**
     * Finds the closest node in the trie matching key
     *
     * @param key
     * @return closest node
     */
    private PatriciaNode<V> findNearestNode(String key) {
        PatriciaNode<V> current = root.getLeft();
        PatriciaNode<V> parent = root;

        while (parent.getBit() < current.getBit()) {
            parent = current;
            if (!keyMapper.isSet(current.getBit(), key)) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
        return current;
    }

    /**
     * Returns the leftmost differing bit index when doing a bitwise comparison of key1 and key2
     *
     * @param key1
     * @param key2
     * @return bit index
     */
    private int findFirstDifferingBit(String key1, String key2) {
        int bit = 0;

        while (keyMapper.isSet(bit, key1) == keyMapper.isSet(bit, key2)) {
            bit++;
        }
        return bit;
    }

    /**
     * Inserts node at into the trie
     *
     * @param node    node to insert
     */
    private void insertNode(PatriciaNode<V> node) {
        PatriciaNode<V> current = root.getLeft();
        PatriciaNode<V> parent = root;

        while (parent.getBit() < current.getBit() && current.getBit() < node.getBit()) {
            parent = current;
            if (!keyMapper.isSet(current.getBit(), node.getKey())) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        if (!keyMapper.isSet(node.getBit(), node.getKey())) {
            node.setLeft(node);
            node.setRight(current);
        } else {
            node.setLeft(current);
            node.setRight(node);
        }

        if (!keyMapper.isSet(parent.getBit(), node.getKey())) {
            parent.setLeft(node);
        } else {
            parent.setRight(node);
        }
    }

    /**
     * Should only be used by {@link PatriciaTrieFormatter}
     *
     * @return trie root
     */
    public PatriciaNode<V> getRoot() {
        return root;
    }

    /**
     * @return key mapper used to map key to bit strings. FIXME: Remove?
     */
    public KeyMapper<String> getKeyMapper() {
        return keyMapper;
    }

    @Override
    public void clear() {
        root = new PatriciaNode<V>(null, null, -1);
        root.setLeft(root);
        entries = 0;
    }

    /**
     * @returns true iff trie contains value
     */
    @Override
    public boolean containsValue(Object value) {
        for (V v : values()) {
            if (v.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return entries in the trie
     */
    @Override
    public Set<Entry<String, V>> entrySet() {
        HashMap<String, V> entries = new HashMap<>();
        entriesR(root.getLeft(), -1, entries);
        return entries.entrySet();
    }

    /**
     * @return keys in the trie
     */
    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        keysR(root.getLeft(), -1, keys);
        return keys;
    }

    /**
     * @return values in the trie
     */
    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<V>();
        valuesR(root.getLeft(), -1, values);
        return values;
    }

    private void valuesR(PatriciaNode<V> node, int bit, List<V> list) {
        if (node.getBit() <= bit) {
            return;
        } else {
            valuesR(node.getLeft(), node.getBit(), list);
            valuesR(node.getRight(), node.getBit(), list);
            list.add(node.getValue());
        }
    }

    private void keysR(PatriciaNode<V> node, int bit, Set<String> keys) {
        if (node.getBit() <= bit) {
            return;
        } else {
            keysR(node.getLeft(), node.getBit(), keys);
            keysR(node.getRight(), node.getBit(), keys);
            keys.add(node.getKey());
        }
    }

    private void entriesR(PatriciaNode<V> node, int bit, Map<String, V> entries) {
        if (node.getBit() <= bit) {
            return;
        } else {
            entriesR(node.getLeft(), node.getBit(), entries);
            entriesR(node.getRight(), node.getBit(), entries);
            entries.put(node.getKey(), node.getValue());
        }
    }

    /**
     * Generic interface to map a key to bits
     *
     * @param <K>
     */
    public interface KeyMapper<K> {
        /** Bit testing */
        public boolean isSet(int bit, K key);

        /** Key formatting */
        public String toBitString(K key);
    }

    /**
     * {@link KeyMapper} mapping Strings to bits
     */
    public static class StringKeyMapper implements KeyMapper<String> {

        public boolean isSet(int bit, String key) {
            if (key == null) {
                return false;
            }

            if (bit >= length(key)) {
                return true;
            }

            int codePoint = Character.codePointAt(key, bit / Character.SIZE);
            int mask = 1 << (Character.SIZE - 1 - (bit % Character.SIZE));
            int result = codePoint & mask;

            if (result != 0) {
                return true;
            } else {
                return false;
            }
        }

        public String toBitString(String key) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length(key); i++) {
                if (isSet(i, key)) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
                if ((i + 1) % 4 == 0 && i < length(key)) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }

        private int length(String key) {
            if (key == null) {
                return 0;
            } else {
                return key.length() * Character.SIZE;
            }
        }
    }
}
