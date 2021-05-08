package io.github.teonistor.chess.factory;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.core.AvailableMovesRule;
import io.github.teonistor.chess.core.CheckRule;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameOverChecker;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.core.ParallelAvailableMovesRule;
import io.github.teonistor.chess.core.PieceSerialiser;
import io.github.teonistor.chess.core.StandardAvailableMovesRule;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.save.SaveLoad;
import io.github.teonistor.chess.util.PositionPairExtractor;
import io.github.teonistor.chess.util.PromotionRequirementExtractor;
import lombok.Getter;

public class Factory implements ControlLoopFactory, GameFactory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final PieceBox pieceBox;
    private final InitialBoardProvider initialBoardProvider;
    private final InitialStateProvider initialStateProvider;
    private final @Getter PieceSerialiser pieceSerialiser;
    private final SaveLoad saveLoad;
    private final AvailableMovesRule standardAvailableMovesRule;
    private final AvailableMovesRule parallelAvailableMovesRule;
    private final PositionPairExtractor positionPairExtractor;
    private final PromotionRequirementExtractor promotionRequirementExtractor;

    public Factory() {
        underAttackRule = new UnderAttackRule();
        checkRule = new CheckRule(underAttackRule);
        gameOverChecker = new GameOverChecker(checkRule);
        pieceBox = new PieceBox(underAttackRule);
        initialBoardProvider = new InitialBoardProvider(pieceBox);
        initialStateProvider = new InitialStateProvider(initialBoardProvider);
        pieceSerialiser = new PieceSerialiser(pieceBox);
        saveLoad = new SaveLoad(pieceSerialiser);
        standardAvailableMovesRule = new StandardAvailableMovesRule(checkRule);
        parallelAvailableMovesRule = new ParallelAvailableMovesRule(checkRule);
        positionPairExtractor = new PositionPairExtractor();
        promotionRequirementExtractor = new PromotionRequirementExtractor();
    }

    public ControlLoop createControlLoop(final View... views) {
        return new ControlLoop(saveLoad, this, MultipleViewWrapper.wrapIfNeeded(views));
    }

    public Game createNewStandardGame() {
        return createGame(GameType.STANDARD, initialStateProvider.createState());
    }

    public Game createNewParallelGame() {
        return createGame(GameType.PARALLEL, initialStateProvider.createState());
    }

    public Game createGame(final GameType type, final GameState state) {
        return new Game(type.availableMovesRule(this), gameOverChecker, positionPairExtractor, promotionRequirementExtractor, type, state);
    }


    private interface GameTypeProvider {
        AvailableMovesRule availableMovesRule(Factory f);
    }

    public enum GameType implements GameTypeProvider {
        STANDARD {
            public AvailableMovesRule availableMovesRule(final Factory f) {
                return f.standardAvailableMovesRule;
            }
        },

        PARALLEL {
            public AvailableMovesRule availableMovesRule(final Factory f) {
                return f.parallelAvailableMovesRule;
            }
        }
    }
}
