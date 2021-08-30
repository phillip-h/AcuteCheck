package com.github.phillip.h.acutecheck.step;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepParserConfigTest {

    @Test
    @DisplayName("Default config should be correct")
    void defaultConfigCorrect() {
        final Plugin plugin = Mockito.mock(Plugin.class);
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(null));
        assertThat(StepParserConfig.defaultConfig(plugin).getVerifySupplier().get(), is(StepParserConfig.makeDefaultVerifyStep()));
        assertThat(StepParserConfig.defaultConfig(plugin).getWaitSupplier().get(), is(StepParserConfig.makeDefaultWaitStep()));
        assertThat(StepParserConfig.defaultConfig(plugin).getAssertAliases().size(), is(0));
    }

    @Test
    @DisplayName("makeDefaultVerifyStep() should be correct")
    void makeDefaultVerifyStep() {
        final BranchStep actual = (BranchStep) StepParserConfig.makeDefaultVerifyStep().get(0);
        assertThat(actual.getPrimaryBranch(), is(StepParserConfig.VERIFY_YES_BRANCH));
        // No Yes branch here because it's not set
        assertThat(actual.getBranches().keySet(), containsInAnyOrder(
                StepParserConfig.VERIFY_NO_BRANCH,
                StepParserConfig.GENERIC_CANCEL_BRANCH
        ));
    }

    @Test
    @DisplayName("makeDefaultWaitStep() should be correct")
    void makeDefaultWaitStepCorrect() {
        final BranchStep actual = (BranchStep) StepParserConfig.makeDefaultWaitStep().get(0);
        assertThat(actual.getPrimaryBranch(), is(StepParserConfig.WAIT_CONTINUE_BRANCH));
        // No Continue branch here because it's not set
        assertThat(actual.getBranches().keySet(), containsInAnyOrder(
                StepParserConfig.GENERIC_CANCEL_BRANCH
        ));
    }

    @Test
    @DisplayName("Object should not accept null")
    void objectShouldNotAcceptNull() {
        final Plugin plugin = Mockito.mock(Plugin.class);
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(plugin).withVerifySupplier(null));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(plugin).withWaitSupplier(null));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(plugin).withAssertAliases(null));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(plugin).addPrecheck(null));
    }

    @Test
    @DisplayName("Message methods should be correct")
    void messageMethodsShouldBeCorrect() {
        final Plugin plugin = Mockito.mock(Plugin.class);
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(plugin).withWaitMessage(null));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig(plugin).withWaitMessage(""));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig(plugin).withWaitMessage("  \t \n"));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig(plugin).withVerifyMessage(null));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig(plugin).withVerifyMessage(""));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig(plugin).withVerifyMessage("  \t \n"));

        final StepParserConfig config = StepParserConfig.defaultConfig(plugin)
                                                        .withVerifyMessage("MESSAGE FOR VERIFY")
                                                        .withWaitMessage("MESSAGE FOR WAIT");
        assertThat(config.getWaitSupplier().get(), contains(
                new EchoStep("MESSAGE FOR WAIT"),
                StepParserConfig.makeDefaultWaitStep().get(0)
        ));
        assertThat(config.getVerifySupplier().get(), contains(
                new EchoStep("MESSAGE FOR VERIFY"),
                StepParserConfig.makeDefaultVerifyStep().get(0)
        ));
    }

    @Test
    void withWaitMessage() {
    }
}