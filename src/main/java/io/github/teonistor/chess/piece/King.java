package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.move.Move;

import java.util.stream.Stream;

public class King implements Piece {
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public Stream<Move> computePossibleMoves(Position from) {
        return null;
    }
}
