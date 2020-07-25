package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.github.teonistor.chess.board.Position.B2;
import static io.github.teonistor.chess.board.Position.B3;
import static io.github.teonistor.chess.board.Position.B4;
import static io.github.teonistor.chess.board.Position.C2;
import static io.github.teonistor.chess.board.Position.C3;
import static io.github.teonistor.chess.board.Position.C4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class TrailLeavingMoveTest extends MoveTest {

    private @Mock Piece moving;
    private @Mock Piece obstacle;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void validateValid() {
        final Map<Position,Piece> board = HashMap.of(C2, moving);

        assertThat(new TrailLeavingMove(C2,C3,C4).validate(stateWith(board))).isTrue();
    }

    @Test
    void validateInvalidTargetBlocked() {
        final Map<Position,Piece> board = HashMap.of(C2, moving, C4, obstacle);
        when(obstacle.getPlayer()).thenReturn(Player.White);

        assertThat(new TrailLeavingMove(C2,C3,C4).validate(stateWith(board))).isFalse();
    }

    @Test
    void validateInvalidTrailBlocked() {
        final Map<Position,Piece> board = HashMap.of(C2, moving, C3, obstacle);
        when(obstacle.getPlayer()).thenReturn(Player.Black);

        assertThat(new TrailLeavingMove(C2,C3,C4).validate(stateWith(board))).isFalse();
    }

    @Test
    void execute() {
        final Map<Position,Piece> boardIn = HashMap.of(B2, moving);

        Map<Position,Piece> boardOut = new TrailLeavingMove(B2,B3,B4).execute(stateWith(boardIn)).getBoard();

        assertThat(boardOut).containsExactly(new Tuple2<>(B4, moving));
    }
}