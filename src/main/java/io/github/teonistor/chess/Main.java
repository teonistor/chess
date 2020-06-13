package io.github.teonistor.chess;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        new Game(new TerminalInput(), new TerminalInput(), new TerminalView()).play();
    }
}
