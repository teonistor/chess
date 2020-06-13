package io.github.teonistor.chess.core;

public enum Player {
    White, Black;

    static {
        Black.next = White;
        White.next = Black;
    }

    private Player next;

    public Player next() {
        return next;
    }
}
