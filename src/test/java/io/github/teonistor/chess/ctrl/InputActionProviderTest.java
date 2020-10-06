package io.github.teonistor.chess.ctrl;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameStateProvider;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.save.SaveLoad;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InputActionProviderTest {

    private final GameStateProvider provider = mock(InitialStateProvider.class);
    private final SaveLoad saveLoad = mock(SaveLoad.class);

    @Test
    void newGame() {
        final InputAction action = new InputActionProvider(provider, saveLoad).newGame();
        assertThat(action.gameStateProvider()).contains(provider);
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void loadGame() {
        when(saveLoad.doLoadState("file name")).thenReturn(provider);

        final InputAction action = new InputActionProvider(provider, saveLoad).loadGame("file name");
        assertThat(action.gameStateProvider()).contains(provider);
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void gameInput() {
        final InputAction action = new InputActionProvider(provider, saveLoad).gameInput(Position.F7, Position.G3);
        assertThat(action.gameStateProvider()).isEmpty();
        assertThat(action.gameInput()).contains(new Tuple2<>(Position.F7, Position.G3));
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void saveGame() {

        final InputAction action = new InputActionProvider(provider, saveLoad).saveGame("more name");
        assertThat(action.gameStateProvider()).isEmpty();
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).contains("more name");
        assertThat(action.isExit()).isFalse();
    }

    @Test
    void exit() {
        final InputAction action = new InputActionProvider(provider, saveLoad).exit();
        assertThat(action.gameStateProvider()).isEmpty();
        assertThat(action.gameInput()).isEmpty();
        assertThat(action.savePath()).isEmpty();
        assertThat(action.isExit()).isTrue();
    }
}