package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

public class FailStep extends ContinuableStep {

    private final String message;

    public FailStep(String message) {
        this.message = message;
    }

    @Override
    void doNext(CommandSender sender) {
        throw new StepException(message);
    }

    @Override
    Step copySelf() {
        return new FailStep(message);
    }
}
