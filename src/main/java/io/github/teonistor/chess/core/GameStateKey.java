package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

import java.util.function.BiFunction;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(exclude="withInput")
@Getter
public class GameStateKey {
    public static final GameStateKey NIL = new GameStateKey(null, null, null, null, null, null);

    private final Map<Player, BiFunction<Position,Position,GameStateKey>> withInput = HashMap.of(Player.White, this::withWhiteInput, Player.Black, this::withBlackInput);

    private final Position whiteFrom;
    private final Position whiteTo;
    private final @With Piece whitePromotion;
    private final Position blackFrom;
    private final Position blackTo;
    private final @With Piece blackPromotion;

    public boolean matchesDefinedFields(final GameStateKey other) {
        return (whiteFrom == null      || whiteFrom == other.whiteFrom)
            && (whiteTo == null        || whiteTo == other.whiteTo)
            && (whitePromotion == null || whitePromotion.equals(other.whitePromotion))
            && (blackFrom == null      || blackFrom == other.blackFrom)
            && (blackTo == null        || blackTo == other.blackTo)
            && (blackPromotion == null || blackPromotion.equals(other.blackPromotion));
    }

    public GameStateKey withInput(final Player player, final Position from, final Position to) {
        return withInput.get(player).get().apply(from, to);
    }

    public GameStateKey withWhiteInput(final Position from, final Position to) {
        return new GameStateKey(from, to, whitePromotion, blackFrom, blackTo, blackPromotion);
    }

    public GameStateKey withBlackInput(final Position from, final Position to) {
        return new GameStateKey(whiteFrom, whiteTo, whitePromotion, from, to, blackPromotion);
    }

}
