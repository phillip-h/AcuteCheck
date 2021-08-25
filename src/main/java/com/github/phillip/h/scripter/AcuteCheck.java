package com.github.phillip.h.scripter;

import com.github.phillip.h.scripter.step.Step;
import com.github.phillip.h.scripter.step.StepParser;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcuteCheck extends JavaPlugin {

    private final Map<String, Map<String, Step>> tests = new HashMap<>();

    @Override
    public void onEnable() {
        loadTests();
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
