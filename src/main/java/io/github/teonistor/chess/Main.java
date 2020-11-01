package io.github.teonistor.chess;

import io.github.teonistor.chess.core.Factory;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        new Factory().createTerminalControlLoop().launch();
    }
}
