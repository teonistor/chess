package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import io.github.teonistor.chess.ctrl.PromotionGameInput;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import org.apache.commons.lang3.RandomUtils;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

        private @Mock Map<Position, Piece> board;
        private @Mock Traversable<Piece> capturedPieces;
        private final Tuple2<Position, Position> possibleMoveBlack = new Tuple2<>(randomPositions.next(), randomPositions.next());
        private final Tuple2<Position, Position> possibleMoveWhite = new Tuple2<>(randomPositions.next(), randomPositions.next());
        private final boolean promotionRequiredBlack = nextBoolean();
        private final boolean promotionRequiredWhite = nextBoolean();

        private ExternalGameState externalStateBlack;
        private ExternalGameState externalStateWhite;
        private ExternalGameState externalStateAll;

        @BeforeEach
        void refresh() {
            externalStateBlack = new ExternalGameState(board, capturedPieces, Stream.of(possibleMoveBlack), HashMap.empty(), false, promotionRequiredBlack);
            externalStateWhite = new ExternalGameState(board, capturedPieces, Stream.of(possibleMoveWhite), HashMap.empty(), promotionRequiredWhite, false);
            externalStateAll = externalStateWhite.combine(externalStateBlack);

            ctrl.refresh(board, capturedPieces, Stream.of(possibleMoveBlack), Stream.of(possibleMoveWhite), promotionRequiredBlack, promotionRequiredWhite);

            verify(ws).convertAndSend("/chess-ws/state-black", externalStateBlack);
            verify(ws).convertAndSend("/chess-ws/state-white", externalStateWhite);
            verify(ws).convertAndSend("/chess-ws/state-all", externalStateAll);
        }

        @Test
        void onSubscribeStateBlack() {
            assertThat(ctrl.onSubscribeStateBlack()).isEqualTo(externalStateBlack);
        }

        @Test
        void onSubscribeStateWhite() {
            assertThat(ctrl.onSubscribeStateWhite()).isEqualTo(externalStateWhite);
        }

        @Test
        void onSubscribeStateAll() {
            assertThat(ctrl.onSubscribeStateAll()).isEqualTo(externalStateAll);
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
    @CsvSource({"White,state-white",
                "Black,state-black",
                "any,state-all",
                "thing,state-all"})
    void stateChannel(final String player, final String channel) {
        assertThat(ctrl.stateChannel(player)).isEqualTo(channel);
    }

    @Test
    void saveGame() {
        final byte[] bytes = RandomUtils.nextBytes(10);
        doAnswer(invocation -> {
            invocation.getArgument(0, OutputStream.class).write(bytes);
            return null;
        }).when(controlLoop).saveGame(any());

        assertThat(ctrl.saveGame()).isEqualTo(ResponseEntity.ok()
                .header("Content-Disposition", "attachment;filename=savegame.json.gz")
                .header("Content-Type", "application/octet-stream")
                .body(bytes));
    }

    @Test
    void loadGame(final @Mock MultipartFile file, final @Mock InputStream inputStream) throws IOException {
        when(file.getInputStream()).thenReturn(inputStream);

        ctrl.loadGame(file);

        verify(controlLoop).loadGame(inputStream);
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
