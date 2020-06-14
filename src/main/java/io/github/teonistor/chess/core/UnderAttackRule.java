package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Bishop;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Knight;
import io.github.teonistor.chess.piece.Pawn;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Queen;
import io.github.teonistor.chess.piece.Rook;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;

import java.util.function.UnaryOperator;

public class UnderAttackRule {

    private final Map<UnaryOperator<Position>,HashSet<Class<? extends Piece>>> recursiveSteps;
    private final Map<UnaryOperator<Position>,HashSet<Class<? extends Piece>>> whiteSteps;
    private final Map<UnaryOperator<Position>,HashSet<Class<? extends Piece>>> blackSteps;

    public UnderAttackRule() {
        // These are extracted so that we can replace them as keys into the color-specialised maps (different lambdas of the same expression don't compare equal)
        UnaryOperator<Position> upLeft    = p -> p.up().left();
        UnaryOperator<Position> upRight   = p -> p.up().right();
        UnaryOperator<Position> downLeft  = p -> p.down().left();
        UnaryOperator<Position> downRight = p -> p.down().right();

        recursiveSteps = HashMap.of(
            Position::left, HashSet.of(Queen.class, Rook.class),
            Position::right,HashSet.of(Queen.class, Rook.class),
            Position::up,   HashSet.of(Queen.class, Rook.class),
            Position::down, HashSet.of(Queen.class, Rook.class),
            upLeft,         HashSet.of(Queen.class, Bishop.class),
            upRight,        HashSet.of(Queen.class, Bishop.class),
            downLeft,       HashSet.of(Queen.class, Bishop.class),
            downRight,      HashSet.of(Queen.class, Bishop.class));

        Map<UnaryOperator<Position>, HashSet<Class<? extends Piece>>> nonRecursiveSteps = HashMap.<UnaryOperator<Position>, HashSet<Class<? extends Piece>>>of(
            p -> p.up().up().left(),       HashSet.of(Knight.class),
            p -> p.up().up().right(),      HashSet.of(Knight.class),
            p -> p.left().left().up(),     HashSet.of(Knight.class),
            p -> p.left().left().down(),   HashSet.of(Knight.class),
            p -> p.down().down().left(),   HashSet.of(Knight.class),
            p -> p.down().down().right(),  HashSet.of(Knight.class),
            p -> p.right().right().up(),   HashSet.of(Knight.class),
            p -> p.right().right().down(), HashSet.of(Knight.class))
           .put(Position::left,    HashSet.of(King.class))
           .put(Position::right,   HashSet.of(King.class))
           .put(Position::up,      HashSet.of(King.class))
           .put(Position::down,    HashSet.of(King.class))
           .put(upLeft,            HashSet.of(King.class))
           .put(upRight,           HashSet.of(King.class))
           .put(downLeft,          HashSet.of(King.class))
           .put(downRight,         HashSet.of(King.class));
        
        // n.b. These are named based on the player who is under attack and defined such that we proceed from the checked position to the (potential) attacking piece
        whiteSteps = nonRecursiveSteps
                .put(upLeft,  HashSet.of(Pawn.class, King.class))
                .put(upRight, HashSet.of(Pawn.class, King.class));
        blackSteps = nonRecursiveSteps
                .put(downLeft,  HashSet.of(Pawn.class, King.class))
                .put(downRight, HashSet.of(Pawn.class, King.class));
    }

    /**
     * Check if on the given board the given player is under attack a the given position
     * @param board The game board
     * @param position The position to check
     * @param player The player who may be under attack
     * @return true if the player is under attack, false otherwise
     */
    public boolean checkAttack(Map<Position, Piece> board, Position position, Player player) {
        switch (player) {
            case White:
                return checkAttack(board, position, Player.Black, whiteSteps);
            case Black:
                return checkAttack(board, position, Player.White, blackSteps);
        }
        throw new IllegalArgumentException("What player is " + player);
    }

    private boolean checkAttack(Map<Position, Piece> board, Position position, Player enemy, Map<UnaryOperator<Position>, HashSet<Class<? extends Piece>>> nonRecursiveSteps) {
        return nonRecursiveSteps.toStream().map(stepAndTypes -> {
            return board.get(stepAndTypes._1.apply(position))
                    .filter(piece -> piece.getPlayer() == enemy)
                    .map(Piece::getClass)
                    .filter(stepAndTypes._2::contains)
                    .isDefined();
        }).reduce(Boolean::logicalOr)

                ||

                recursiveSteps.toStream().map(stepAndTypes -> recurseStep(board, stepAndTypes._1.apply(position), enemy, stepAndTypes._1, stepAndTypes._2)).reduce(Boolean::logicalOr);
    }

    private boolean recurseStep (Map<Position, Piece> board, Position currentPosition, Player enemy, UnaryOperator<Position> step, HashSet<Class<? extends Piece>> types) {
        return currentPosition != Position.OutOfBoard
            && board.get(currentPosition)
                    .map(piece -> piece.getPlayer() == enemy && types.contains(piece.getClass()))
                    .getOrElse(() -> recurseStep(board, step.apply(currentPosition), enemy, step, types));
    }

    /*
    A set of methods like this may be too complicated?

    isKnight() -> Knight
    isPawnOrDiagonallyAttacking() -> Pawn,
    isDiagonallyAttacking()
    isOrthogonallyAttacking()

     */
}
