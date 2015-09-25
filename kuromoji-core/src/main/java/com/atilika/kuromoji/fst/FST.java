package com.atilika.kuromoji.fst;

import com.atilika.kuromoji.fst.vm.Program;
import com.atilika.kuromoji.fst.vm.VirtualMachine;

import java.io.IOException;
import java.io.InputStream;

public class FST {

    private final VirtualMachine vm = new VirtualMachine();

    private Program program;

    public FST(InputStream input) throws IOException {
        this.program = new Program();
        this.program.readProgramFromFile(input);
    }

    public int lookup(String input) {
        return vm.run(program, input);
    }
}
