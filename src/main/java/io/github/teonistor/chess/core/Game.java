package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.factory.Factory.GameType;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.util.PositionPairExtractor;
import io.github.teonistor.chess.util.PromotionRequirementExtractor;
import io.vavr.Lazy;
import io.vavr.collection.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import static io.github.teonistor.chess.core.GameCondition.Continue;

@RequiredArgsConstructor
public class Game {

    private final AvailableMovesRule availableMovesRule;
    private final GameOverChecker gameOverChecker;
    private final PositionPairExtractor positionPairExtractor;
    private final PromotionRequirementExtractor promotionRequirementExtractor;

    private final @Getter GameType type;
    private final @Getter GameState state;
    private final @With GameStateKey key;

    private final Lazy<Map<GameStateKey,GameState>> availableMoves = Lazy.of(this::computeAvailableMoves);
    private final Lazy<GameCondition> condition = Lazy.of(this::computeCondition);

    public Game(final AvailableMovesRule availableMovesRule, final GameOverChecker gameOverChecker, final PositionPairExtractor positionPairExtractor, final PromotionRequirementExtractor promotionRequirementExtractor, final GameType type, final GameState state) {
        this(availableMovesRule, gameOverChecker, positionPairExtractor, promotionRequirementExtractor, type, state, GameStateKey.NIL);
    }

    public GameCondition getCondition() {
        return condition.get();
    }

    public void triggerView(final View view) {
        view.refresh(state.getBoard(), state.getCapturedPieces(), positionPairExtractor.extractBlack(getAvailableMoves()), positionPairExtractor.extractWhite(getAvailableMoves()), promotionRequirementExtractor.extractBlack(key, getAvailableMoves()), promotionRequirementExtractor.extractWhite(key, getAvailableMoves()));

        switch (getCondition()) {
            // TODO It is impossible to checkmate in the parallel game under the standard rules because there always exists the move where the player who checkmated removes the checkmate

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
        return state.getBoard().get(from)
             . map(piece -> key.withInput(piece.getPlayer(), from, to))
             . map(this::processInput)
             . getOrElse(this);
    }

    public Game processInput(final Piece promotionPiece) {
        return processInput(key.withPromotion(promotionPiece));
    }

    private Game processInput(final GameStateKey newKey) {
        if (!Continue.equals(getCondition()))
            return this;

        if (matchExists(newKey))
            return this.withState(getAvailableMoves().get(newKey).get());

        if (partialMatchesExist(newKey))
            return this.withKey(newKey);

        // TODO Announce the fact that the move was cancelled
        // TODO Ban the cancelled move until a successful move is made
        return this.withKey(GameStateKey.NIL);
    }

    private boolean matchExists(final GameStateKey newKey) {
        return getAvailableMoves().containsKey(newKey);
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
        return new Game(availableMovesRule, gameOverChecker, positionPairExtractor, promotionRequirementExtractor, type, state);
    }
}
