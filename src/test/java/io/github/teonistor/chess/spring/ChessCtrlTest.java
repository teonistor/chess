package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import io.github.teonistor.chess.ctrl.PromotionGameInput;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class ChessCtrlTest implements RandomPositionsTestMixin {

    private @Mock SimpMessagingTemplate ws;
    private @Mock ControlLoop controlLoop;

    private @InjectMocks ChessCtrl ctrl;

    @Nested
    class CachedFields {

        private @Mock Map<Position, Piece> lastBoard;
        private @Mock Traversable<Piece> lastCapturedPieces;
        private final Tuple2<Position, Position> possibleMoveWhite = new Tuple2<>(randomPositions.next(), randomPositions.next());
        private final Tuple2<Position, Position> possibleMoveBlack = new Tuple2<>(randomPositions.next(), randomPositions.next());

        @BeforeEach
        void refresh() {
            ctrl.refresh(lastBoard, lastCapturedPieces, Stream.of(possibleMoveBlack), Stream.of(possibleMoveWhite));

            verify(ws).convertAndSend("/chess-ws/board", lastBoard);
            verify(ws).convertAndSend("/chess-ws/captured-pieces", lastCapturedPieces);
            verify(ws).convertAndSend("/chess-ws/moves-white", Stream.of(possibleMoveWhite));
            verify(ws).convertAndSend("/chess-ws/moves-black", Stream.of(possibleMoveBlack));
            verify(ws).convertAndSend("/chess-ws/moves-all", Stream.of(possibleMoveBlack, possibleMoveWhite));
        }

        @Test
        void onSubscribeBoard() {
            assertThat(ctrl.onSubscribeBoard()).isEqualTo(lastBoard);
        }

        @Test
        void onSubscribeCapturedPieces() {
            assertThat(ctrl.onSubscribeCapturedPieces()).isEqualTo(lastCapturedPieces);
        }

        @Test
        void onSubscribeMovesBlack() {
            assertThat(ctrl.onSubscribeMovesBlack()).isEqualTo(Stream.of(possibleMoveBlack));
        }

        @Test
        void onSubscribeMovesWhite() {
            assertThat(ctrl.onSubscribeMovesWhite()).isEqualTo(Stream.of(possibleMoveWhite));
        }

        @Test
        void onSubscribeMovesAll() {
            assertThat(ctrl.onSubscribeMovesAll()).isEqualTo(Stream.of(possibleMoveBlack, possibleMoveWhite));
        }
    }

    @Test
    void announce() {
        final String payload = randomAlphabetic(10);
        ctrl.announce(payload);

        verify(ws).convertAndSend("/chess-ws/announcements", payload);
    }

    @Test
    void onMove() {
        final Position from = randomPositions.next();
        final Position to = randomPositions.next();
        ctrl.onMove("irrelevant for now", new Tuple2<>(from, to));
        verify(controlLoop).gameInput(new NormalGameInput(from, to));
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void onPromote(final Player player, final @Mock Piece piece) {
        when(piece.getPlayer()).thenReturn(player);
        ctrl.onPromote(player.name(), piece);
        verify(controlLoop).gameInput(new PromotionGameInput(piece));
    }

    @Test
    void onPromoteWhileHotseat(final @Mock Piece piece) {
        ctrl.onPromote("Hotseat", piece);
        verify(controlLoop).gameInput(new PromotionGameInput(piece));
    }

    @Test
    void onPromoteFailsOnWrongPlayer(final @Mock Piece piece) {
        when(piece.getPlayer()).thenReturn(Player.White);
        assertThatIllegalArgumentException().isThrownBy(() -> ctrl.onPromote("Black", piece));
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
