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
        int pc = program.endOfTheProgram / Program.BYTES_PER_INSTRUCTIONS - 1; // Compiled in a reverse order
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

            short opcode = program.getOpcode(pc);

            switch (opcode) {

                case Program.MATCH:

                    char arg1 = program.getArg1(pc);

                    if (position > input.length()) {
                        break;
                    }

                    // We're at the end of input and we didn't match, which means a prefix match
                    if (position == input.length()) {
                        accumulator = 0;
                        done = true;
                        break;
                    }

                    if (arg1 == input.charAt(position)) {
                        accumulator += program.getArg3(pc);
                        pc = program.getArg2(pc) + 1; // JUMP to Address i.arg2
                        position += 1; // move the input char pointer
                    }

                    break;

                case Program.HELLO:
                    System.out.println("hello!");
                    break;

                case Program.ACCEPT:
                    if (input.length() == position) {
                        done = true;
                    }
                    break;

                case Program.ACCEPT_OR_MATCH:
                    arg1 = program.getArg1(pc);

                    if (input.length() == position + 1 && arg1 == input.charAt(position)) {
                        // last character
                        accumulator += program.getArg3(pc);
                        done = true;
                    } else {
                        if (position < input.length() && arg1 == input.charAt(position)) {
                            accumulator += program.getArg3(pc);
                            pc = program.getArg2(pc) + 1; // JUMP to Address i.arg2
                            position += 1; // move the input char pointer
                        } else {
                            // We're at the end of input and we didn't match, which means a prefix match
                            if (position == input.length()) {
                                accumulator = 0;
                                done = true;
                            }
                        }
                    }
                    break;

                case Program.FAIL:
                    done = true;
                    accumulator = -1;
                    break;

                default:
                    throw new RuntimeException("Unexpected program status. Terminating FST!");
            }

            pc--;
        }

        return accumulator;

    }
}
