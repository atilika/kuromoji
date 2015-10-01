package com.atilika.kuromoji.fst.vm;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VirtualMachineTest {

    @Ignore("Needs work for prefix matching")
    @Test
    public void testMatch() throws Exception {
        // testing the input string "a" being accepted or not
        VirtualMachine vm = new VirtualMachine(false);
        Program program = new Program();
        program.addInstruction(false, false, 'a', 1, 1);
        assertEquals(1, vm.run(program, "a"));
    }
}
