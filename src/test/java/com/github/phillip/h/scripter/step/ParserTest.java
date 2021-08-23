package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    @Test
    @DisplayName("Step lists are parsed correctly")
    void stepListsShouldBeParsedCorrectly() {
        final Parser parser = new Parser();
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
    }

    @Test
    @DisplayName("Steps are parsed correctly")
    void stepsShouldBeParsedCorrectly() throws NoSuchMethodException {
        final var parser = new Parser();
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(" "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("  \t\t  \t\n"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("foo bar baz"));

        assertThat(parser.parseStep("echo"), is(new EchoStep("")));
        assertThat(parser.parseStep("echo "), is(new EchoStep("")));
        assertThat(parser.parseStep("echo hello, world!"), is(new EchoStep("hello, world!")));

        assertThat(parser.parseStep("/foo"), is(new CommandStep("foo")));
        assertThat(parser.parseStep("/foo bar baz"), is(new CommandStep("foo bar baz")));

        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.scripter.step"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.scripter.step "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.scripter.step fake"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.fake.SomeClass assertTest"));

        assertThat(parser.parseStep("assertRaw com.github.phillip.h.scripter.step.ParserTest assertTest"),
                is(new AssertStep(getClass().getDeclaredMethod("assertTest", CommandSender.class))));
    }

    static void assertTest(@SuppressWarnings("unused") final CommandSender sender) {}
}
