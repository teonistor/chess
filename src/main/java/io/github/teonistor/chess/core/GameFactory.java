package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import static io.github.teonistor.chess.core.StateProvision.New;

public class GameFactory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final InitialBoardProvider initialBoardProvider;
    private final InitialStateProvider initialStateProvider;
    private final Map<StateProvision, InitialStateProvider> stateProvision;

    public GameFactory() {
        underAttackRule = new UnderAttackRule();
        checkRule = new CheckRule(underAttackRule);
        gameOverChecker = new GameOverChecker(underAttackRule);
        initialBoardProvider = new InitialBoardProvider(underAttackRule);
        initialStateProvider = new InitialStateProvider(initialBoardProvider);
        stateProvision = HashMap.of(New, initialStateProvider);
    }

    public Game createTerminalGame() {
        return createGame(new TerminalInput(), new TerminalInput(), new TerminalView());
    }

    public Game createGame(Input white, Input black, View... views) {
        return new Game(white.stateProvision().map(stateProvision).get(), checkRule, gameOverChecker, white, black, MultipleViewWrapper.wrapIfNeeded(views));
    }
}
