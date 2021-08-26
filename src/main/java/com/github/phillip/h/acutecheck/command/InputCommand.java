package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.CommandSender;

import java.util.Objects;

public class InputCommand extends AcuteCheckCommand<CommandSender> {

    private final RunCommand runCommand;
    private final Object input;

    public InputCommand(RunCommand runCommand, Object input, final String permission) {
        super(permission);
        this.runCommand = Objects.requireNonNull(runCommand, "null runCommand");
        this.input = Objects.requireNonNull(input, "null input object");
    }

    @Override
    public void doHandle(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("This command does not take arguments.");
        } else {
            runCommand.inputToStepRunner(sender, input);
        }
    }
}
