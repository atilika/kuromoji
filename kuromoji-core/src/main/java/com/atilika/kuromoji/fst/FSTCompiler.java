package com.atilika.kuromoji.fst;

import com.atilika.kuromoji.fst.vm.Program;

public class FSTCompiler {

    private static final int ADDRESS_FAIL = 0;

    public Program program;

    public FSTCompiler() {
        this.program = new Program();
    }

    /**
     * Assigning an target jump address to Arc b and make corresponding Instruction.
     *
     * @param state
     */
    public void compileState(State state) {
        if (state.arcs.size() == 0) {
            // an arc which points to dead end accepting state
            state.setTargetJumpAddress(ADDRESS_FAIL);// assuming dead-end accepting state is always at the address 0
        } else {
            // check whether equivalent destination state is already frozen
            if (state.getTargetJumpAddress() == -1) {
                // The last arc is regarded as a state because currently, VM is running the program backwards.
                int newAddress = makeNewInstructionsForFreezingState(state);
                state.setTargetJumpAddress(newAddress);
            }
        }
    }

    /**
     * Make an instruction for an arc and cache it
     *
     * @param b
     */
    public void compileArcsFromStartingState(Arc b) {
        if (b.getLabel() < program.cacheFirstAddresses.length) {
            compileArcToInstruction(b);
        }
    }

    /**
     * Freeze a new arc since no frozen arcs transiting to the same state.
     *
     * @param freezingState
     * @return the address of new instruction
     */
    public int makeNewInstructionsForFreezingState(State freezingState) {
        program.addInstructionFail();

        for (Arc outgoingArc : freezingState.arcs) {
            compileArcToInstruction(outgoingArc);
        }

        int newAddress = program.size() - 1;
        freezingState.setTargetJumpAddress(newAddress);
        return newAddress;
    }

    private void compileArcToInstruction(Arc d) {
        if (d.getDestination().isFinal()) {
            program.addInstructionMatchOrAccept(d.getLabel(), d.getDestination().getTargetJumpAddress(), d.getOutput());
        } else {
            program.addInstructionMatch(d.getLabel(), d.getDestination().getTargetJumpAddress(), d.getOutput());
        }
    }

    /**
     * Compile Instructions for starting state
     *
     * @param state
     */
    public void compileStartingState(State state) {
        program.addInstructionFail();
        for (Arc arc : state.arcs) {
            compileArcsFromStartingState(arc);
        }
    }

    public Program getProgram() {
        return this.program;
    }
}