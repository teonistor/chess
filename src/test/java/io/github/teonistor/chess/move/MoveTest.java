package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import static io.github.teonistor.chess.core.Player.White;

public abstract class MoveTest {

    protected GameState stateWith(Map<Position, Piece> board) {
        return stateWith(board, White);
    }

    protected GameState stateWith(Map<Position, Piece> board, Player player) {
        return new GameState(board, player, List.empty(), null);
    }
}
