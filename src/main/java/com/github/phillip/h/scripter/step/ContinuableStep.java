package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.Optional;

abstract class ContinuableStep implements Step {

    private Step continuation;

    abstract void doNext(final CommandSender sender);

    @Override
    public final Optional<Step> next(CommandSender sender) {
        doNext(sender);
        return Optional.ofNullable(continuation);
    }

    @Override
    public final Step then(Step next) {
        if (continuation != null) throw new IllegalStateException("Step already has continuation");
        if (next == null) throw new IllegalArgumentException("Next step cannot be null");
        continuation = next;
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContinuableStep that = (ContinuableStep) o;
        return Objects.equals(continuation, that.continuation);
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
