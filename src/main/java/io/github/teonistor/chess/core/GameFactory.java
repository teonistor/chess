package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;

import java.io.IOException;

import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public class GameFactory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final InitialBoardProvider initialBoardProvider;
    private final GameStateProvider gameStateProvider;

    public GameFactory() {
        underAttackRule = new UnderAttackRule();
        checkRule = new CheckRule(underAttackRule);
        gameOverChecker = new GameOverChecker(underAttackRule);
        initialBoardProvider = new InitialBoardProvider(underAttackRule);
        gameStateProvider = new InitialStateProvider(initialBoardProvider);
    }

    public Game createTerminalGame() {
        return createGame(new TerminalInput(), new TerminalInput(), new TerminalView());
    }

    public Game createGame(final Input white, final Input black, final View... views) {
        return new Game(gameStateProvider, checkRule, gameOverChecker, white, black, MultipleViewWrapper.wrapIfNeeded(views));
    }

    public Game loadGame() {
        try {
            return new Game(SaveLoad.loadState(), checkRule, gameOverChecker, new TerminalInput(), new TerminalInput(), MultipleViewWrapper.wrapIfNeeded(new TerminalView()));
        } catch (final IOException e) {
            return rethrow(e);
        }
    }
}
