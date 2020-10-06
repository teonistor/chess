package io.github.teonistor.chess.core;

import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class GameFactoryTest {

    private static final GameFactory gameFactory = new GameFactory();

    @Test
    void createGame() {
        final GameStateProvider stateProvider = mock(GameStateProvider.class);
        final CheckRule checkRule = mock(CheckRule.class);
        final GameOverChecker gameOverChecker = mock(GameOverChecker.class);
        final Input inputOne = mock(Input.class);
        final Input inputTwo = mock(Input.class);
        final View viewOne = mock(View.class);
        final View viewTwo = mock(View.class);
        final View viewThree = mock(View.class);

        setField(World.class, "checkRule", checkRule);
        setField(World.class, "gameOverChecker", gameOverChecker);

        final Game game = gameFactory.createGame(stateProvider, inputOne, inputTwo, viewOne, viewTwo, viewThree);

        assertThat(getField(game, "gameStateProvider")).isEqualTo(stateProvider);
        assertThat(getField(game, "gameOverChecker")).isEqualTo(gameOverChecker);
        assertThat(getField(game, "checkRule")).isEqualTo(checkRule);
        assertThat((Input[]) getField(game, "inputs")).containsExactly(inputOne, inputTwo);
        assertThat((Iterable) getField(getField(game, "view"), "views")).containsExactlyInAnyOrder(viewOne, viewTwo, viewThree);
    }
}