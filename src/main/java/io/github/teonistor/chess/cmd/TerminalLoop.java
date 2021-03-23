package io.github.teonistor.chess.cmd;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.inter.DefinitelyInput;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;
import lombok.AllArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PACKAGE;

@AllArgsConstructor(access=PACKAGE, onConstructor_=@VisibleForTesting)
public class TerminalLoop implements Runnable {

    private final DefinitelyInput input;
    private final View view;
    private final ControlLoop controlLoop;
    private final ScheduledExecutorService executorService;

    public TerminalLoop(ControlLoop controlLoop, InputActionProvider inputActionProvider) {
        this(new TerminalInput(inputActionProvider),
             new TerminalView(),
             controlLoop,
             newScheduledThreadPool(2));
    }

    public void start() {
        executorService.scheduleWithFixedDelay(this, 1, 1, MILLISECONDS);
    }

    public void run() {
        final InputAction action = input.simpleInput();
        if (action.isExit())
            stop();
        else
            controlLoop.onInput(action);
    }

    public void stop() {
        executorService.shutdown();
    }
}
