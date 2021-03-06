package com.github.phillip.h.acutecheck.step;

import com.github.phillip.h.acutelib.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepParserTest {

    private static final Map<String, Pair<String, String>> aliasesMap = new HashMap<>();
    private static final Plugin plugin = Mockito.mock(Plugin.class);
    private static final StepParserConfig parserConfig = StepParserConfig.defaultConfig(plugin)
                                                                         .withAssertAliases(aliasesMap)
                                                                         .withWaitMessage("WAIT MESSAGE")
                                                                         .withVerifyMessage("VERIFY MESSAGE");

    @BeforeAll
    static void setup() {
        aliasesMap.put("alias1", new Pair<>("com.github.phillip.h.acutecheck.step.StepParserTest", "assertTest"));
        aliasesMap.put("alias2", new Pair<>("com.github.phillip.h.acutecheck.step.StepParserTest", "fakeMethod"));
    }

    @Test
    @DisplayName("StepParser should not accept null aliases map")
    void stepParserShouldNotAcceptNullAliasesMap() {
        assertThrows(NullPointerException.class, () -> new StepParser(null));
    }

    @Test
    @DisplayName("Step lists are parsed correctly")
    void stepListsShouldBeParsedCorrectly() throws NoSuchMethodException {
        final StepParser parser = new StepParser(parserConfig);
        assertThrows(NullPointerException.class, () -> parser.parseSteps(null));
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
                .then(new EchoStep("WAIT MESSAGE"))
                .then(StepParserConfig.makeDefaultWaitStep().get(0))
                .then(new EchoStep("Foo"))
                .then(new EchoStep(""))
                .then(new CommandStep("command arg1 arg2", plugin))
                .then(new AssertStep(getClass().getDeclaredMethod("assertTest", CommandSender.class)))
                .then(new EchoStep("VERIFY MESSAGE"))
                .then(StepParserConfig.makeDefaultVerifyStep().get(0))
                .then(new AssertStep(getClass().getDeclaredMethod("assertTest", CommandSender.class)));
        final List<String> stepsList = Arrays.asList(
                "echo Hello, world!",
                "wait",
                "echo Foo",
                "echo",
                "/command arg1 arg2",
                "assert alias1",
                "verify",
                "assertRaw com.github.phillip.h.acutecheck.step.StepParserTest assertTest"
        );
        assertThat(parser.parseSteps(stepsList), is(allSteps));
    }

    @Test
    @DisplayName("Steps are parsed correctly")
    void stepsShouldBeParsedCorrectly() throws NoSuchMethodException {
        final var parser = new StepParser(parserConfig);
        assertThrows(NullPointerException.class, () -> parser.parseStep(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep(" "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("  \t\t  \t\n"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("foo bar baz"));

        assertThat(parser.parseStep("echo"), contains(new EchoStep("")));
        assertThat(parser.parseStep("echo "), contains(new EchoStep("")));
        assertThat(parser.parseStep("echo hello, world!"), contains(new EchoStep("hello, world!")));

        assertThat(parser.parseStep("/foo"), contains(new CommandStep("foo", plugin)));
        assertThat(parser.parseStep("/foo bar baz"), contains(new CommandStep("foo bar baz", plugin)));

        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.acutecheck.step"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.acutecheck.step "));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.phillip.h.acutecheck.step fake"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assertRaw com.github.fake.SomeClass assertTest"));

        assertThat(parser.parseStep("assertRaw com.github.phillip.h.acutecheck.step.StepParserTest assertTest"),
                contains(new AssertStep(getClass().getDeclaredMethod("assertTest", CommandSender.class))));

        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assert"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assert "));
        // Alias that does not exist
        assertThrows(NullPointerException.class, () -> parser.parseStep("assert fakeAlias"));
        // Alias that does not resolve to a valid method
        assertThrows(IllegalArgumentException.class, () -> parser.parseStep("assert alias2"));

        // Proper alias
        assertThat(parser.parseStep("assert alias1"),
                   is(parser.parseStep("assertRaw com.github.phillip.h.acutecheck.step.StepParserTest assertTest")));

        assertThat(parser.parseStep("wait"), is(parserConfig.getWaitSupplier().get()));
        assertThat(parser.parseStep("verify"), is(parserConfig.getVerifySupplier().get()));

        assertThat(parser.parseStep("recurse"), contains(new BranchStep(StepParserConfig.RECURSE_BRANCH)));
    }

    static void assertTest(@SuppressWarnings("unused") final CommandSender sender) {}

    @Test
    void prechecksShouldRunCorrectly() {
        final int[] count = {0};
        final Consumer<List<String>> counter = l -> count[0] += l.size();
        final Consumer<List<String>> fail = l -> {throw new IllegalArgumentException("Fail!");};

        final StepParserConfig config = StepParserConfig.defaultConfig(plugin).addPrecheck(counter).addPrecheck(counter);
        final StepParser parser = new StepParser(config);
        parser.parseSteps(Arrays.asList("echo", "echo hello"));
        assertThat(count[0], is(4));
        count[0] = 0;

        config.addPrecheck(fail);
        assertThrows(IllegalArgumentException.class, () -> new StepParser(config).parseSteps(Collections.singletonList("echo this will fail...")));
        // it will fail, but the other prechecks should run first...
        assertThat(count[0], is(2));

    }
}
