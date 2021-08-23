package com.github.phillip.h.scripter.step;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Parser {

    public Step parseSteps(final List<String> steps) {
        if (steps == null) throw new IllegalArgumentException("Null steps list");
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
        if (step == null || step.isBlank()) throw new IllegalArgumentException("Empty step");

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
        } else {
            throw new IllegalArgumentException(String.format("Failed to parse step '%s'", step));
        }
    }

    EchoStep makeWaitMessage() {
        return new EchoStep("type '/scripter continue' to continue.");
    }

    BranchStep makeWaitStep() {
        return new BranchStep("continue");
    }

    EchoStep makeVerifyMessage() {
        return new EchoStep("Verify with one of '/scripter yes', '/scripter no', or '/scripter cancel'");
    }

    BranchStep makeVerifyStep() {
        final BranchStep verify = new BranchStep("yes");
        verify.addBranch("no", new FailStep("Verify failed."));
        verify.addBranch("cancel", new NullStep());
        return verify;
    }

}
