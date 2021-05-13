package io.github.teonistor.chess.term;

import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.Input;
import io.vavr.control.Option;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@MockitoSettings
class TerminalApplicationTest {

    private @Mock TerminalInput terminalInput;
    private @Mock Input input;
    private @Mock ControlLoop loop;
    private @Mock ScheduledExecutorService executorService;

    @Test
    void constructor() {
        final TerminalApplication app = new TerminalApplication();
        assertThat(getField(app, "input")).isInstanceOf(TerminalInput.class);
        assertThat(getField(app, "controlLoop")).isInstanceOf(ControlLoop.class);
        assertThat(getField(app, "executorService")).isInstanceOf(ScheduledExecutorService.class);
    }

    @Test
    void start() {
        final TerminalApplication app = new TerminalApplication(terminalInput, loop, executorService);

        app.start();

        verify(executorService).scheduleWithFixedDelay(app, 1, 1, MILLISECONDS);
    }

    @Test
    void run() {
        final TerminalApplication app = new TerminalApplication(terminalInput, loop, executorService);
        when(terminalInput.simpleInput()).thenReturn(Option.some(input));

        app.run();

        verify(input).execute(loop);
    }

    @Test
    void runEncountersException() {
        final TerminalApplication app = new TerminalApplication(terminalInput, loop, executorService);
        when(terminalInput.simpleInput()).thenReturn(Option.some(input));
        doThrow(RuntimeException.class).when(input).execute(loop);

        app.run();
    }

    @Test
    void exit() {
        final TerminalApplication app = new TerminalApplication(terminalInput, loop, executorService);
        when(terminalInput.simpleInput()).thenReturn(Option.none());

        app.run();

        verify(executorService).shutdown();
    }

    @Test
    void stop() {
        final TerminalApplication app = new TerminalApplication(terminalInput, loop, executorService);

        app.stop();

        verify(executorService).shutdown();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(terminalInput, loop, executorService);
    }
}