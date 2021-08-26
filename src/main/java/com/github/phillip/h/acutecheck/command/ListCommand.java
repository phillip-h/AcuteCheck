package com.github.phillip.h.acutecheck.command;

import com.github.phillip.h.acutecheck.step.Step;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Objects;

// Has AcuteCheck test(s)
public class ListCommand extends AcuteCheckCommand<CommandSender> {

    private final Map<String, Map<String, Step>> tests;

    public ListCommand(Map<String, Map<String, Step>> tests, final String permission) {
        super(permission);
        this.tests = Objects.requireNonNull(tests, "null tests map");
    }

    @Override
    public void doHandle(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage("Test groups:");
            sender.sendMessage("(Run /ac list [groups...] to view tests in group)");
            tests.keySet().forEach(k -> sender.sendMessage("* " + k));
        } else {
            for (int i = 1; i < args.length; i++) {
                if (!tests.containsKey(args[i])) {
                    sender.sendMessage(String.format("Unknown test group '%s'", args[i]));
                    continue;
                }

                sender.sendMessage(args[i] + ":");
                tests.get(args[i]).keySet().forEach(k -> sender.sendMessage("- " + k));
            }
        }
    }
}
