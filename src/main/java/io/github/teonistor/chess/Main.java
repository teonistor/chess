package io.github.teonistor.chess;

import io.github.teonistor.chess.board.InitialBoardProvider;
import io.github.teonistor.chess.core.CheckRule;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameOverChecker;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        final UnderAttackRule underAttackRule = new UnderAttackRule();
        final CheckRule checkRule = new CheckRule(underAttackRule);
        final GameOverChecker gameOverChecker = new GameOverChecker();
        final InitialBoardProvider initialBoardProvider = new InitialBoardProvider(underAttackRule);
        final InitialStateProvider initialStateProvider = new InitialStateProvider(initialBoardProvider);

        new Game(initialStateProvider, checkRule, gameOverChecker, new TerminalInput(), new TerminalInput(), new TerminalView()).play();
    }
}
