package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

import java.util.Objects;

class EchoStep extends ContinuableStep {

    private final String message;

    EchoStep(String message) {
        Objects.requireNonNull(message, "Null message");
        this.message = message;
    }

    @Override
    public void doNext(CommandSender sender) {
        sender.sendMessage(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EchoStep echoStep = (EchoStep) o;
        return Objects.equals(message, echoStep.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "EchoStep{" +
                "message='" + message + '\'' +
                '}';
    }
}
