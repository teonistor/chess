package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.B5;
import static io.github.teonistor.chess.board.Position.C6;
import static io.github.teonistor.chess.board.Position.E2;
import static io.github.teonistor.chess.board.Position.F1;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class CaptureIndependentMoveTest extends MoveTest {

    private @Mock Piece moving;
    private @Mock Piece victim;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateCapture() {
        final Map<Position,Piece> board = HashMap.of(A1, moving, C6, victim);
        when(victim.getPlayer()).thenReturn(Black);

        assertThat(new CaptureIndependentMove(A1,C6).validate(stateWith(board))).isTrue();
    }

    @Test
    void validateNonCapture() {
        final Map<Position,Piece> board = HashMap.of(A1, moving);

        assertThat(new CaptureIndependentMove(A1,C6).validate(stateWith(board))).isTrue();
    }

    @Test
    void validateInvalid() {
        final Map<Position,Piece> board = HashMap.of(A1, moving, C6, victim);
        when(victim.getPlayer()).thenReturn(White);

        assertThat(new CaptureIndependentMove(A1,C6).validate(stateWith(board))).isFalse();
    }

    @Test
    void executeCapture() {
        final Map<Position,Piece> boardIn = HashMap.of(B5, moving, E2, victim);

        Map<Position,Piece> boardOut = new CaptureIndependentMove(B5, E2).execute(boardIn, captureExpectedBoard, capturingReturnBoard);
        Piece piece = new CaptureIndependentMove(B5, E2).execute(boardIn, captureExpectedPiece, capturingReturnPiece);

        assertThat(boardOut).containsExactly(new Tuple2<>(E2, moving));
        assertThat(piece).isEqualTo(victim);
    }

    @Test
    void executeNonCapture() {
        final Map<Position,Piece> boardIn = HashMap.of(B5, moving);

        Map<Position,Piece> boardOut = new CaptureIndependentMove(B5, F1).execute(boardIn, nonCapturingReturnBoard, captureNotExpected);

        assertThat(boardOut).containsExactly(new Tuple2<>(F1, moving));
    }
}