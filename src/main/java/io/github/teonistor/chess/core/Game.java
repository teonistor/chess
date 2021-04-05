package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.util.PositionPairExtractor;
import io.vavr.Lazy;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import static io.github.teonistor.chess.core.GameCondition.Continue;
import static io.github.teonistor.chess.core.GameStateKey.NIL;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
public class Game {

    private final AvailableMovesRule availableMovesRule;
    private final GameOverChecker gameOverChecker;
    private final PositionPairExtractor positionPairExtractor;

    private final @Getter @With(PRIVATE) GameState state;
    private final Lazy<Map<GameStateKey,GameState>> availableMoves = Lazy.of(this::computeAvailableMoves);
    private final Lazy<GameCondition> condition = Lazy.of(this::computeCondition);

    public Game(final AvailableMovesRule availableMovesRule, final GameOverChecker gameOverChecker, final PositionPairExtractor positionPairExtractor, final View view, final GameStateProvider gameStateProvider) {
        this(availableMovesRule, gameOverChecker, positionPairExtractor, gameStateProvider.createState());
    }

    public GameCondition getCondition() {
        return condition.get();
    }

    public void triggerView(final View view) {
        switch (getCondition()) {
            case Continue:
                if (state.getPlayer() == Player.White)
                    view.refresh(state.getBoard(), state.getCapturedPieces(), Stream.empty(), positionPairExtractor.extractWhite(getAvailableMoves()));
                else
                    view.refresh(state.getBoard(), state.getCapturedPieces(), positionPairExtractor.extractBlack(getAvailableMoves()), Stream.empty());
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
                 . map(piece -> NIL.withInput(piece.getPlayer(), from, to))
                 . flatMap(getAvailableMoves()::get)
                 . map(this::withState)
                 . getOrElse(this);

        } else {
            return this;
        }
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
}
