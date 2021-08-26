package com.github.phillip.h.acutecheck.step;

import com.github.phillip.h.acutelib.util.Checks;
import com.github.phillip.h.acutelib.util.Pair;

import java.util.*;

public class StepParser {

    private final Map<String, Pair<String, String>> assertAliases;

    public StepParser(Map<String, Pair<String, String>> assertAliases) {
        this.assertAliases = Objects.requireNonNull(assertAliases, "null assertAliases");
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
            return Arrays.asList(makeVerifyMessage(), makeVerifyStep());
        } if (step.equals("wait")) {
            return Arrays.asList(makeWaitMessage(), makeWaitStep());
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
            final Pair<String, String> alias = assertAliases.get(key);
            Objects.requireNonNull(alias, String.format("Unknown alias '%s'", key));
            return Collections.singletonList(new AssertStep(alias.left(), alias.right()));
        } else {
            throw new IllegalArgumentException(String.format("Failed to parse step '%s'", step));
        }
    }

    EchoStep makeWaitMessage() {
        return new EchoStep("type '/ac continue' to continue or '/ac cancel' to cancel.");
    }

    BranchStep makeWaitStep() {
        final BranchStep wait = new BranchStep("continue");
        wait.addBranch("cancel", new NullStep());
        return wait;
    }

    EchoStep makeVerifyMessage() {
        return new EchoStep("Verify with one of '/ac yes', '/ac no', or '/ac cancel'");
    }

    BranchStep makeVerifyStep() {
        final BranchStep verify = new BranchStep("yes");
        verify.addBranch("no", new FailStep("Verify failed."));
        verify.addBranch("cancel", new NullStep());
        return verify;
    }

}
