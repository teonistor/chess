package io.github.teonistor.chess.term;

import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.factory.Factory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@MockitoSettings
class TerminalApplicationTest {

    private @Mock Factory factory;
    private @Mock TerminalInput input;
    private @Mock ControlLoop controlLoop;
    private @Mock ScheduledExecutorService executorService;

    @Test
    void constructor() {
        final TerminalApplication loop = new TerminalApplication();
        assertThat(getField(loop, "input")).isInstanceOf(TerminalInput.class);
        assertThat(getField(loop, "controlLoop")).isInstanceOf(ControlLoop.class);
        assertThat(getField(loop, "executorService")).isInstanceOf(ScheduledExecutorService.class);
    }

    @Test
    void start() {
        final TerminalApplication loop = new TerminalApplication(input, controlLoop, executorService);

        loop.start();

        verify(executorService).scheduleWithFixedDelay(loop, 1, 1, MILLISECONDS);
    }

    @Test
    void run() {
        final TerminalApplication loop = new TerminalApplication(input, controlLoop, executorService);
        final InputAction action = new InputAction() {};
        when(input.simpleInput()).thenReturn(action);

        loop.run();

        verify(controlLoop).onInput(action);
    }

    @Test
    void exit() {
        final TerminalApplication loop = new TerminalApplication(input, controlLoop, executorService);
        final InputAction action = new InputAction() {
            @Override
            public boolean isExit() {
                return true;
            }
        };
        when(input.simpleInput()).thenReturn(action);

        loop.run();

        verify(executorService).shutdown();
    }

    @Test
    void stop() {
        final TerminalApplication loop = new TerminalApplication(input, controlLoop, executorService);

        loop.stop();

        verify(executorService).shutdown();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(factory, input, controlLoop, executorService);
    }
}