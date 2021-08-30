package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;

class RunCompleterTest {

    @Test
    void constructorShouldValidateParameters() {
        assertThrows(NullPointerException.class, () -> new RunCompleter(null, null));
        assertThrows(NullPointerException.class, () -> new RunCompleter(Mockito.mock(ListCompleter.class), null));
        assertThrows(NullPointerException.class, () -> new RunCompleter(null, new HashMap<>()));
    }

    @Test
    void onTabComplete() {
        final CommandSender sender = Mockito.mock(CommandSender.class);
        final Command command = Mockito.mock(Command.class);
        final ListCompleter listCompleter = Mockito.mock(ListCompleter.class);

        final RunCompleter empty = new RunCompleter(listCompleter, new HashMap<>());
        assertThat(empty.onTabComplete(sender, command, "run", new String[] {"run", "foo", "bar"}), is(Collections.emptyList()));
        assertThat(empty.onTabComplete(sender, command, "run", new String[] {"run", "foo", ""}), is(Collections.emptyList()));
        assertThat(empty.onTabComplete(sender, command, "run", new String[] {"run", "foo", "fo"}), is(Collections.emptyList()));
        assertThat(empty.onTabComplete(sender, command, "run", new String[] {"run", "arg", ""}), is(Collections.emptyList()));
        assertThat(empty.onTabComplete(sender, command, "run", new String[] {"run", "foobar", ""}), is(Collections.emptyList()));

        final Map<String, Set<String>> tests = new HashMap<>();
        tests.put("foo", new HashSet<>(Arrays.asList("foo1", "foo2", "bar")));
        tests.put("arg", new HashSet<>(Collections.singletonList("arg1")));
        tests.put("foobar", new HashSet<>());
        final RunCompleter full = new RunCompleter(listCompleter, tests);

        assertThat(full.onTabComplete(sender, command, "run", new String[] {}), is(Collections.emptyList()));
        assertThat(full.onTabComplete(sender, command, "run", new String[] {"run"}), is(Collections.emptyList()));

        full.onTabComplete(sender, command, "run", new String[] {"run", "fo"});
        Mockito.verify(listCompleter).onTabComplete(sender, command, "run", new String[] {"run", "fo"});

        assertThat(full.onTabComplete(sender, command, "run", new String[] {"run", "foo", "fake"}), is(Collections.emptyList()));
        assertThat(full.onTabComplete(sender, command, "run", new String[] {"run", "foo", ""}), containsInAnyOrder("foo1", "foo2", "bar"));
        assertThat(full.onTabComplete(sender, command, "run", new String[] {"run", "foo", "fo"}), containsInAnyOrder("foo1", "foo2"));
        assertThat(full.onTabComplete(sender, command, "run", new String[] {"run", "arg", ""}), containsInAnyOrder("arg1"));
        assertThat(full.onTabComplete(sender, command, "run", new String[] {"run", "foobar", ""}), is(Collections.emptyList()));
    }
}