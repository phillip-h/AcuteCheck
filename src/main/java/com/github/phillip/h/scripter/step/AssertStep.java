package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

class AssertStep extends ContinuableStep {

    private final Method assertion;

    AssertStep(Method assertion) {
        if (assertion == null) throw new IllegalArgumentException("Null assertion");
        this.assertion = assertion;
    }

    AssertStep(final String className, final String declaredName) {
        try {
            assertion = getClass().getClassLoader()
                                  .loadClass(className)
                                  .getDeclaredMethod(declaredName, CommandSender.class);
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to load assertion method", e);
        }
    }

    @Override
    void doNext(CommandSender sender) {
        try {
            assertion.invoke(null, sender);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new StepException("Assertion failed", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AssertStep that = (AssertStep) o;
        return Objects.equals(assertion, that.assertion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), assertion);
    }
}
