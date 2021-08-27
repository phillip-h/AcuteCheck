package com.github.phillip.h.acutecheck.step;

import com.github.phillip.h.acutelib.util.Checks;
import com.github.phillip.h.acutelib.util.Pair;

import java.util.*;

public class StepParser {

    private final StepParserConfig stepParserConfig;

    public StepParser(StepParserConfig stepParserConfig) {
        this.stepParserConfig = Objects.requireNonNull(stepParserConfig, "null config");
    }

    public Step parseSteps(final List<String> steps) {
        Objects.requireNonNull(steps, "Null steps list");
        final Step base = new NullStep();
        Step working = base;
        for (final String step : steps) {
            for (Step parsedStep : parseStep(step)) {
                working = working.then(parsedStep);
            }
        }
        return base;
    }

    List<Step> parseStep(final String step) {
        Checks.requireNonEmpty(step, "Step may not be empty");

        if (step.equals("verify")) {
            return stepParserConfig.getVerifySupplier().get();
        } if (step.equals("wait")) {
            return stepParserConfig.getWaitSupplier().get();
        } else if (step.startsWith("echo ")) {
            return Collections.singletonList(new EchoStep(step.substring(5)));
        } else if (step.startsWith("echo")) {
            return Collections.singletonList(new EchoStep(""));
        } else if (step.startsWith("/")) {
            return Collections.singletonList(new CommandStep(step.substring(1)));
        } else if (step.startsWith("assertRaw")) {
            if (step.length() < 10) throw new IllegalArgumentException("assertRaw missing arguments");
            final String[] parts = step.substring(10).split(" ");
            if (parts.length != 2) {
                throw new IllegalArgumentException(String.format("Failed to parse assertRaw statement '%s", step));
            }
            return Collections.singletonList(new AssertStep(parts[0], parts[1]));
        } else if (step.startsWith("assert")) {
            if (step.length() < 7) throw new IllegalArgumentException("assert missing argument");
            final String key = step.substring(7);
            Checks.requireNonEmpty(key, "assert missing argument");
            final Pair<String, String> alias = stepParserConfig.getAssertAliases().get(key);
            Objects.requireNonNull(alias, String.format("Unknown alias '%s'", key));
            return Collections.singletonList(new AssertStep(alias.left(), alias.right()));
        } else {
            throw new IllegalArgumentException(String.format("Failed to parse step '%s'", step));
        }
    }
}
