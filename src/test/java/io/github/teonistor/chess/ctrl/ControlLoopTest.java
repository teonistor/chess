package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.Tuple2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static io.github.teonistor.chess.factory.Factory.GameType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@MockitoSettings
class ControlLoopTest implements RandomPositionsTestMixin {
    @Mock private SaveLoad saveLoad;
    @Mock private GameFactory factory;
    @Mock private View view;

    @Mock private InputAction action;
    @Mock private Game game;
    @Mock private Game newGame;

    @InjectMocks private ControlLoop loop;

    @Test
    void onGameInput() {
        setField(loop, "game", game);
        final Position from = randomPositions.get();
        final Position to = randomPositions.get();
        when(action.gameInput()).thenReturn(Optional.of(new Tuple2<>(from, to)));
        // TODO Refactor: The fact that I have to stub the next 2 calls is concerning
        when(action.savePath()).thenReturn(Optional.empty());
        when(action.gameStateProvider()).thenReturn(Optional.empty());
        when(game.processInput(from, to)).thenReturn(newGame);

        loop.onInput(action);

        assertThat(loop).hasFieldOrPropertyWithValue("game", newGame);
        verify(newGame).triggerView(view);
    }

    @Test
    void onGameInputInAbsenceOfGame() {
        lenient().when(action.gameInput()).thenReturn(Optional.of(new Tuple2<>(randomPositions.next(), randomPositions.next())));
        when(action.gameStateProvider()).thenReturn(Optional.empty());
        loop.onInput(action);
    }

    @Test
    void onSave(final @Mock GameState state) {
        setField(loop, "game", game);
        when(action.gameInput()).thenReturn(Optional.empty());
        when(action.savePath()).thenReturn(Optional.of("some path"));
        when(action.gameStateProvider()).thenReturn(Optional.empty());
        when(game.getState()).thenReturn(state);

        loop.onInput(action);

        verify(saveLoad).saveState(state, "some path");
    }

    @Test
    void onSaveInAbsenceOfGame() {
        lenient().when(action.savePath()).thenReturn(Optional.of("some other path"));
        when(action.gameStateProvider()).thenReturn(Optional.empty());
        loop.onInput(action);
    }

    @Test
    void onNew(final @Mock GameState state) {
        when(action.gameStateProvider()).thenReturn(Optional.of(() -> state));
        when(factory.createGame(STANDARD, state)).thenReturn(game);

        loop.onInput(action);

        assertThat(loop).hasFieldOrPropertyWithValue("game", game);
        verify(game).triggerView(view);
    }

    @Test
    void onIrrelevantInput() {
        setField(loop, "game", game);
        when(action.gameInput()).thenReturn(Optional.empty());
        when(action.savePath()).thenReturn(Optional.empty());
        when(action.gameStateProvider()).thenReturn(Optional.empty());

        loop.onInput(action);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(saveLoad, factory, view, action, game, newGame);
    }
}