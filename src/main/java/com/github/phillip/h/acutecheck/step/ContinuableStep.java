package com.github.phillip.h.acutecheck.step;

import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.Optional;

abstract class ContinuableStep implements Step {

    private Step continuation;

    abstract void doNext(final CommandSender sender);

    abstract Step copySelf();

    @Override
    public final Optional<Step> next(CommandSender sender) {
        doNext(sender);
        return Optional.ofNullable(continuation);
    }

    @Override
    public final Step then(Step next) {
        Objects.requireNonNull(next, "Next step cannot be null");
        if (continuation != null) throw new IllegalStateException("Step already has continuation");
        continuation = next;
        return next;
    }

    @Override
    public void input(Object input) {
        throw new IllegalStateException("Step does not take input");
    }

    @Override
    public boolean requiresInput() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContinuableStep that = (ContinuableStep) o;
        return Objects.equals(continuation, that.continuation);
    }

    @Override
    public final Step copy() {
        final Step copy = copySelf();
        if (continuation != null) copy.then(continuation.copy());
        return copy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(continuation);
    }

    @Override
    public String toString() {
        return "ContinuableStep{" +
                "continuation=" + continuation +
                '}';
    }
}
