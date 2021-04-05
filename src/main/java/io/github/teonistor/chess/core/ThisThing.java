package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Lazy;
import io.vavr.Tuple2;
import io.vavr.Tuple4;
import io.vavr.collection.HashMap;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access=PRIVATE)
public class ThisThing {

    private final HashMap<Tuple4<Position, Position, Position, Position>, PartialState> decisionTree;

    private final Position blackFrom;
    private final Position blackTo;
    private final Position whiteFrom;
    private final Position whiteTo;

    private final Lazy<Set<Tuple2<Position,Position>>> plausibleBlackInput = Lazy.of(this::computePlausibleBlackInput);
    private final Lazy<Set<Tuple2<Position,Position>>> plausibleWhiteInput = Lazy.of(this::computePlausibleWhiteInput);

    public ThisThing(HashMap<Tuple4<Position, Position, Position, Position>, PartialState> decisionTree) {
        this(decisionTree, null, null, null, null);
    }

    public Option<GameState> decide() {
        if (blackFrom != null && whiteFrom != null)
            return decisionTree.get(new Tuple4<>(whiteFrom, whiteTo, blackFrom, blackTo))
                  .getOrElseThrow(StuckException::new)
                  .completeState();

        return Option.none();
    }

    public ThisThing blackInput(Position blackFrom, Position blackTo) {
        if (this.blackFrom != null)
            throw new RepeatedInputException();

        if (plausibleBlackInput.get().contains(new Tuple2<>(blackFrom, blackTo)))
            return new ThisThing(decisionTree, blackFrom, blackTo, whiteFrom, whiteTo);

        throw new ImplausibleInputException();
    }

    public ThisThing whiteInput(Position whiteFrom, Position whiteTo) {
        if (this.whiteFrom != null)
            throw new RepeatedInputException();

        if (plausibleWhiteInput.get().contains(new Tuple2<>(whiteFrom, whiteTo)))
            return new ThisThing(decisionTree, blackFrom, blackTo, whiteFrom, whiteTo);

        throw new ImplausibleInputException();
    }

    public ThisThing blackPromotes(Piece piece) {
        return this;
    }

    private Set<Tuple2<Position, Position>> computePlausibleBlackInput() {
        return decisionTree.keySet().map(t -> new Tuple2<>(t._3, t._4));
    }

    private Set<Tuple2<Position, Position>> computePlausibleWhiteInput() {
        return decisionTree.keySet().map(t -> new Tuple2<>(t._1, t._2));
    }

    public static class ImplausibleInputException extends RuntimeException {}

    public static class RepeatedInputException extends RuntimeException {}

    public static class StuckException extends RuntimeException {}
}
