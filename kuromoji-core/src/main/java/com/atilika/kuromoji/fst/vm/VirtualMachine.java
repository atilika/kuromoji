package com.atilika.kuromoji.fst.vm;

public class VirtualMachine {

    private boolean useCache;

    public VirtualMachine() {
        this.useCache = true;
    }

    public VirtualMachine(boolean useCache) {
        this.useCache = useCache;
    }

    public int run(Program program, String input) {
        int pc = program.size() - 1; // Compiled in a reverse order
        int accumulator = 0; // CPU register
        int position = 0; // CPU register

        boolean done = false;
        boolean isFirstArc = true;

        while (!done) {

            // Referring to the cache
            if (useCache && isFirstArc && input.charAt(position) < program.getCacheFirstAddresses().length) {
                char inputChar = input.charAt(position);

                if (program.getCacheFirstAddresses()[inputChar] == -1) {
                    accumulator = -1;
                    break;
                }

                pc = program.getCacheFirstAddresses()[inputChar];
                accumulator += program.getCacheFirstOutputs()[inputChar];

                if (input.length() == position + 1 && program.cacheFirstIsAccept[inputChar]) {
                    // last character
                    done = true;
                }

                position++;

                isFirstArc = false;
                continue;
            }

            long instruction = program.getInstruction(pc);
            boolean fail = program.isFail(instruction);
            boolean accept = program.isAccept(instruction);

            if (fail) {
                done = true;
                accumulator = -1;
            } else {
                char label = program.getLabel(instruction);
                int targetAddress = program.getTargetAddress(instruction);
                int output = program.getOutput(instruction);

                if (accept) {
                    if (input.length() == position + 1 && label == input.charAt(position)) {
                        // last character
                        accumulator += output;
                        done = true;
                    } else {
                        if (position < input.length() && label == input.charAt(position)) {
                            accumulator += output;
                            pc = targetAddress + 1; // JUMP to Address i.arg2
                            position++; // move the input char pointer
                        } else {
                            // We're at the end of input and we didn't match, which means a prefix match
                            if (position == input.length()) {
                                accumulator = 0;
                                done = true;
                            }
                        }
                    }
                } else if (position <= input.length()) {
                    // We're at the end of input and we didn't match, which means a prefix match
                    if (position == input.length()) {
                        accumulator = 0;
                        done = true;
                    } else if (label == input.charAt(position)) {
                        accumulator += output;
                        pc = targetAddress + 1; // JUMP to Address i.arg2
                        position++; // move the input char pointer
                    }
                }
            }

            pc--;
        }

        return accumulator;

    }
}
