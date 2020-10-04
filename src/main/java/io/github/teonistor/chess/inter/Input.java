package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.StateProvision;
import io.vavr.control.Option;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Input {
    <T> T takeInput(Function<Position, T> callbackOne, BiFunction<Position, Position, T> callbackTwo);
    <T> T takeInput(Function<Position, T> callback);
//    Function<Player, Piece> promotionPiece();
    // TODO This is player-independent, making this interface confusing
    Option<StateProvision> stateProvision();
}
