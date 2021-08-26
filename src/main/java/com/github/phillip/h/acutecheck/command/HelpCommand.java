package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.CommandSender;

// Has AcuteCheck test(s)
public class HelpCommand extends AcuteCheckCommand<CommandSender> {

    public HelpCommand(String permission) {
        super(permission);
    }

    @Override
    void doHandle(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("/ac help: list AcuteCheck commands");
        commandSender.sendMessage("/ac list [test groups...]: list AcuteCheck tests");
        commandSender.sendMessage("/ac run <test group> [tests...]: run AcuteCheck test(s)");
        commandSender.sendMessage("/ac <yes|no|continue|cancel>: interact with running test(s)");
    }
}
