package io.github.teonistor.chess;

import io.github.teonistor.chess.cmd.TerminalLoop;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        final TerminalLoop app = new TerminalLoop();
        app.start();
    }
}
