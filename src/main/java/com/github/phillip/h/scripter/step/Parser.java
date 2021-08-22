package com.github.phillip.h.scripter.step;

import java.util.List;

public class Parser {

    public Step parseSteps(final List<String> steps) {
        if (steps == null) throw new IllegalArgumentException("Null steps list");
        final Step base = new NullStep();
        Step working = base;
        for (final String step : steps) {
            final Step next = parseStep(step);
            working.then(next);
            working = next;
        }
        return base;
    }

    Step parseStep(final String step) {
        if (step == null || step.isBlank()) throw new IllegalArgumentException("Empty step");

        if (step.startsWith("echo ")) {
            return new EchoStep(step.substring(5));
        } else if (step.startsWith("echo")) {
            return new EchoStep("");
        } else if (step.startsWith("/")) {
            return new CommandStep(step.substring(1));
        } else {
            throw new IllegalArgumentException(String.format("Failed to parse step '%s'", step));
        }
    }

}
