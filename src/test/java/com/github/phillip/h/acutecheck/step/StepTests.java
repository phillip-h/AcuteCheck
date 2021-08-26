package com.github.phillip.h.acutecheck.step;

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
        assertThrows(NullPointerException.class, () -> new EchoStep("Hello").then(null));
        assertThrows(IllegalStateException.class, () -> new EchoStep("Hello").input("foo"));

        final CommandSender commandSender = Mockito.mock(CommandSender.class);
        final ContinuableStep step = new EchoStep("Foo");
        final ContinuableStep next = new EchoStep("Bar");

        assertThat(step.then(next), is(next));
        assertThat(step.next(commandSender), is(Optional.of(next)));
        assertThat(next.next(commandSender), is(Optional.empty()));

        assertThat(step.requiresInput(), is(false));
        assertThat(next.requiresInput(), is(false));

        assertThrows(IllegalStateException.class, () -> step.then(new EchoStep("Baz")));
    }

    @Test
    @DisplayName("EchoStep should be correct")
    void echoStepShouldBeCorrect() {
        assertThrows(NullPointerException.class, () -> new EchoStep(null));

        final CommandSender commandSender = Mockito.mock(CommandSender.class);
        new EchoStep("hello, world!").doNext(commandSender);
        Mockito.verify(commandSender).sendMessage("hello, world!");
    }

    @Test
    @DisplayName("CommandStep should be correct")
    void commandStepShouldBeCorrect() {
        assertThrows(NullPointerException.class, () -> new CommandStep(null));
        assertThrows(IllegalArgumentException.class, () -> new CommandStep(""));
        assertThrows(IllegalArgumentException.class, () -> new CommandStep(" "));
        assertThrows(IllegalArgumentException.class, () -> new CommandStep("\n   \t \t \n "));

        // TODO it would be good to verify that the command was dispatched, but it's
        // TODO done statically..
    }

    @Test
    @DisplayName("AssertStep should be correct")
    void assertStepShouldBeCorrect() throws NoSuchMethodException {
        assertThrows(NullPointerException.class, () -> new AssertStep(null));
        assertThrows(IllegalArgumentException.class, () -> new AssertStep(null, null));
        assertThrows(IllegalArgumentException.class, () -> new AssertStep("com.github.phillip.h.acutecheck.step.StepTests", null));
        assertThrows(IllegalArgumentException.class, () -> new AssertStep(null, "assertionTest"));

        final AssertStep assertStep = new AssertStep("com.github.phillip.h.acutecheck.step.StepTests", "assertionTest");

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

    @Test
    @DisplayName("BranchStep should be correct")
    void branchStepShouldBeCorrect() {
        assertThrows(NullPointerException.class, () -> new BranchStep(null));

        final CommandSender commandSender = Mockito.mock(CommandSender.class);
        final BranchStep branchStep = new BranchStep("main");
        assertThrows(IllegalStateException.class, () -> branchStep.next(commandSender));

        assertThrows(IllegalArgumentException.class, () -> branchStep.input("other"));
        assertThrows(IllegalArgumentException.class, () -> branchStep.input("another"));
        assertThrows(IllegalStateException.class, () -> branchStep.next(commandSender));

        final Step otherStep = new EchoStep("other");
        final Step anotherStep = new EchoStep("another");
        branchStep.addBranch("other", otherStep);

        assertThrows(IllegalArgumentException.class, () -> branchStep.input("another"));
        branchStep.input("other");

        assertThat(branchStep.next(commandSender), is(Optional.of(otherStep)));

        branchStep.addBranch("another", anotherStep);
        branchStep.input("another");
        assertThat(branchStep.next(commandSender), is(Optional.of(anotherStep)));

        branchStep.input("main");
        assertThat(branchStep.next(commandSender), is(Optional.empty()));

        final Step mainStep = new EchoStep("main");
        assertThat(branchStep.then(mainStep), is(mainStep));
        assertThat(branchStep.next(commandSender), is(Optional.of(mainStep)));
    }

    @Test
    @DisplayName("copy() implementations should be correct")
    void copyImplementationsShouldBeCorrect() throws NoSuchMethodException {
        final AssertStep assertStep = new AssertStep(getClass().getDeclaredMethod("assertionTest", CommandSender.class));
        final EchoStep echoStep = new EchoStep("Hello, world!");
        final BranchStep branchStep = new BranchStep("main");
        branchStep.then(assertStep);
        branchStep.addBranch("other", echoStep);
        final CommandStep commandStep = new CommandStep("foo bar");
        final FailStep failStep = new FailStep("failed!");
        final NullStep nullStep = new NullStep();

        assertThat(assertStep.copy(), is(assertStep));
        assertThat(echoStep.copy(), is(echoStep));
        assertThat(branchStep.copy(), is(branchStep));
        assertThat(commandStep.copy(), is(commandStep));
        assertThat(failStep.copy(), is(failStep));
        assertThat(nullStep.copy(), is(nullStep));

        assertThat("Copy is not referentially equal", assertStep.copy() != assertStep);
        assertThat("Copy is not referentially equal", echoStep.copy() != echoStep);
        assertThat("Copy is not referentially equal", branchStep.copy() != branchStep);
        assertThat("Copy is not referentially equal", commandStep.copy() != commandStep);
        assertThat("Copy is not referentially equal", failStep.copy() != failStep);
        assertThat("Copy is not referentially equal", nullStep.copy() != nullStep);

        // Test on a ContinuableStep with continuation
        nullStep.then(failStep).then(commandStep).then(branchStep);
        assertThat(nullStep.copy(), is(nullStep));
    }
}
