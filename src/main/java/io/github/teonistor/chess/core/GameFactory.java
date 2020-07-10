package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.MultipleViewWrapper;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;

public class GameFactory {

    private final UnderAttackRule underAttackRule;
    private final CheckRule checkRule;
    private final GameOverChecker gameOverChecker;
    private final InitialBoardProvider initialBoardProvider;
    private final InitialStateProvider initialStateProvider;

//    One day...
//    private final @Default Supplier<UnderAttackRule> underAttackRule = () -> new UnderAttackRule();
//    private final @Default Supplier<CheckRule> checkRule = () -> new CheckRule(underAttackRule.get());
//    private final @Default Supplier<GameOverChecker> gameOverChecker = () -> new GameOverChecker(underAttackRule.get());
//    private final @Default Supplier<InitialBoardProvider> initialBoardProvider = () -> new InitialBoardProvider(underAttackRule.get());
//    private final @Default Supplier<InitialStateProvider> initialStateProvider = () -> new InitialStateProvider(initialBoardProvider.get());

    public GameFactory() {
        underAttackRule = new UnderAttackRule();
        checkRule = new CheckRule(underAttackRule);
        gameOverChecker = new GameOverChecker(underAttackRule);
        initialBoardProvider = new InitialBoardProvider(underAttackRule);
        initialStateProvider = new InitialStateProvider(initialBoardProvider);
    }

    public Game createTerminalGame() {
        return createGame(new TerminalInput(), new TerminalInput(), new TerminalView());
    }

    public Game createGame(Input white, Input black, View... views) {
        return new Game(initialStateProvider, checkRule, gameOverChecker, white, black, MultipleViewWrapper.wrapIfNeeded(views));
    }
}
