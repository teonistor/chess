package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.rule.AvailableMovesRule;
import io.github.teonistor.chess.rule.GameOverChecker;
import io.github.teonistor.chess.util.PositionPairExtractor;
import io.github.teonistor.chess.util.PromotionRequirementExtractor;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.RequiredArgsConstructor;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
public class StateInferer {

    private final AvailableMovesRule availableMovesRule;
    private final GameOverChecker gameOverChecker;
    private final PositionPairExtractor positionPairExtractor;
    private final PromotionRequirementExtractor promotionRequirementExtractor;

    public InferredState infer(final GameState state, final GameStateKey key) {
        final Map<GameStateKey, GameState> availableMoves = availableMovesRule.computeAvailableMoves(state);
        final Set<Tuple2<Position, Position>> blackMovePairs = positionPairExtractor.extractBlack(availableMoves);
        final Set<Tuple2<Position, Position>> whiteMovePairs = positionPairExtractor.extractWhite(availableMoves);
        final GameCondition condition = gameOverChecker.check(state.getBoard(), state.getPlayer(), availableMoves);
        final Set<Position> highlighted = computeHighlighted(state);
        final boolean promotionRequiredBlack = promotionRequirementExtractor.extractBlack(key, availableMoves);
        final boolean promotionRequiredWhite = promotionRequirementExtractor.extractWhite(key, availableMoves);

        return new InferredState(availableMoves, blackMovePairs, whiteMovePairs, condition, highlighted, promotionRequiredBlack, promotionRequiredWhite);
    }


    // TODO Left here for now because wherever we put it it'll be screwed by the next game state overhaul for Parallel
    // TODO Currently for Parallel only the move from the last intermediate state is shown

    private Set<Position> computeHighlighted(final GameState state) {
        return state.getPrevious() != null
             ? antijoin(state.getPrevious().getBoard(), state.getBoard())
             : HashSet.empty();
    }

    private Set<Position> antijoin(final Map<Position, Piece> previousBoard, final Map<Position, Piece> currentBoard) {
        return previousBoard.filter(not(currentBoard::contains)).keySet().addAll(
                currentBoard.filter(not(previousBoard::contains)).keySet());
    }
}
