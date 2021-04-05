package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.util.NestedMapKeyExtractor;
import io.vavr.Lazy;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
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

    private final @Getter GameState state;
    private final @With(PRIVATE) PartialState partialState;

    private final Lazy<Map<Position, Map<Position, PartialState>>> availableMoves = Lazy.of(this::computeAvailableMoves);
    private final Lazy<GameCondition> condition = Lazy.of(this::computeCondition);

    public Game(AvailableMovesRule availableMovesRule, GameOverChecker gameOverChecker, NestedMapKeyExtractor nestedMapKeyExtractor, GameState state) {
        this(availableMovesRule, gameOverChecker, nestedMapKeyExtractor, state, PartialState.NIL);
    }

    public GameCondition getCondition() {
        return condition.get();
    }

    public void triggerView(final View view) {
        partialState.triggerViewIfNeeded()
                .getOrElse(this::triggerView0)
                .accept(view);
    }

    private void triggerView0(final View view) {
        switch (getCondition()) {
            case Continue:
                final Stream<Tuple2<Position, Position>> possibleMoves = nestedMapKeyExtractor.extract(getAvailableMoves());
                if (state.getPlayer() == Player.White)
                    view.refresh(state.getBoard(), state.getCapturedPieces(), Stream.empty(), possibleMoves);
                else
                    view.refresh(state.getBoard(), state.getCapturedPieces(), possibleMoves, Stream.empty());
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
                    .map(partialState -> partialState.completeState().map(this::withState)
                    .getOrElse(this.withPartialState(partialState)))
                    .getOrElse(this);

        } else {
            return this;
        }
    }

    private Map<Position, Map<Position, PartialState>> getAvailableMoves() {
        return availableMoves.get();
    }

    private Map<Position, Map<Position, PartialState>> computeAvailableMoves() {
        return availableMovesRule.computeAvailableMoves(state);
    }

    private GameCondition computeCondition() {
        return gameOverChecker.check(state.getBoard(), state.getPlayer(), getAvailableMoves());
    }

    private Game withState(GameState state) {
        return this.state == state ? this : new Game(this.availableMovesRule, this.gameOverChecker, this.nestedMapKeyExtractor, state, PartialState.NIL);
    }
}
