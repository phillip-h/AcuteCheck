package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class StepRunner {

    private final CommandSender executor;
    private final Deque<Step> stack = new ArrayDeque<>();

    public StepRunner(CommandSender executor) {
        this.executor = Objects.requireNonNull(executor, "null executor");
    }

    public void run(final Step step) {
        Objects.requireNonNull(step, "null step");
        stack.push(step);
        runUntilInputNeeded();
    }

    public boolean executing() {
        return !stack.isEmpty();
    }

    public boolean requiresInput() {
        if (stack.isEmpty()) throw new IllegalStateException("No step executing");
        return stack.peek().requiresInput();
    }

    public void input(final Object input) {
        if (stack.isEmpty()) throw new IllegalStateException("No step executing");
        stack.peek().input(input);
        runUntilInputNeeded();
    }

    private void runUntilInputNeeded() {
        while (!stack.isEmpty() && !stack.peek().requiresInput()) {
            stack.pop().next(executor).ifPresent(stack::push);
        }
    }
}
