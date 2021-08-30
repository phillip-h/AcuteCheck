package com.github.phillip.h.acutecheck.step;

import com.github.phillip.h.acutelib.util.Checks;
import com.github.phillip.h.acutelib.util.Pair;

import java.util.*;
import java.util.function.Supplier;

public class StepParserConfig {

    public static final String VERIFY_YES_BRANCH = "verify_yes";
    public static final String VERIFY_NO_BRANCH = "verify_no";

    public static final String WAIT_CONTINUE_BRANCH = "wait_continue";

    public static final String GENERIC_CANCEL_BRANCH = "generic_cancel";

    public static final String RECURSE_BRANCH = "RECURSE";

    private Supplier<List<Step>> verifySupplier = StepParserConfig::makeDefaultVerifyStep;
    private Supplier<List<Step>> waitSupplier = StepParserConfig::makeDefaultWaitStep;

    private Map<String, Pair<String, String>> assertAliases = new HashMap<>();

    private StepParserConfig() {}

    public static StepParserConfig defaultConfig() {
        return new StepParserConfig();
    }

    public static List<Step> makeDefaultVerifyStep() {
        final BranchStep verify = new BranchStep(VERIFY_YES_BRANCH);
        verify.addBranch(VERIFY_NO_BRANCH, new FailStep("Verify failed."));
        verify.addBranch(GENERIC_CANCEL_BRANCH, new NullStep());
        return Collections.singletonList(verify);
    }

    public static List<Step> makeDefaultWaitStep() {
        final BranchStep wait = new BranchStep(WAIT_CONTINUE_BRANCH);
        wait.addBranch(GENERIC_CANCEL_BRANCH, new NullStep());
        return Collections.singletonList(wait);
    }

    public StepParserConfig withVerifySupplier(final Supplier<List<Step>> verifySupplier) {
        this.verifySupplier = Objects.requireNonNull(verifySupplier, "null verifySupplier");
        return this;
    }

    public StepParserConfig withWaitSupplier(final Supplier<List<Step>> waitSupplier) {
        this.waitSupplier = Objects.requireNonNull(waitSupplier, "null waitSupplier");
        return this;
    }

    public StepParserConfig withAssertAliases(final Map<String, Pair<String, String>> assertAliases) {
        this.assertAliases = Objects.requireNonNull(assertAliases, "null assertAliases");
        return this;
    }

    public StepParserConfig withVerifyMessage(final String message) {
        Checks.requireNonEmpty(message, "empty message");
        final Supplier<List<Step>> currentSupplier = verifySupplier;
        this.verifySupplier = () -> {
            final List<Step> steps = new ArrayList<>();
            steps.add(new EchoStep(message));
            steps.addAll(currentSupplier.get());
            return steps;
        };
        return this;
    }

    public StepParserConfig withWaitMessage(final String message) {
        Checks.requireNonEmpty(message, "empty message");
        final Supplier<List<Step>> currentSupplier = waitSupplier;
        this.waitSupplier = () -> {
            final List<Step> steps = new ArrayList<>();
            steps.add(new EchoStep(message));
            steps.addAll(currentSupplier.get());
            return steps;
        };
        return this;
    }

    public Supplier<List<Step>> getVerifySupplier() {
        return verifySupplier;
    }

    public Supplier<List<Step>> getWaitSupplier() {
        return waitSupplier;
    }

    public Map<String, Pair<String, String>> getAssertAliases() {
        return assertAliases;
    }
}
