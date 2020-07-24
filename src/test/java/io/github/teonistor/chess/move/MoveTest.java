package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.teonistor.chess.core.Player.White;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.fail;

public abstract class MoveTest {
    protected final Function<Map<Position,Piece>,Map<Position,Piece>> nonCapturingReturnBoard = identity();
    protected final Function<Map<Position,Piece>,Map<Position,Piece>> captureExpectedBoard = ignore -> fail("Capture expected");
    protected final Function<Map<Position,Piece>,Piece> captureExpectedPiece = ignore -> fail("Capture expected");
    protected final BiFunction<Map<Position,Piece>,Piece,Map<Position,Piece>> capturingReturnBoard = (board,piece) -> board;
    protected final BiFunction<Map<Position,Piece>,Piece,Piece> capturingReturnPiece = (board,piece) -> piece;
    protected final BiFunction<Map<Position,Piece>,Piece,Map<Position,Piece>> captureNotExpected = (ignore1,ignore2) -> fail("Capture not expected");

    protected GameState stateWith(Map<Position, Piece> board) {
        return stateWith(board, White);
    }

    protected GameState stateWith(Map<Position, Piece> board, Player player) {
        return new GameState(board, player, HashSet.empty());
    }
}
