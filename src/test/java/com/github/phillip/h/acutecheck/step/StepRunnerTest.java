package com.github.phillip.h.acutecheck.step;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StepRunnerTest {

    @Test
    @DisplayName("StepRunner runs correctly")
    void stepRunnerRunsCorrect() {
        final CommandSender sender = Mockito.mock(CommandSender.class);

        assertThrows(NullPointerException.class, () -> new StepRunner(null));
        assertThrows(NullPointerException.class, () -> new StepRunner(sender).run((Step) null));
        assertThrows(NullPointerException.class, () -> new StepRunner(sender).run((List<Step>) null));
        assertThrows(NoSuchElementException.class, () -> new StepRunner(sender).run(Collections.emptyList()));

        final StepRunner runner = new StepRunner(sender);
        assertThat("Runner should not be executing at first", !runner.executing());

        assertThrows(IllegalStateException.class, runner::requiresInput);
        assertThrows(IllegalStateException.class, () -> runner.input("input"));

        runner.run(new EchoStep("Hello, world!"));
        Mockito.verify(sender).sendMessage("Hello, world!");
        Mockito.reset(sender);

        assertThat("Runner should not be executing after execute", !runner.executing());

        final Step step1 = new EchoStep("foo");
        step1.then(new EchoStep("bar"));
        runner.run(step1);
        final InOrder inOrder = Mockito.inOrder(sender);
        inOrder.verify(sender).sendMessage("foo");
        inOrder.verify(sender).sendMessage("bar");
        Mockito.reset(sender);

        final Step step2 = new EchoStep("Recurse!");
        step2.then(new BranchStep("continue")).then(new EchoStep("done!"));
        runner.run(step2);
        inOrder.verify(sender).sendMessage("Recurse!");

        assertThat("Runner should wait for input", runner.requiresInput());
        assertThrows(IllegalArgumentException.class, () -> runner.input("foo"));
        assertThat("Runner should be executing while waiting for input", runner.executing());

        runner.run(step1);
        inOrder.verify(sender).sendMessage("foo");
        inOrder.verify(sender).sendMessage("bar");

        assertThat("Runner should still wait for input", runner.requiresInput());
        assertThat("Runner should be executing while waiting for input", runner.executing());
        runner.input("continue");
        inOrder.verify(sender).sendMessage("done!");
        Mockito.reset(sender);

        runner.run(Arrays.asList(new EchoStep("first"), new EchoStep("second")));
        inOrder.verify(sender).sendMessage("second");
        inOrder.verify(sender).sendMessage("first");
        Mockito.reset(sender);
    }

}
