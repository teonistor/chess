package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.inter.DefinitelyInput;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.save.SaveLoad;

public class Factory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final PieceBox pieceBox;
    private final InitialBoardProvider initialBoardProvider;
    private final InitialStateProvider initialStateProvider;
    private final PieceSerialiser pieceSerialiser;
    private final SaveLoad saveLoad;
    private final InputActionProvider inputActionProvider;

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
    }

    public ControlLoop createTerminalControlLoop() {
        return new ControlLoop(saveLoad, createGameFactory(createTerminalView()), createTerminalInput());
    }

    GameFactory createGameFactory(final View... views) {
        return (p, i) -> new Game(p, checkRule, gameOverChecker, i, MultipleViewWrapper.wrapIfNeeded(views));
    }

    DefinitelyInput createTerminalInput() {
        return new TerminalInput(inputActionProvider);
    }

    View createTerminalView() {
        return new TerminalView();
    }
}
