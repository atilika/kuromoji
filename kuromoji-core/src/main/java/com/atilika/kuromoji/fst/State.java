package com.atilika.kuromoji.fst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class State {
    List<Arc> arcs;
    private boolean isFinal = false;
    boolean visited; //for visualization purpose
    private int targetJumpAddress = -1;

    public State() {
        this.arcs = new ArrayList<>();
    } // INITIAL_CAPACITY not set

    /**
     * Copy constructor
     */
    public State(State source) {
        this.arcs = source.arcs;
        this.isFinal = source.isFinal;
    }

    public int getTargetJumpAddress() {
        return this.targetJumpAddress;
    }

    public void setTargetJumpAddress(int targetJumpAddress) {
        this.targetJumpAddress = targetJumpAddress;
    }

    public Arc setArc(char transition, int output, State toState) {
//        Arc existingArc = findArc(transition);
//        if (existingArc != null) {
//            // does override existing arc
//            arcs.remove(existingArc);
//        }
        // Assuming no duplicate transition character
        Arc newArc = new Arc(output, toState, transition);
        arcs.add(newArc);
        return newArc;
    }

    public void setArc(char transition, State toState) {
//        if (findArc(transition) != null) {
////            does not override existing arc
//            return;
//        }
        // Assuming no duplicate transition character
        Arc newArc = new Arc(toState);
        newArc.setLabel(transition);
        arcs.add(newArc);
    }

    public List<Character> getAllTransitionStrings() {
        List<Character> retList = new ArrayList<Character>();

        for (Arc arc : arcs) {
            retList.add(arc.getLabel());
        }

        Collections.sort(retList);

        return retList;
    }

    public void setFinal() {
        this.isFinal = true;
    }

    public boolean isFinal() { return this.isFinal; }

//    public Arc findArc(char transition) {
//        // linear search
//        for (Arc arc : arcs) {
//            if (arc.getLabel() == transition) {
//                return arc;
//            }
//        }
//
//        return null;
//    }

    public Arc findArc(char transition) {
        return binarySearchArc(transition, 0, this.arcs.size());
    }

    public Arc binarySearchArc(char transition, int beginIndice, int endIndice) {
        if (beginIndice >= endIndice) {
            return null;
        }

        int indice = beginIndice + (endIndice - beginIndice) / 2; // round down

        if (arcs.get(indice).getLabel() == transition) {
            return arcs.get(indice);
        }
        else if (arcs.get(indice).getLabel() > transition) {
            // transition char is placed at the left part of the array
            return binarySearchArc(transition, beginIndice, indice);
        }
        else if (arcs.get(indice).getLabel() < transition) {
            // transition char is placed at the right part of the array
            return binarySearchArc(transition, indice + 1, endIndice);
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (isFinal != state.isFinal) return false;
        if (arcs != null) {
            if (!arcs.equals(state.arcs)) return false;
        } else {
            if (state.arcs != null) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = arcs != null ? arcs.hashCode() : 0;

        result = 31 * result + (isFinal ? 1 : 0);
        return result;
    }
}
