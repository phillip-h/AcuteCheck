package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class ListCompleter implements TabCompleter {

    private final Set<String> testGroups;

    public ListCompleter(Set<String> testGroups) {
        this.testGroups = Objects.requireNonNull(testGroups);
    }

    @Override
    public List<String> onTabComplete(final CommandSender cs, final Command c, final String s, final String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], testGroups, new ArrayList<>());
    }
}
