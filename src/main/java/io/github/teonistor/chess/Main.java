package io.github.teonistor.chess;

import io.github.teonistor.chess.board.Board;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        final UnderAttackRule underAttackRule = new UnderAttackRule();
        final Board board = new Board(underAttackRule);
        final InitialStateProvider initialStateProvider = new InitialStateProvider(board);

        new Game(initialStateProvider, underAttackRule, new TerminalInput(), new TerminalInput(), new TerminalView()).play();
    }
}
