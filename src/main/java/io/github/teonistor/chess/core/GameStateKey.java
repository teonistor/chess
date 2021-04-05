package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access=PRIVATE)
@EqualsAndHashCode
@Getter
public class GameStateKey {
    public static final GameStateKey NIL = new GameStateKey(null, null, null, null, null, null);

    private final Map<Player, BiFunction<Position,Position,GameStateKey>> withInput = HashMap.of(Player.White, this::withWhiteInput, Player.Black, this::withBlackInput);

    private final Position whiteFrom;
    private final Position whiteTo;
    private final Piece whitePromotion;
    private final Position blackFrom;
    private final Position blackTo;
    private final Piece blackPromotion;

    public boolean matchesDefinedFields(final GameStateKey other) {
        return true;
    }

    public GameStateKey withInput(Player player, Position from, Position to){
        return withInput.get(player).get().apply(from, to);
    }

    public GameStateKey withWhiteInput(Position from, Position to){
        return this;
    }

    public GameStateKey withBlackInput(Position from, Position to){
        return this;
    }

}
