package com.github.phillip.h.acutecheck;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcuteCheckTest {

    @Test
    void checkForRecursiveInstructionsShouldBeCorrect() {
        assertDoesNotThrow(() -> AcuteCheck.checkForRecursiveInstructions(Arrays.asList(
                "echo foo",
                "assert assertion",
                "/ac list",
                "/acutecheck help",
                "/clear",
                "wait",
                "verify"
        )));

        assertDoesNotThrow(() -> AcuteCheck.checkForRecursiveInstructions(Arrays.asList(
                "/ac run foo bar",
                "recurse",
                "echo foo",
                "echo hello",
                "/ac run foo bar",
                "recurse",
                "echo foo",
                "echo hello",
                "/ac run foo bar",
                "recurse"
        )));

        assertThrows(IllegalArgumentException.class, () -> AcuteCheck.checkForRecursiveInstructions(Arrays.asList(
                "echo foo",
                "echo hello",
                "/ac run foo bar"
        )));

        assertThrows(IllegalArgumentException.class, () -> AcuteCheck.checkForRecursiveInstructions(Arrays.asList(
                "recurse",
                "/ac run foo bar"
        )));

        assertThrows(IllegalArgumentException.class, () -> AcuteCheck.checkForRecursiveInstructions(Arrays.asList(
                "echo foo",
                "recurse",
                "echo bar"
        )));
    }
}