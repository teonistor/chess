package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.Move;

import java.util.stream.Stream;

public interface Piece {
    Player getPlayer();
    Stream<Move> computePossibleMoves(Position from);
}
