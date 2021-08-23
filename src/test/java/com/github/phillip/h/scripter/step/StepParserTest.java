package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepParserTest {

    @Test
    @DisplayName("Step lists are parsed correctly")
    void stepListsShouldBeParsedCorrectly() throws NoSuchMethodException {
        final StepParser parser = new StepParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parseSteps(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parseSteps(Collections.singletonList("fake step")));
        assertThrows(IllegalArgumentException.class, () -> parser.parseSteps(Arrays.asList("echo foo bar", "not a step")));

        assertThat(parser.parseSteps(Collections.emptyList()), is(new NullStep()));

        final Step oneStep = new NullStep();
        oneStep.then(new EchoStep("first"));
        assertThat(parser.parseSteps(Collections.singletonList("echo first")), is(oneStep));

        final Step threeSteps = new NullStep();
        threeSteps.then(new EchoStep("first")).then(new EchoStep("second")).then(new EchoStep("third"));
        assertThat(parser.parseSteps(Arrays.asList("echo first", "echo second", "echo third")), is(threeSteps));

        final Step allSteps = new NullStep();
        allSteps.then(new EchoStep("Hello, world!"))
                .then(parser.makeWaitMessage())
                .then(parser.makeWaitStep())
                .then(new EchoStep("Foo"))
                .then(new EchoStep(""))
                .then(new CommandStep("command arg1 arg2"))
                .then(parser.makeVerifyMessage())
                .then(parser.makeVerifyStep())
                .then(new AssertStep(getClass().getDeclaredMethod("assertTest", CommandSender.class)));
        final List<String> stepsList = Arrays.asList(
                "echo Hello, world!",
                "wait",
                "echo Foo",
                "echo",
                "/command arg1 arg2",
                "verify",
                "assertRaw com.github.phillip.h.scripter.step.StepParserTest assertTest"
        );
        assertThat(parser.parseSteps(stepsList), is(allSteps));
    }

    @Test
    @DisplayName("Steps are parsed correctly")
    void stepsShouldBeParsedCorrectly() throws NoSuchMethodException {
        final var parser = new StepParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(" "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("  \t\t  \t\n"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("foo bar baz"));

        assertThat(parser.parseStep("echo"), contains(new EchoStep("")));
        assertThat(parser.parseStep("echo "), contains(new EchoStep("")));
        assertThat(parser.parseStep("echo hello, world!"), contains(new EchoStep("hello, world!")));

        assertThat(parser.parseStep("/foo"), contains(new CommandStep("foo")));
        assertThat(parser.parseStep("/foo bar baz"), contains(new CommandStep("foo bar baz")));

        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.scripter.step"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.scripter.step "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.scripter.step fake"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.fake.SomeClass assertTest"));

        assertThat(parser.parseStep("assertRaw com.github.phillip.h.scripter.step.StepParserTest assertTest"),
                contains(new AssertStep(getClass().getDeclaredMethod("assertTest", CommandSender.class))));

        assertThat(parser.parseStep("wait"), contains(parser.makeWaitMessage(), parser.makeWaitStep()));
        assertThat(parser.parseStep("verify"), contains(parser.makeVerifyMessage(), parser.makeVerifyStep()));
    }

    static void assertTest(@SuppressWarnings("unused") final CommandSender sender) {}
}
