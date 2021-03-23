package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.factory.ControlLoopFactory;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.save.SaveLoad;
import io.github.teonistor.chess.util.NestedMapKeyExtractor;
import lombok.Getter;

public class Factory implements ControlLoopFactory, io.github.teonistor.chess.factory.GameFactory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final PieceBox pieceBox;
    private final InitialBoardProvider initialBoardProvider;
    private final InitialStateProvider initialStateProvider;
    private final PieceSerialiser pieceSerialiser;
    private final SaveLoad saveLoad;
    private final @Getter InputActionProvider inputActionProvider;
    private final AvailableMovesRule availableMovesRule;
    private final NestedMapKeyExtractor nestedMapKeyExtractor;

    public Factory() {
        underAttackRule = new UnderAttackRule();
        checkRule = new CheckRule(underAttackRule);
        gameOverChecker = new GameOverChecker(checkRule);
        pieceBox = new PieceBox(underAttackRule);
        initialBoardProvider = new InitialBoardProvider(pieceBox);
        initialStateProvider = new InitialStateProvider(initialBoardProvider);
        pieceSerialiser = new PieceSerialiser(pieceBox);
        saveLoad = new SaveLoad(pieceSerialiser);
        inputActionProvider = new InputActionProvider(initialStateProvider, saveLoad);
        availableMovesRule = new AvailableMovesRule(checkRule);
        nestedMapKeyExtractor = new NestedMapKeyExtractor();
    }

    public ControlLoop createControlLoop(final View... views) {
        return new ControlLoop(saveLoad, this, MultipleViewWrapper.wrapIfNeeded(views));
    }

    public Game createNewGame(final View view) {
        return new Game(availableMovesRule, gameOverChecker, nestedMapKeyExtractor, initialStateProvider.createState());
    }

    public Game createGame(final View view, final GameState state) {
        return new Game(availableMovesRule, gameOverChecker, nestedMapKeyExtractor, state);
    }
}
