package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.core.World;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class InputActionTest {

    @Test
    void newGame() {
        final GameStateProvider provider = mock(InitialStateProvider.class);
        setField(World.class, "initialStateProvider", provider);

        final InputAction action = InputAction.newGame();
        assertThat(action.gameStateProvider()).contains(provider);
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void loadGame() {
        final GameStateProvider provider = mock(GameStateProvider.class);
        final SaveLoad saveLoad = mock(SaveLoad.class);
        when(saveLoad.doLoadState("file name")).thenReturn(provider);
        setField(World.class, "saveLoad", saveLoad);

        final InputAction action = InputAction.loadGame("file name");
        assertThat(action.gameStateProvider()).contains(provider);
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void gameInput() {

        final InputAction action = InputAction.gameInput(Position.F7, Position.G3);
        assertThat(action.gameStateProvider()).isEmpty();
        assertThat(action.gameInput()).contains(new Tuple2<>(Position.F7, Position.G3));
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void saveGame() {

        final InputAction action = InputAction.saveGame("more name");
        assertThat(action.gameStateProvider()).isEmpty();
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).contains("more name");
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void exit() {
        final InputAction action = InputAction.exit();
        assertThat(action.gameStateProvider()).isEmpty();
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isTrue();
    }
}