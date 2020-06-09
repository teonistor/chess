package io.github.teonistor.devschess;

import io.github.teonistor.devschess.inter.TerminalInput;
import io.github.teonistor.devschess.inter.TerminalView;

public class Main {
    // Launch a terminal-based game
    public static void main(String[] arg) {
        new Game(new TerminalInput(), new TerminalInput(), new TerminalView()).play();
    }
}
