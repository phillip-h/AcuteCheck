package com.github.phillip.h.acutecheck.step;

import com.github.phillip.h.acutelib.util.Checks;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class StepRunner {

    private final CommandSender executor;
    private final Deque<Step> stack = new ArrayDeque<>();
    private final int stackSize = 64;

    private final ReentrantLock lock = new ReentrantLock();

    public StepRunner(CommandSender executor) {
        this.executor = Objects.requireNonNull(executor, "null executor");
    }

    public void run(final Step step) {
        Objects.requireNonNull(step, "null step");
        run(Collections.singletonList(step));
    }

    public void run(final List<Step> steps) {
        Checks.requireNonEmpty(steps, "empty steps list");
        lock.lock();
        if (stack.size() + steps.size() >= stackSize) {
            throw new IllegalStateException("StepRunner stack overflow");
        }
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
