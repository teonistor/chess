package io.github.teonistor.devschess.piece;

import io.github.teonistor.devschess.Board.Position;
import io.github.teonistor.devschess.Player;
import io.github.teonistor.devschess.move.Move;

import java.util.stream.Stream;

public interface Piece {
    Player getPlayer();
    Stream<Move> computePossibleMoves(Position from);
}
