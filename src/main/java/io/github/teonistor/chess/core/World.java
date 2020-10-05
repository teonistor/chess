package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.save.SaveLoad;

// A (class-based) singleton, lazy dependency injection point
public class World {

    private static UnderAttackRule underAttackRule;
    private static CheckRule checkRule;
    private static GameOverChecker gameOverChecker;
    private static InitialBoardProvider initialBoardProvider;
    private static InitialStateProvider initialStateProvider;
    private static SaveLoad saveLoad;
    private static GameFactory gameFactory;

    public static UnderAttackRule underAttackRule() {
        if (underAttackRule == null)
            underAttackRule = new UnderAttackRule();
        return underAttackRule;
    }

    public static CheckRule checkRule() {
        if (checkRule == null)
            checkRule = new CheckRule(underAttackRule());
        return checkRule;
    }

    public static GameOverChecker gameOverChecker() {
        if (gameOverChecker == null)
            gameOverChecker = new GameOverChecker(underAttackRule());
        return gameOverChecker;
    }

    public static InitialBoardProvider initialBoardProvider() {
        if (initialBoardProvider == null)
            initialBoardProvider = new InitialBoardProvider();
        return initialBoardProvider;
    }

    public static InitialStateProvider initialStateProvider() {
        if (initialStateProvider == null)
            initialStateProvider = new InitialStateProvider();
        return initialStateProvider;
    }

    public static SaveLoad saveLoad() {
        if (saveLoad == null)
            saveLoad = new SaveLoad();
        return saveLoad;
    }

    public static GameFactory gameFactory() {
        if (gameFactory == null)
            gameFactory = new GameFactory();
        return gameFactory;
    }
}
