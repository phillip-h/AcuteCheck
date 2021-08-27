package com.github.phillip.h.acutecheck.command;

import com.github.phillip.h.acutecheck.runner.BasicTestRunner;
import com.github.phillip.h.acutecheck.step.Step;
import com.github.phillip.h.acutelib.util.Pair;
import com.github.phillip.h.acutelib.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RunCommand extends AcuteCheckCommand<CommandSender> {

    private final Map<String, BasicTestRunner> runners = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Step>> stepMap;

    private final Plugin plugin;

    public RunCommand(Map<String, Map<String, Step>> stepMap, Plugin plugin, final String permission) {
        super(permission);
        this.stepMap = Objects.requireNonNull(stepMap, "null stepMap");
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void doHandle(final CommandSender sender, final String[] args) {
        final Queue<Pair<String, Step>> tests;
        // TODO Error checking
        if (args.length == 2) {
            tests = stepMap.get(args[1])
                           .entrySet()
                           .stream()
                           .map(e -> new Pair<>(args[1] + ":" + e.getKey(), e.getValue().copy()))
                           .collect(Collectors.toCollection(LinkedList::new));
        } else if (args.length == 3) {
            tests = Util.asQueue(new Pair<>(args[1] + ":" + args[2], stepMap.get(args[1]).get(args[2]).copy()));
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }

        if (runners.containsKey(sender.getName())) {
            runners.get(sender.getName()).recurse(tests.stream().map(Pair::right).collect(Collectors.toList()));
        } else {


            runners.put(sender.getName(), new BasicTestRunner(
                    plugin,
                    tests,
                    sender,
                    test -> sender.sendMessage(ChatColor.WHITE + "-- " + test),
                    res -> {
                        runners.remove(sender.getName());
                        displayTestResults(sender, res);
                    }));
        }

        // todo need callback or something to clear
    }

    void inputToStepRunner(final CommandSender sender, final Object input) {
        Objects.requireNonNull(runners.get(sender.getName()), "No step running")
               .input(input);
    }

    private void displayTestResults(final CommandSender executor, final Collection<Pair<String, Boolean>> testResults) {
        final List<String> results = testResults.stream().map(this::formatTestResult).collect(Collectors.toList());
        StringBuilder sep = new StringBuilder();
        sep.append("-".repeat(Math.max(0, results.stream().mapToInt(String::length).max().getAsInt())));
        executor.sendMessage(sep.toString());
        executor.sendMessage((testResults.size() > 1 ? "Tests" : "Test") + " complete");
        executor.sendMessage(sep.toString());
        results.forEach(executor::sendMessage);
        executor.sendMessage(sep.toString());

        final long failedCount = testResults.stream().filter(p -> !p.right()).count();
        if (failedCount == 0) {
            executor.sendMessage(ChatColor.GREEN + "ALL TESTS PASSED" + ChatColor.WHITE + " ran " + testResults.size() + " tests, " + failedCount + " failures ");
        } else {
            executor.sendMessage(ChatColor.RED + "TESTS FAILED" + ChatColor.WHITE + " ran " + testResults.size() + " tests, " + failedCount + " failures ");
        }
    }

    private String formatTestResult(final Pair<String, Boolean> testResult) {
        final String res = testResult.right() ? ChatColor.GREEN + "OK " + ChatColor.RESET
                                              : ChatColor.RED + "BAD" + ChatColor.RESET;
        return res + " " + testResult.left();
    }


}
