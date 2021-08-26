package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AcuteCheckCommandTest {

    @Test
    @DisplayName("Command permission must be non-empty")
    void commandPermissionShouldBeNonEmpty() {
        assertThrows(NullPointerException.class, () -> new TestCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new TestCommand(""));
        assertThrows(IllegalArgumentException.class, () -> new TestCommand("   \t \n"));
    }

    @Test
    @DisplayName("handle() should be correct")
    void handleCorrect() {
        final TestCommand command = new TestCommand("perm");
        final CommandSender withPerm = Mockito.mock(CommandSender.class);
        Mockito.doReturn(true).when(withPerm).hasPermission("perm");

        command.handle(withPerm, new String[] {"command"});
        Mockito.verify(withPerm).sendMessage("handled");
    }

    @Test
    @DisplayName("permissibleFor() should be correct")
    void permissibleForCorrect() {
        final CommandSender withPerm = Mockito.mock(CommandSender.class);
        Mockito.doReturn(true).when(withPerm).hasPermission("some.perm");
        Mockito.doReturn(false).when(withPerm).hasPermission("some.other.perm");

        final TestCommand commandOne = new TestCommand("some.perm");
        final TestCommand commandTwo = new TestCommand("some.other.perm");

        assertThat("Permission is checked properly", commandOne.permissibleFor(withPerm));
        assertThat("Permission is checked properly", !commandTwo.permissibleFor(withPerm));
    }

    private static final class TestCommand extends AcuteCheckCommand<CommandSender> {

        public TestCommand(String permission) {
            super(permission);
        }

        @Override
        void doHandle(CommandSender commandSender, String[] args) {
            commandSender.sendMessage("handled");
        }
    }
}