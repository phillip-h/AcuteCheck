package com.github.phillip.h.acutecheck.step;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepParserConfigTest {

    @Test
    @DisplayName("Default config should be correct")
    void defaultConfigCorrect() {
        assertThat(StepParserConfig.defaultConfig().getVerifySupplier().get(), is(StepParserConfig.makeDefaultVerifyStep()));
        assertThat(StepParserConfig.defaultConfig().getWaitSupplier().get(), is(StepParserConfig.makeDefaultWaitStep()));
        assertThat(StepParserConfig.defaultConfig().getAssertAliases().size(), is(0));
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
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig().withVerifySupplier(null));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig().withWaitSupplier(null));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig().withAssertAliases(null));
    }

    @Test
    @DisplayName("Message methods should be correct")
    void messageMethodsShouldBeCorrect() {
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig().withWaitMessage(null));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig().withWaitMessage(""));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig().withWaitMessage("  \t \n"));
        assertThrows(NullPointerException.class, () -> StepParserConfig.defaultConfig().withVerifyMessage(null));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig().withVerifyMessage(""));
        assertThrows(IllegalArgumentException.class, () -> StepParserConfig.defaultConfig().withVerifyMessage("  \t \n"));

        final StepParserConfig config = StepParserConfig.defaultConfig()
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