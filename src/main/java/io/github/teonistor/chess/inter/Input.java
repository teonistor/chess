package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Input {
    @Deprecated
    Position takeInput();

    <T> T takeInput(Function<Position, T> callbackOne, BiFunction<Position, Position, T> callbackTwo);
    <T> T takeInput(Function<Position, T> callback);
}
