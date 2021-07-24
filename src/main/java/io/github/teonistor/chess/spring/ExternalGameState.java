package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import lombok.Value;

@Value
public class ExternalGameState {
    public static final ExternalGameState NIL = new ExternalGameState(HashMap.empty(), List.empty(), HashSet.empty(), List.empty(), HashMap.empty(), false, false);

    // Universal
    Map<Position, Piece> board;
    Traversable<Piece> capturedPieces;
    Set<Position> highlighted;

    // Player-specific
    Traversable<Tuple2<Position, Position>> possibleMoves;
    Map<Position, Piece> provisional;
    boolean promotionRequiredW;
    boolean promotionRequiredB;

    public ExternalGameState combine(final ExternalGameState other) {
        return new ExternalGameState(board, capturedPieces, highlighted,
            Stream.concat(possibleMoves, other.possibleMoves),
            HashMap.ofEntries(Stream.concat(provisional, other.provisional)),
            promotionRequiredW || other.promotionRequiredW,
            promotionRequiredB || other.promotionRequiredB);
    }
}
