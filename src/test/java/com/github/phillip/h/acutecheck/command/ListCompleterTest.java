package com.github.phillip.h.acutecheck.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListCompleterTest {

    @Test
    void constructorShouldValidateParameters() {
        assertThrows(NullPointerException.class, () -> new ListCompleter(null)) ;
    }

    @Test
    void completionShouldBeCorrect() {
        final CommandSender sender = Mockito.mock(CommandSender.class);
        final Command command = Mockito.mock(Command.class);

        final ListCompleter empty = new ListCompleter(Collections.emptySet());
        assertThat(empty.onTabComplete(sender, command, "list", new String[] {}), is(Collections.emptyList()));
        assertThat(empty.onTabComplete(sender, command, "list", new String[] {"arg"}), is(Collections.emptyList()));
        assertThat(empty.onTabComplete(sender, command, "list", new String[] {"arg", "foo"}), is(Collections.emptyList()));

        final ListCompleter full = new ListCompleter(new HashSet<>(Arrays.asList("foo", "arg", "foobar")));
        assertThat(full.onTabComplete(sender, command, "list", new String[] {}), is(Collections.emptyList()));
        assertThat(full.onTabComplete(sender, command, "list", new String[] {"ar"}), containsInAnyOrder("arg"));
        assertThat(full.onTabComplete(sender, command, "list", new String[] {"fo"}), containsInAnyOrder("foo", "foobar"));
        assertThat(full.onTabComplete(sender, command, "list", new String[] {"bar"}), is(Collections.emptyList()));
        assertThat(full.onTabComplete(sender, command, "list", new String[] {"arg", "fo"}), containsInAnyOrder("foo", "foobar"));
        assertThat(full.onTabComplete(sender, command, "list", new String[] {"arg", "bar"}), is(Collections.emptyList()));
    }
}
