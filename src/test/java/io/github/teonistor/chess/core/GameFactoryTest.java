package io.github.teonistor.chess.core;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class GameFactoryTest {

    private static final GameFactory gameFactory = new GameFactory();

    @Test
    void createGame() {
        // The fact that we need to set this global in order for the GameFactoryTest to pass is worrying
        setField(World.class, "gameOverChecker", mock(GameOverChecker.class));
        final GameState state = new GameState(HashMap.empty(), White, List.empty(), null);
        final GameStateProvider stateProvider = () -> state;

        final Game game = gameFactory.createGame(stateProvider);

        assertThat(game.getState()).isEqualTo(state);
    }
}