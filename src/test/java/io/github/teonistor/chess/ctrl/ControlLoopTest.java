package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameData;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.factory.Factory.GameType;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@MockitoSettings
class ControlLoopTest implements RandomPositionsTestMixin {
    @Mock private SaveLoad saveLoad;
    @Mock private GameFactory factory;
    @Mock private View view;

    @Mock private GameInput gameInput;
    @Mock private Game game;
    @Mock private Game newGame;

    @InjectMocks private ControlLoop loop;

    @Test
    void gameInput() {
        setField(loop, "game", game);
        when(gameInput.execute(game)).thenReturn(newGame);

        loop.gameInput(gameInput);

        assertThat(loop).hasFieldOrPropertyWithValue("game", newGame);
        verify(newGame).triggerView(view);
    }

    @Test
    void gameInputInAbsenceOfGame() {
        loop.gameInput(gameInput);
        assertThat(loop).hasFieldOrPropertyWithValue("game", null);
    }

    @ParameterizedTest
    @EnumSource(GameType.class)
    void save(final GameType type, final @Mock GameState state, final @Mock OutputStream outputStream) {
        setField(loop, "game", game);
        when(game.getType()).thenReturn(type);
        when(game.getState()).thenReturn(state);

        loop.saveGame(outputStream);

        verify(saveLoad).save(new GameData(type, state), outputStream);
    }

    @Test
    void saveInAbsenceOfGame(final @Mock OutputStream outputStream) {
        assertThatIllegalStateException().isThrownBy(() -> loop.saveGame(outputStream))
                .withMessage("No game in progress");
    }

    @Test
    void newStandardGame() {
        when(factory.createNewStandardGame()).thenReturn(game);

        loop.newStandardGame();

        assertThat(loop).hasFieldOrPropertyWithValue("game", game);
        verify(game).triggerView(view);
    }

    @Test
    void newParallelGame() {
        when(factory.createNewParallelGame()).thenReturn(game);

        loop.newParallelGame();

        assertThat(loop).hasFieldOrPropertyWithValue("game", game);
        verify(game).triggerView(view);
    }

    @ParameterizedTest
    @EnumSource(GameType.class)
    void load(final GameType type, final @Mock GameState state, final @Mock InputStream inputStream) {
        when(saveLoad.load(inputStream)).thenReturn(new GameData(type, state));
        when(factory.createGame(type, state)).thenReturn(game);

        loop.loadGame(inputStream);

        assertThat(loop).hasFieldOrPropertyWithValue("game", game);
        verify(game).triggerView(view);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(saveLoad, factory, view, gameInput, game, newGame);
    }
}