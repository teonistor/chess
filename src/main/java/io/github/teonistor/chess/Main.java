package io.github.teonistor.chess;

import io.github.teonistor.chess.core.GameFactory;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        new GameFactory().createTerminalGame().play();
    }
}
