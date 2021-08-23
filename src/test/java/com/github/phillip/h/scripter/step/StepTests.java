package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StepTests {

    @Test
    @DisplayName("ContinuableStep should be correct")
    void continuableStepShouldBeCorrect() {
        assertThrows(IllegalArgumentException.class, () -> new EchoStep("Hello").then(null));

        final CommandSender commandSender = Mockito.mock(CommandSender.class);
        final ContinuableStep step = new EchoStep("Foo");
        final ContinuableStep next = new EchoStep("Bar");

        assertThat(step.then(next), is(next));
        assertThat(step.next(commandSender), is(Optional.of(next)));
        assertThat(next.next(commandSender), is(Optional.empty()));

        assertThrows(IllegalStateException.class, () -> step.then(new EchoStep("Baz")));
    }

    @Test
    @DisplayName("EchoStep should be correct")
    void echoStepShouldBeCorrect() {
        assertThrows(IllegalArgumentException.class, () -> new EchoStep(null));

        final CommandSender commandSender = Mockito.mock(CommandSender.class);
        new EchoStep("hello, world!").doNext(commandSender);
        Mockito.verify(commandSender).sendMessage("hello, world!");
    }

    @Test
    @DisplayName("CommandStep should be correct")
    void commandStepShouldBeCorrect() {
        assertThrows(IllegalArgumentException.class, () -> new CommandStep(null));
        assertThrows(IllegalArgumentException.class, () -> new CommandStep(""));
        assertThrows(IllegalArgumentException.class, () -> new CommandStep(" "));
        assertThrows(IllegalArgumentException.class, () -> new CommandStep("\n   \t \t \n "));

        // TODO it would be good to verify that the command was dispatched, but it's
        // TODO done statically..
    }

    @Test
    @DisplayName("AssertStep should be correct")
    void assertStepShouldBeCorrect() throws NoSuchMethodException {
        assertThrows(IllegalArgumentException.class, () -> new AssertStep(null));
        assertThrows(IllegalArgumentException.class, () -> new AssertStep(null, null));
        assertThrows(IllegalArgumentException.class, () -> new AssertStep("com.github.phillip.h.scripter.step.StepTests", null));
        assertThrows(IllegalArgumentException.class, () -> new AssertStep(null, "assertionTest"));

        final AssertStep assertStep = new AssertStep("com.github.phillip.h.scripter.step.StepTests", "assertionTest");

        final Method method = getClass().getDeclaredMethod("assertionTest", CommandSender.class);
        final AssertStep fromMethod = new AssertStep(method);

        assertThat(assertStep, is(fromMethod));

        final CommandSender commandSender = Mockito.mock(CommandSender.class);
        assertStep.doNext(commandSender);
        Mockito.verify(commandSender).sendMessage("Asserted!");
    }

    static void assertionTest(final CommandSender sender) {
        sender.sendMessage("Asserted!");
    }

    @Test
    @DisplayName("FailStep should be correct")
    void failStepShouldBeCorrect() {
        assertThrows(StepException.class, () -> new FailStep("Failure").doNext(null));
    }
}
