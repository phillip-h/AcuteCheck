package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class RunCompleter implements TabCompleter {

    private final ListCompleter listCompleter;
    private final Map<String, Set<String>> tests;

    public RunCompleter(ListCompleter listCompleter, Map<String, Set<String>> tests) {
        this.listCompleter = Objects.requireNonNull(listCompleter);
        this.tests = Objects.requireNonNull(tests);
    }

    @Override
    public List<String> onTabComplete(final CommandSender cs, final Command c, final String s, final String[] args) {
        switch (args.length) {
            case 0:
            case 1: return Collections.emptyList();
            case 2: return listCompleter.onTabComplete(cs, c, s, args);
            default: return StringUtil.copyPartialMatches(args[args.length - 1],
                                                          tests.getOrDefault(args[1], Collections.emptySet()),
                                                          new ArrayList<>());
        }
    }
}
