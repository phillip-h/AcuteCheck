package com.github.phillip.h.acutecheck.step;

import org.bukkit.command.CommandSender;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class StepRunner {

    private final CommandSender executor;
    private final Deque<Step> stack = new ArrayDeque<>();

    private final ReentrantLock lock = new ReentrantLock();

    public StepRunner(CommandSender executor) {
        this.executor = Objects.requireNonNull(executor, "null executor");
    }

    public void run(final Step step) {
        lock.lock();
        Objects.requireNonNull(step, "null step");
        stack.push(step);
        runUntilInputNeeded();
        lock.unlock();
    }

    public void enqueueAll(final List<Step> steps) {
        lock.lock();
        steps.forEach(stack::push);
        runUntilInputNeeded();
        lock.unlock();
    }

    public boolean executing() {
        return !stack.isEmpty();
    }

    public boolean requiresInput() {
        if (stack.isEmpty()) throw new IllegalStateException("No step executing");
        return stack.peek().requiresInput();
    }

    public void input(final Object input) {
        lock.lock();
        if (stack.isEmpty()) throw new IllegalStateException("No step executing");
        stack.peek().input(input);
        runUntilInputNeeded();
        lock.unlock();
    }

    private void runUntilInputNeeded() {
        lock.lock();
        while (!stack.isEmpty() && !stack.peek().requiresInput()) {
            stack.pop().next(executor).ifPresent(stack::push);
        }
        lock.unlock();
    }
}
