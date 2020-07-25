package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.UnmovedKing;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.board.Position.D2;
import static io.github.teonistor.chess.board.Position.E1;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


class KingsFirstMoveTest extends MoveTest {

    @Test
    void execute() {
        final Map<Position, Piece> boardIn = HashMap.of(E1, new UnmovedKing(White, mock(UnderAttackRule.class)));

        final Map<Position, Piece> boardOut = new KingsFirstMove(E1, D2).execute(stateWith(boardIn)).getBoard();

        assertThat(boardOut).hasSize(1);
        assertThat(boardOut.get(D2).get()).isExactlyInstanceOf(King.class);
    }
}