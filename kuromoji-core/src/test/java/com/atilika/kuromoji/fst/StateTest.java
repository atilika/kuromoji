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