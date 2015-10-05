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
package com.atilika.kuromoji.fst;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class StateTest {

    @Test
    public void testBinarySearchArc() throws Exception {
        State state = new State();
        State destState = new State();

        Arc ArcA = state.setArc('a', 1, destState);
        Arc ArcB = state.setArc('b', 1, destState);
        Arc ArcC = state.setArc('c', 1, destState);

        assertEquals(ArcA, state.binarySearchArc('a', 0, state.arcs.size()));
        assertEquals(ArcB, state.binarySearchArc('b', 0, state.arcs.size()));
        assertEquals(ArcC, state.binarySearchArc('c', 0, state.arcs.size()));
        assertNull(state.binarySearchArc('d', 0, state.arcs.size()));
    }

    @Test
    public void testFindArcWithFourStates() throws Exception {
        State state = new State();
        State destState = new State();
        Arc ArcA = state.setArc('a', 1, destState);
        Arc ArcB = state.setArc('b', 1, destState);
        Arc ArcC = state.setArc('c', 1, destState);
        Arc ArcD = state.setArc('d', 1, destState);

        assertEquals(ArcA, state.findArc('a'));
        assertEquals(ArcB, state.findArc('b'));
        assertEquals(ArcC, state.findArc('c'));
        assertEquals(ArcD, state.findArc('d'));
    }

    @Test
    public void testFindArcWithSurrogatePairs() throws Exception {
        State state = new State();
        State destState = new State();
        Arc ArcA = state.setArc('a', 1, destState);
        Arc ArcB = state.setArc('b', 1, destState);
        Arc ArcC = state.setArc('c', 1, destState);

        String surrogateOne = "𥝱"; // U+25771
        Arc ArcD = state.setArc((surrogateOne.charAt(0)), 1, destState); // surrogate pair
        Arc ArcE = state.setArc(surrogateOne.charAt(1), 1, destState); // surrogate pair

        assertEquals(ArcA, state.findArc('a'));
        assertEquals(ArcB, state.findArc('b'));
        assertEquals(ArcD, state.findArc(surrogateOne.charAt(0)));
        assertEquals(ArcE, state.findArc(surrogateOne.charAt(1)));
    }
}
