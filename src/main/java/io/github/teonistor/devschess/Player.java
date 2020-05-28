package io.github.teonistor.devschess;

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
