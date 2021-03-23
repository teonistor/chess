package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.util.NestedMapKeyExtractor;
import io.vavr.Lazy;
import io.vavr.collection.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import static io.github.teonistor.chess.core.GameCondition.Continue;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
public class Game {

    private final AvailableMovesRule availableMovesRule;
    private final GameOverChecker gameOverChecker;
    private final NestedMapKeyExtractor nestedMapKeyExtractor;

    private final @Getter @With(PRIVATE) GameState state;
    private final Lazy<Map<Position, Map<Position, GameState>>> availableMoves = Lazy.of(this::computeAvailableMoves);
    private final Lazy<GameCondition> condition = Lazy.of(this::computeCondition);

    public Game(final AvailableMovesRule availableMovesRule, final GameOverChecker gameOverChecker, final NestedMapKeyExtractor nestedMapKeyExtractor, final View view, final GameStateProvider gameStateProvider) {
        this(availableMovesRule, gameOverChecker, nestedMapKeyExtractor, gameStateProvider.createState());
    }

    public GameCondition getCondition() {
        return condition.get();
    }

    public void triggerView(final View view) {
        switch (getCondition()) {
            case Continue:
                view.refresh(state.getBoard(), state.getPlayer(), state.getCapturedPieces(), nestedMapKeyExtractor.extract(getAvailableMoves()));
                break;

            case WhiteWins:
                view.announce("White wins!");
                break;

            case BlackWins:
                view.announce("Black wins!");
                break;

            case Stalemate:
                view.announce("Stalemate!");
                break;
        }
    }

    public Game processInput(final Position from, final Position to) {
        if (Continue.equals(getCondition())) {
            return getAvailableMoves().get(from)
                    .flatMap(moveSubset -> moveSubset.get(to))
                    .map(this::withState)
                    .getOrElse(this);

        } else {
            return this;
        }
    }

    private Map<Position, Map<Position, GameState>> getAvailableMoves() {
        return availableMoves.get();
    }

    private Map<Position, Map<Position, GameState>> computeAvailableMoves() {
        return availableMovesRule.computeAvailableMoves(state);
    }

    private GameCondition computeCondition() {
        return gameOverChecker.check(state.getBoard(), state.getPlayer(), getAvailableMoves());
    }
}
