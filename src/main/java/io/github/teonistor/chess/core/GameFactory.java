package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.InputEngine;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public class GameFactory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final PieceBox pieceBox;
    private final InitialBoardProvider initialBoardProvider;
    private final InitialStateProvider initialStateProvider;
    private final SaveLoad saveLoad;
    private final InputActionProvider inputActionProvider;
    private final Map<StateProvision, InitialStateProvider> stateProvision;

    public GameFactory() {
        underAttackRule = new UnderAttackRule();
        checkRule = new CheckRule(underAttackRule);
        gameOverChecker = new GameOverChecker(underAttackRule);
        pieceBox = new PieceBox(underAttackRule);
        initialBoardProvider = new InitialBoardProvider(pieceBox);
        initialStateProvider = new InitialStateProvider(initialBoardProvider);
        saveLoad = new SaveLoad();
        inputActionProvider = new InputActionProvider(initialStateProvider, saveLoad);
        stateProvision = HashMap.of(StateProvision.New, initialStateProvider);
    }

    public Game createTerminalGame() {
//        return createGame(new TerminalInput(inputActionProvider, inputActionConsumer), new TerminalInput(inputActionProvider, inputActionConsumer), new TerminalView());
    throw new UnsupportedOperationException("This has to vanish");
    }

    public Game createGame(GameStateProvider provider, Input input, View... views) {
        return new Game(provider, checkRule, gameOverChecker, input, input, MultipleViewWrapper.wrapIfNeeded(views));
    }

    public ControlLoop createControlLoop(InputEngine inputEngine, View view) {

        // TODO Figure out, and rename per, what is created and what is launched etc
        final ControlLoop controlLoop = new ControlLoop(inputEngine, view, saveLoad, this);
        inputEngine.prepare(controlLoop::processInput);

        return controlLoop;
    }
}
