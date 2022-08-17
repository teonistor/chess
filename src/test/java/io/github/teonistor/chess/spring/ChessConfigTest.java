package io.github.teonistor.chess.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.factory.Factory;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;

@MockitoSettings
class ChessConfigTest {

    @Spy
    private final ChessConfig chessConfig = new ChessConfig();

    @Test
    void factory() {
        assertThat(chessConfig.factory()).isInstanceOf(Factory.class);
    }

    @Test
    void configureObjectMapper(final @Mock Factory factory, final @Mock ObjectMapper objectMapper) throws Exception {
        willReturn(factory).given(chessConfig).factory();
        willDoNothing().given(factory).configureObjectMapper(objectMapper);

        chessConfig.configureObjectMapper(objectMapper).afterPropertiesSet();
    }

    @Test
    void controlLoop(final @Mock Factory factory, final @Mock ChessCtrl chessCtrl, final @Mock ControlLoop controlLoop) {
        willReturn(factory).given(chessConfig).factory();
        willReturn(controlLoop).given(factory).createControlLoop(chessCtrl);

        assertThat(chessConfig.controlLoop(chessCtrl)).isSameAs(controlLoop);
    }
}