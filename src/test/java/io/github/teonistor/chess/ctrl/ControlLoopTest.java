package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
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

    @Test
    void save(final @Mock GameState state) {
//        setField(loop, "game", game);
//        when(action.gameInput()).thenReturn(Optional.empty());
//        when(action.savePath()).thenReturn(Optional.of("some path"));
//        when(action.gameStateProvider()).thenReturn(Optional.empty());
//        when(game.getState()).thenReturn(state);
//
//        loop.onInput(action);
//
//        verify(saveLoad).saveState(state, "some path");
        assumeTrue(false, "Dependent on SaveLoad refactor");
    }

    @Test
    void saveInAbsenceOfGame() {
//        lenient().when(action.savePath()).thenReturn(Optional.of("some other path"));
//        when(action.gameStateProvider()).thenReturn(Optional.empty());
//        loop.onInput(action);
        assumeTrue(false, "Dependent on SaveLoad refactor");
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

    @Test
    void load() {
        assumeTrue(false, "Dependent on SaveLoad refactor");
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(saveLoad, factory, view, gameInput, game, newGame);
    }
}