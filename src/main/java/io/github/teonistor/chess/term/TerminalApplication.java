package io.github.teonistor.chess.term;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.Input;
import io.github.teonistor.chess.factory.Factory;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PACKAGE;

@AllArgsConstructor(access=PACKAGE, onConstructor_=@VisibleForTesting)
public class TerminalApplication implements Runnable {

    private final TerminalInput input;
    private final ControlLoop controlLoop;
    private final ScheduledExecutorService executorService;

    public TerminalApplication() {
        this(new TerminalInput(),
             new Factory().createControlLoop(new TerminalView()),
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
        final Option<Input> action = input.simpleInput();
        if (action.isEmpty())
            stop();
        else
            action.get().execute(controlLoop);
    }

    /**
     * Stop the application loop, preventing future iterations. The currently running iteration is not affected
     */
    public void stop() {
        executorService.shutdown();
    }
}
