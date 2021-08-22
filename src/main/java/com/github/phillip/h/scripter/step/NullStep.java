package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

class NullStep extends ContinuableStep {

    @Override
    void doNext(CommandSender sender) {}

    @Override
    public String toString() {
        return "NullStep{}";
    }
}
