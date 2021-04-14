package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.util.PositionPairExtractor;
import io.vavr.Lazy;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import static io.github.teonistor.chess.core.GameCondition.Continue;

@RequiredArgsConstructor
public class Game {

    private final AvailableMovesRule availableMovesRule;
    private final GameOverChecker gameOverChecker;
    private final PositionPairExtractor positionPairExtractor;

    private final @Getter GameState state;
    private final @With GameStateKey key;

    private final Lazy<Map<GameStateKey,GameState>> availableMoves = Lazy.of(this::computeAvailableMoves);
    private final Lazy<GameCondition> condition = Lazy.of(this::computeCondition);

    public Game(final AvailableMovesRule availableMovesRule, final GameOverChecker gameOverChecker, final PositionPairExtractor positionPairExtractor, final GameState state) {
        this(availableMovesRule, gameOverChecker, positionPairExtractor, state, GameStateKey.NIL);
    }

    public GameCondition getCondition() {
        return condition.get();
    }

    public void triggerView(final View view) {
        switch (getCondition()) {
            case Continue:
                if (key.noPositionsDefined())
                    view.refresh(state.getBoard(), state.getCapturedPieces(), positionPairExtractor.extractBlack(getAvailableMoves()), positionPairExtractor.extractWhite(getAvailableMoves()));
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
            return state.getBoard().get(from)
                 . map(piece -> key.withInput(piece.getPlayer(), from, to))
                 . flatMap(key -> getAvailableMoves().get(key)
                 . map(this::withState)
                 . orElse(Option.some(key).filter(this::partialMatchesExist)
                 . map(this::withKey)))
                 . getOrElse(this);

        } else {
            return this;
        }
    }

    private boolean partialMatchesExist(final GameStateKey key) {
        return getAvailableMoves().keySet().count(key::matchesDefinedFields) > 0;
    }

    private Map<GameStateKey,GameState> getAvailableMoves() {
        return availableMoves.get();
    }

    private Map<GameStateKey,GameState> computeAvailableMoves() {
        return availableMovesRule.computeAvailableMoves(state);
    }

    private GameCondition computeCondition() {
        return gameOverChecker.check(state.getBoard(), state.getPlayer(), getAvailableMoves());
    }

    private Game withState(final GameState state) {
        return this.state == state ? this : new Game(this.availableMovesRule, this.gameOverChecker, this.positionPairExtractor, state);
    }
}
