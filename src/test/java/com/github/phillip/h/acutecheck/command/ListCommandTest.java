package com.github.phillip.h.acutecheck.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListCommandTest {

    @Test
    @DisplayName("ListCommand construction correct")
    void ListCommandConstructionCorrect() {
        assertThrows(NullPointerException.class, () -> new ListCommand(null, "some.perm"));
    }

}