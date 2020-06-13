package io.github.teonistor.devschess.move;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;
import static io.github.teonistor.devschess.board.Position.A1;
import static io.github.teonistor.devschess.board.Position.B5;
import static io.github.teonistor.devschess.board.Position.C6;
import static io.github.teonistor.devschess.board.Position.E2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class CapturingMoveTest extends MoveTest {

    private @Mock Piece moving;
    private @Mock Piece victim;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateValid() {
        final Map<Position,Piece> board = HashMap.of(A1, moving, C6, victim);
        when(victim.getPlayer()).thenReturn(Black);

        assertThat(new CapturingMove(A1,C6,White).validate(board)).isTrue();
    }

    @Test
    void validateInvalidNotOccupied() {
        final Map<Position,Piece> board = HashMap.of(A1, moving);

        assertThat(new CapturingMove(A1,C6,White).validate(board)).isFalse();
    }

    @Test
    void validateInvalidNotOpponent() {
        final Map<Position,Piece> board = HashMap.of(A1, moving, C6, victim);
        when(victim.getPlayer()).thenReturn(White);

        assertThat(new CapturingMove(A1,C6,White).validate(board)).isFalse();
    }

    @Test
    void execute() {
        final Map<Position,Piece> boardIn = HashMap.of(B5, moving, E2, victim);

        Map<Position,Piece> boardOut = new CapturingMove(B5, E2, Black).execute(boardIn, captureExpectedBoard, capturingReturnBoard);
        Piece piece = new CapturingMove(B5, E2, Black).execute(boardIn, captureExpectedPiece, capturingReturnPiece);

        assertThat(boardOut).containsExactly(new Tuple2<>(E2, moving));
        assertThat(piece).isEqualTo(victim);
    }
}