package io.github.teonistor.chess.piece;


import io.github.teonistor.chess.core.UnderAttackRule;

import static io.github.teonistor.chess.core.Player.*;

// Tested by InitialBoardProviderTest
public class PieceBox {

    public final Piece blackPawn;
    public final Piece blackRook;
    public final Piece blackKnight;
    public final Piece blackBishop;
    public final Piece blackQueen;
    public final Piece blackKing;
    public final Piece whitePawn;
    public final Piece whiteRook;
    public final Piece whiteKnight;
    public final Piece whiteBishop;
    public final Piece whiteQueen;
    public final Piece whiteKing;

    public PieceBox(UnderAttackRule rule) {
        blackPawn = new Pawn(Black);
        blackRook = new Rook(Black);
        blackKnight = new Knight(Black);
        blackBishop = new Bishop(Black);
        blackQueen = new Queen(Black);
        blackKing = new King(Black, rule);
        whitePawn = new Pawn(White);
        whiteRook = new Rook(White);
        whiteKnight = new Knight(White);
        whiteBishop = new Bishop(White);
        whiteQueen = new Queen(White);
        whiteKing = new King(White, rule);
    }
}
