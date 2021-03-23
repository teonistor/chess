package io.github.teonistor.chess.cmd;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.core.Factory;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.inter.DefinitelyInput;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import lombok.AllArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PACKAGE;

@AllArgsConstructor(access=PACKAGE, onConstructor_=@VisibleForTesting)
public class TerminalLoop implements Runnable {

    private final DefinitelyInput input;
    private final ControlLoop controlLoop;
    private final ScheduledExecutorService executorService;

    public TerminalLoop() {
        this(new Factory());
    }

    private TerminalLoop(final Factory factory) {
        this(new TerminalInput(factory.getInputActionProvider()),
             factory.createControlLoop(new TerminalView()),
             newScheduledThreadPool(1));
    }

    /**
     * Schedule this application to be run in a loop
     */
    public void start() {
        executorService.scheduleWithFixedDelay(this, 1, 1, MILLISECONDS);
    }

    /**
     * Run one iteration. You probably don't want to call this manually
     */
    public void run() {
        final InputAction action = input.simpleInput();
        if (action.isExit())
            stop();
        else
            controlLoop.onInput(action);
    }

    /**
     * Stop the application loop, preventing future iterations. The currently running iteration is not affected
     */
    public void stop() {
        executorService.shutdown();
    }
}
