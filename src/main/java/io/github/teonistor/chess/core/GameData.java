package io.github.teonistor.chess.core;

import io.github.teonistor.chess.factory.Factory.GameType;
import lombok.Value;

@Value
public class GameData {
    GameType type;
    GameState state;
}
