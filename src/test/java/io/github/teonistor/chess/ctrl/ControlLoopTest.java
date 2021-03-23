package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.factory.GameFactory;
import io.github.teonistor.chess.inter.DefinitelyInput;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.InputEngine;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@MockitoSettings
class ControlLoopTest {
    public static final Iterator<Position> randomPositions = Stream.continually(Stream.of(Position.values()))
            .flatMap(s -> s.splitAt(32).apply(Stream::of))
            .flatMap(Stream::shuffle).iterator();

    @Mock private SaveLoad saveLoad;
    @Mock private GameFactory factory;
    @Mock private View view;

    @Mock private InputAction action;
    @Mock private Game game;
    @Mock private Game newGame;

    @Mock private InputEngine inputEngine;
    @Mock private GameStateProvider providerIn;
    @Mock private DefinitelyInput input;

    private GameStateProvider providerOut;
    private Input inputProxy;

    private ControlLoop loop;

    @BeforeEach
    void setUp() {
        loop = new ControlLoop(saveLoad, factory, view);
    }

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
        when(action.gameStateProvider()).thenReturn(Optional.empty());
        loop.onInput(action);
    }

    // TODO These  -->

    @Test
    void onSave() {

    }

    @Test
    void onSaveInAbsenceOfGame() {

    }

    @Test
    void onNew() {


//        verify(newGame).triggerView(view);
    }

    @Test
    void onIrrelevantInput() {

    }



    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(saveLoad, factory, view, action, game, newGame);
    }
}