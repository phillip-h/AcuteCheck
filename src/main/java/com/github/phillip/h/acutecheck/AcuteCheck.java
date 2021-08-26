package com.github.phillip.h.acutecheck;

import com.github.phillip.h.acutecheck.command.HelpCommand;
import com.github.phillip.h.acutelib.commands.TabCompletedMultiCommand;
import com.github.phillip.h.acutecheck.command.InputCommand;
import com.github.phillip.h.acutecheck.command.ListCommand;
import com.github.phillip.h.acutecheck.command.RunCommand;
import com.github.phillip.h.acutecheck.step.Step;
import com.github.phillip.h.acutecheck.step.StepParser;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AcuteCheck extends JavaPlugin {

    private final Map<String, Map<String, Step>> tests = new HashMap<>();

    @Override
    public void onEnable() {
        loadTests();
        configureCommand();
    }

    private void configureCommand() {
        final TabCompletedMultiCommand command = new TabCompletedMultiCommand();
        Objects.requireNonNull(getCommand("acutecheck"), "Command does not exist!").setExecutor(command);

        command.registerGenericSubcommand("help", new HelpCommand("acutecheck.help"));
        command.registerGenericSubcommand("list", new ListCommand(tests, "acutecheck.list"));

        final RunCommand runCommand = new RunCommand(tests, "acutecheck.run");
        command.registerGenericSubcommand("run", runCommand);

        command.registerGenericSubcommand("yes", new InputCommand(runCommand, "yes", "acutecheck.input"));
        command.registerGenericSubcommand("no", new InputCommand(runCommand, "no", "acutecheck.input"));
        command.registerGenericSubcommand("continue", new InputCommand(runCommand, "continue", "acutecheck.input"));
        command.registerGenericSubcommand("cancel", new InputCommand(runCommand, "cancel", "acutecheck.input"));
    }

    private void loadTests() {
        getLogger().info("== Compiling tests ==");
        tests.clear();

        int totalTests = 0;
        final MemorySection testsSection = (MemorySection) getConfig().get("tests");
        for (String testGroup : testsSection.getKeys(false)) {
            final Map<String, Step> testGroupMap = new HashMap<>();
            tests.put(testGroup, testGroupMap);

            final MemorySection testGroupSection = (MemorySection) testsSection.get(testGroup);
            for (String test : testGroupSection.getKeys(false)) {
                final List<String> steps = testGroupSection.getStringList(test);

                try {
                    testGroupMap.put(test, new StepParser(new HashMap<>()).parseSteps(steps));
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

}
