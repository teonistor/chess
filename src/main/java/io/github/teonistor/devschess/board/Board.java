package io.github.teonistor.devschess.board;

import io.github.teonistor.devschess.piece.Knight;
import io.github.teonistor.devschess.piece.Pawn;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;


public class Board {

    public static Map<Position, Piece> initialSetup() {
        // TODO Do when we have all pieces
        return HashMap.<Position,Piece>of(Position.A2, new Pawn(White))
                .put(Position.B2, new Pawn(White))
                .put(Position.C2, new Pawn(White))
                .put(Position.D2, new Pawn(White))
                .put(Position.E2, new Pawn(White))
                .put(Position.F2, new Pawn(White))
                .put(Position.G2, new Pawn(White))
                .put(Position.H2, new Pawn(White))
                .put(Position.B1, new Knight(White))
                .put(Position.G1, new Knight(White))
                .put(Position.A7, new Pawn(Black))
                .put(Position.B7, new Pawn(Black))
                .put(Position.C7, new Pawn(Black))
                .put(Position.D7, new Pawn(Black))
                .put(Position.E7, new Pawn(Black))
                .put(Position.F7, new Pawn(Black))
                .put(Position.G7, new Pawn(Black))
                .put(Position.H7, new Pawn(Black))
                .put(Position.B8, new Knight(Black))
                .put(Position.G8, new Knight(Black));
    }
}
