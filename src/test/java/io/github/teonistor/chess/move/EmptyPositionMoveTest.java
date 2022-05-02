package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.C6;
import static io.github.teonistor.chess.board.Position.G1;
import static io.github.teonistor.chess.board.Position.H6;
import static org.assertj.core.api.Assertions.assertThat;


@MockitoSettings
class EmptyPositionMoveTest extends MoveTest {

    private @Mock Piece moving;
    private @Mock Piece obstacle;

    @Test
    void validateValid() {
        final Map<Position,Piece> board = HashMap.of(A1, moving);

        assertThat(new EmptyPositionMove(A1,C6).validate(stateWith(board))).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void validateInvalid(final Player player) {
        final Map<Position,Piece> board = HashMap.of(A1, moving, C6, obstacle);

        // One player stays fixed to prove that the move cannot be done irrespective of who owns the obstacle
        assertThat(new EmptyPositionMove(A1,C6).validate(stateWith(board))).isFalse();
    }

    @Test
    void execute() {
        final Map<Position,Piece> boardIn = HashMap.of(H6, moving);

        final Map<Position,Piece> boardOut = new EmptyPositionMove(H6, G1).executeSingleOutcome(stateWith(boardIn)).getBoard();

        assertThat(boardOut).containsExactly(new Tuple2<>(G1, moving));
    }
}