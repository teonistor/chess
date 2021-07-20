package io.github.teonistor.chess.core;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force=true)
public enum GameCondition {
    Continue,
    WhiteWins("White wins!"),
    BlackWins("Black wins!"),
    Stalemate("Stalemate!");

    private final String announcement;

    public Option<String> getAnnouncement() {
        return Option.of(announcement);
    }
}
