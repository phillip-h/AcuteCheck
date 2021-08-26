package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class InputCommandTest {

    @Test
    @DisplayName("InputCommand construction should validate parameters")
    void inputCommandConstructionsShouldValidateParameters() {
        assertThrows(NullPointerException.class, () -> new InputCommand(null, null, "perm"));
        assertThrows(NullPointerException.class, () -> new InputCommand(Mockito.mock(RunCommand.class), null, "perm"));
        assertThrows(NullPointerException.class, () -> new InputCommand(null, "input_object", "perm"));
    }

    @Test
    @DisplayName("doHandle() should pass input to RunCommand")
    void doHandleShouldPassInputToRunCommand() {
        final RunCommand runCommand = Mockito.mock(RunCommand.class);
        final InputCommand inputCommand = new InputCommand(runCommand, "input_object_string", "perm");
        final CommandSender commandSender = Mockito.mock(CommandSender.class);

        inputCommand.doHandle(commandSender, new String[] {"input"});
        Mockito.verify(runCommand).inputToStepRunner(commandSender, "input_object_string");
        Mockito.reset(runCommand);

        inputCommand.doHandle(commandSender, new String[] {"input", "with_arg"});
        Mockito.verify(runCommand, Mockito.never()).inputToStepRunner(any(), any());
        Mockito.verify(commandSender).sendMessage(anyString());
    }
}