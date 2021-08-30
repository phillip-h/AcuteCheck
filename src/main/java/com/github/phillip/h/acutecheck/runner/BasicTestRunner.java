package com.github.phillip.h.acutecheck.runner;

import com.github.phillip.h.acutecheck.step.InputStep;
import com.github.phillip.h.acutecheck.step.Step;
import com.github.phillip.h.acutecheck.step.StepException;
import com.github.phillip.h.acutecheck.step.StepRunner;
import com.github.phillip.h.acutelib.util.Checks;
import com.github.phillip.h.acutelib.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class BasicTestRunner extends BukkitRunnable {

    private final Queue<Pair<String, Step>> tests;
    private final Consumer<String> testBeginCallback;
    private final Consumer<Set<Pair<String, Boolean>>> completionCallback;

    private final Set<Pair<String, Boolean>> testResults = new HashSet<>();
    private final StepRunner stepRunner;

    private String runningTest;
    private Object queuedInput;

    private final ReentrantLock lock = new ReentrantLock();

    public BasicTestRunner(final Plugin plugin, Queue<Pair<String, Step>> tests, CommandSender executor, Consumer<String> testBeginCallback, Consumer<Set<Pair<String, Boolean>>> completionCallback) {
        this.tests = tests;
        this.stepRunner = new StepRunner(executor);
        this.testBeginCallback = testBeginCallback;
        this.completionCallback = completionCallback;
        runTaskTimer(plugin, 0, 1);
    }

    public void input(final Object input) {
        // TODO error if input already queued?
        lock.lock();
        queuedInput = input;
        lock.unlock();
    }

    public void recurse(final List<Step> steps) {
        Checks.requireNonEmpty(steps, "empty steps list");
        final List<Step> realSteps = new ArrayList<>();
        realSteps.add(new InputStep(stepRunner, "RECURSE"));
        realSteps.addAll(steps);
        // TODO need to defer this to the worker thread
        stepRunner.run(realSteps);
    }

    private void tryTest(final Runnable action) {
        try {
            lock.lock();
            action.run();
        } catch (StepException | IllegalArgumentException e) {
            testResults.add(new Pair<>(runningTest, false));
            runningTest = null;
        } catch (Exception e) {
            testResults.add(new Pair<>(runningTest, false));
            runningTest = null;
            // todo some real error
        } finally {
            lock.unlock();
        }
    }

    private void doInput(final Object input) {
        //tryTest(() -> stepRunner.input(input));
        try {
            lock.lock();
            stepRunner.input(input);
        } catch (StepException | IllegalArgumentException e) {
            testResults.add(new Pair<>(runningTest, false));
            runningTest = null;
            e.printStackTrace();
        } catch (Exception e) {
            testResults.add(new Pair<>(runningTest, false));
            runningTest = null;
            // todo some real error
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void doNextTest() {
        lock.lock();
        if (tests.isEmpty()) {
            cancel();
            completionCallback.accept(testResults);
        } else {
            final Pair<String, Step> nextTest = tests.poll();
            runningTest = nextTest.left();
            testBeginCallback.accept(runningTest);
            //tryTest(() -> stepRunner.run(nextTest.right()));
            try {
                //testBeginCallback.accept(runningTest);
                stepRunner.run(nextTest.right());
            } catch (StepException e) {
                // The test failed without needing input
                testResults.add(new Pair<>(runningTest, false));
                runningTest = null;
                e.printStackTrace();
            } catch (Exception e) {
                testResults.add(new Pair<>(runningTest, false));
                runningTest = null;
                // todo some real error
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    @Override
    public void run() {
        if (lock.tryLock()) {
            if (!stepRunner.executing()) {
                if (runningTest != null) {
                    // The test completed normally following receiving input
                    testResults.add(new Pair<>(runningTest, true));
                    runningTest = null;
                }
                doNextTest();
            } else if (queuedInput != null) {
                if (stepRunner.requiresInput()) {
                    doInput(queuedInput);
                } else {
                    // TODO error we have input but don't want it...
                }
                queuedInput = null;
            }
            lock.unlock();
        } else {
            // todo warn?
        }
    }
}
