package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.StateProvision;
import io.github.teonistor.chess.ctrl.InputAction;
import io.vavr.PartialFunction;
import org.apache.commons.lang3.Functions;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Input {

    @Deprecated
    <T> T takeInput(Function<Position, T> callbackOne, BiFunction<Position, Position, T> callbackTwo);

    @Deprecated
    <T> T takeInput(Function<Position, T> callback);
//    Function<Player, Piece> promotionPiece();
    // TODO This is player-independent, making this interface confusing
    @Deprecated
    GameStateProvider stateProvision(PartialFunction<StateProvision, Functions.FailableFunction<String,GameStateProvider,Exception>> stateProvision);

    InputAction takeCommonInput();
    String takeSpecialInput(String... options);
}
