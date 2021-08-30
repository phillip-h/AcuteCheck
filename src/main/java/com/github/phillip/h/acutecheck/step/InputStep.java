package com.github.phillip.h.acutecheck.step;

import org.bukkit.command.CommandSender;

import java.util.Objects;

public class InputStep extends ContinuableStep {

    private final StepRunner runner;
    private final Object input;

    public InputStep(StepRunner runner, Object input) {
        this.runner = Objects.requireNonNull(runner, "null runner");
        this.input = input;
    }

    @Override
    void doNext(CommandSender sender) {
        runner.input(input);
    }

    @Override
    Step copySelf() {
        throw new UnsupportedOperationException("InputStep cannot be copied");
    }

    @Override
    public String toString() {
        return "InputStep{" +
                "runner=" + runner +
                ", input=" + input +
                '}';
    }
}
