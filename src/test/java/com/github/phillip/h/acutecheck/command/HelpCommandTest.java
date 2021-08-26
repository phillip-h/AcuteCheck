package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class HelpCommandTest {

    @Test
    @DisplayName("HelpCommand should send messages")
    void helpCommandShouldSendMessages() {
        final CommandSender sender = Mockito.mock(CommandSender.class);
        new HelpCommand("perm").doHandle(sender, new String[] {"help"});
        Mockito.verify(sender, Mockito.atLeastOnce()).sendMessage(anyString());
    }
}