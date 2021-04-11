package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings
class ChessCtrlTest implements RandomPositionsTestMixin {

    private @Mock SimpMessagingTemplate ws;
    private @Mock ControlLoop controlLoop;

    private @InjectMocks ChessCtrl ctrl;

    @BeforeEach
    void setUp() {

    }


    @Test
    void refresh() {

    }

    @Test
    void announce() {
        final String payload = randomAlphabetic(10);
        ctrl.announce(payload);

        verify(ws).convertAndSend("/chess-ws/announcements", payload);
    }

    @Test
    void onSubscribeBoard() {
    }

    @Test
    void onMove() {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        ctrl.onMove("irrelevant for now", new Tuple2<>(from, to));
        verify(controlLoop).gameInput(new NormalGameInput(from, to));
    }

    @ParameterizedTest
    @CsvSource({"White,moves-white",
                "Black,moves-black",
                "any,moves-all",
                "thing,moves-all"})
    void movesChannel(String player, String channel) {
        assertThat(ctrl.movesChannel(player)).isEqualTo(channel);
    }

    @Test
    void newStandardGame() {
        ctrl.newStandardGame();
        verify(controlLoop).newStandardGame();
    }

    @Test
    void newParallelGame() {
        ctrl.newParallelGame();
        verify(controlLoop).newParallelGame();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(ws, controlLoop);
    }
}
