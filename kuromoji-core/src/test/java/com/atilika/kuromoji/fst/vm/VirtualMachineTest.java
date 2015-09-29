package com.atilika.kuromoji.fst.vm;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VirtualMachineTest {

    @Test
    public void testHelloVM() {
        VirtualMachine vm = new VirtualMachine(false);
        Program program = new Program();

        program.addInstruction(Program.HELLO);
        program.addInstruction(Program.FAIL);

        vm.run(program, "");

    }

    @Ignore("Needs work for prefix matching")
    @Test
    public void testMatch() throws Exception {
        // testing the input string "a" being accepted or not
        VirtualMachine vm = new VirtualMachine(false);
        Program program = new Program();
        program.addInstruction(Program.MATCH, 'a', 1, 1);
        program.addInstruction(Program.ACCEPT);

        assertEquals(1, vm.run(program, "a"));
    }
}
