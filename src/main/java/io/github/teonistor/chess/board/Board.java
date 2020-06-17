package io.github.teonistor.chess.board;

import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.piece.UnmovedKing;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;


@AllArgsConstructor
public class Board {

    private final @NonNull UnderAttackRule underAttackRule;

    public Map<Position, Piece> initialSetup() {
        return HashMap.of(
                     Position.A1, new Rook(White),
                     Position.B1, new Knight(White),
                     Position.C1, new Bishop(White),
                     Position.D1, new Queen(White),
                     Position.E1, new UnmovedKing(White, underAttackRule),
                     Position.F1, new Bishop(White),
                     Position.G1, new Knight(White),
                     Position.H1, new Rook(White))
                .put(Position.A2, new Pawn(White))
                .put(Position.B2, new Pawn(White))
                .put(Position.C2, new Pawn(White))
                .put(Position.D2, new Pawn(White))
                .put(Position.E2, new Pawn(White))
                .put(Position.F2, new Pawn(White))
                .put(Position.G2, new Pawn(White))
                .put(Position.H2, new Pawn(White))

                .put(Position.A7, new Pawn(Black))
                .put(Position.B7, new Pawn(Black))
                .put(Position.C7, new Pawn(Black))
                .put(Position.D7, new Pawn(Black))
                .put(Position.E7, new Pawn(Black))
                .put(Position.F7, new Pawn(Black))
                .put(Position.G7, new Pawn(Black))
                .put(Position.H7, new Pawn(Black))

                .put(Position.A8, new Rook(Black))
                .put(Position.B8, new Knight(Black))
                .put(Position.C8, new Bishop(Black))
                .put(Position.D8, new Queen(Black))
                .put(Position.E8, new UnmovedKing(Black, underAttackRule))
                .put(Position.F8, new Bishop(Black))
                .put(Position.G8, new Knight(Black))
                .put(Position.H8, new Rook(Black));
    }
}
