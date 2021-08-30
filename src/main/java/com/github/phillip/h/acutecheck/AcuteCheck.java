package com.github.phillip.h.acutecheck;

import com.github.phillip.h.acutecheck.command.*;
import com.github.phillip.h.acutecheck.step.StepParserConfig;
import com.github.phillip.h.acutelib.commands.TabCompletedMultiCommand;
import com.github.phillip.h.acutecheck.step.Step;
import com.github.phillip.h.acutecheck.step.StepParser;
import com.github.phillip.h.acutelib.util.Pair;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.phillip.h.acutecheck.step.StepParserConfig.*;

public class AcuteCheck extends JavaPlugin {

    private final Map<String, Map<String, Step>> tests = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        getLogger().info("Config version: " + getConfig().getString("version"));
        loadTests(readAliases());
        configureCommand();
    }

    private void configureCommand() {
        final TabCompletedMultiCommand command = new TabCompletedMultiCommand();
        Objects.requireNonNull(getCommand("acutecheck"), "Command does not exist!").setExecutor(command);

        command.registerGenericSubcommand("help", new HelpCommand("acutecheck.help"));
        command.registerGenericSubcommand("list", new ListCommand(tests, "acutecheck.list"));

        final RunCommand runCommand = new RunCommand(tests, this, "acutecheck.run");
        command.registerGenericSubcommand("run", runCommand);

        command.registerGenericSubcommand("yes", new InputCommand(runCommand, VERIFY_YES_BRANCH, "acutecheck.input"));
        command.registerGenericSubcommand("no", new InputCommand(runCommand, VERIFY_NO_BRANCH, "acutecheck.input"));
        command.registerGenericSubcommand("continue", new InputCommand(runCommand, WAIT_CONTINUE_BRANCH, "acutecheck.input"));
        command.registerGenericSubcommand("cancel", new InputCommand(runCommand, GENERIC_CANCEL_BRANCH, "acutecheck.input"));

        final ListCompleter listCompleter = new ListCompleter(tests.keySet());
        command.registerSubcompletion("list", listCompleter);
        final Map<String, Set<String>> testNames = tests.entrySet()
                                                        .stream()
                                                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().keySet()));
        command.registerSubcompletion("run", new RunCompleter(listCompleter, testNames));
    }

    private void loadTests(final Map<String, Pair<String, String>> aliasMap) {
        getLogger().info("== Compiling tests ==");
        tests.clear();

        final StepParserConfig parserConfig = StepParserConfig
                .defaultConfig(this)
                .withAssertAliases(aliasMap)
                .withWaitMessage("type '/ac continue' to continue or '/ac cancel' to cancel.")
                .withVerifyMessage("Verify with one of '/ac yes', '/ac no', or '/ac cancel'");

        final StepParser parser = new StepParser(parserConfig);

        int totalTests = 0;
        final MemorySection testsSection = (MemorySection) getConfig().get("tests");
        for (String testGroup : testsSection.getKeys(false)) {
            final Map<String, Step> testGroupMap = new HashMap<>();
            tests.put(testGroup, testGroupMap);

            final MemorySection testGroupSection = (MemorySection) testsSection.get(testGroup);
            for (String test : testGroupSection.getKeys(false)) {
                final List<String> steps = testGroupSection.getStringList(test);

                try {
                    testGroupMap.put(test, parser.parseSteps(steps));
                } catch (IllegalArgumentException e) {
                    getLogger().warning(String.format("Compilation failed for test '%s:%s': %s",
                                                      testGroup, test, e.getMessage()));
                }
            }

            getLogger().info(String.format(" - Compiled %d test(s) for group %s", testGroupMap.size(), testGroup));
            totalTests += testGroupMap.size();
        }

        getLogger().info(String.format("Done. %d test(s) loaded", totalTests));
    }

    private Map<String, Pair<String, String>> readAliases() {
        final Map<String, Pair<String, String>> aliasMap = new HashMap<>();
        final MemorySection aliasesSection = (MemorySection) getConfig().get("aliases");
        for (String alias : aliasesSection.getKeys(false)) {
            final MemorySection aliasSection = (MemorySection) aliasesSection.get(alias);
            aliasMap.put(alias, new Pair<>(aliasSection.getString("className"), aliasSection.getString("methodName")));
        }

        getLogger().info(String.format("Loaded %d alias(es)", aliasMap.size()));
        return aliasMap;
    }

}
