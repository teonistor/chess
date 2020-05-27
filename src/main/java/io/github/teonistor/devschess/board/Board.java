package io.github.teonistor.devschess.board;

import io.github.teonistor.devschess.piece.Piece;
import java.util.EnumMap;
import java.util.Map;


public class Board {

    public static Map<Position, Piece> initialSetup() {
        // TODO Do when we have all pieces
        return new EnumMap<>(Position.class);
    }
}
