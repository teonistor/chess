package io.github.teonistor.chess.core;

import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.inter.DefinitelyInput;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.springframework.test.util.ReflectionTestUtils.getField;

class FactoryTest {

    private static final Factory FACTORY = spy(new Factory());

    @Test
    void constructorInitialisedFields() {
        final SoftAssertions soft = new SoftAssertions();
        final Object underAttackRule = getField(FACTORY, "underAttackRule");
        final Object checkRule = getField(FACTORY, "checkRule");
        final Object gameOverChecker = getField(FACTORY, "gameOverChecker");
        final Object pieceBox = getField(FACTORY, "pieceBox");
        final Object initialBoardProvider = getField(FACTORY, "initialBoardProvider");
        final Object initialStateProvider = getField(FACTORY, "initialStateProvider");
        final Object pieceSerialiser = getField(FACTORY, "pieceSerialiser");

        soft.assertThat(underAttackRule).isNotNull();
        soft.assertThat(checkRule).isNotNull();
        soft.assertThat(gameOverChecker).isNotNull();
        soft.assertThat(initialBoardProvider).isNotNull();
        soft.assertThat(initialStateProvider).isNotNull();
        soft.assertThat(getField(checkRule, "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getField(gameOverChecker, "checkRule")).isEqualTo(checkRule);
        soft.assertThat(getField(initialBoardProvider, "box")).isEqualTo(pieceBox);
        soft.assertThat(getFieldRec(pieceBox, "whiteKing", "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getFieldRec(pieceBox, "blackKing", "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getField(initialStateProvider, "initialBoardProvider")).isEqualTo(initialBoardProvider);
        // Actions in constructors render these untestable
//        soft.assertThat(pieceSerialiser).hasFieldOrPropertyWithValue("pieceBox", pieceBox);
//        soft.assertThat(getField(FACTORY, "saveLoad")).hasFieldOrPropertyWithValue("pieceSerialiser", pieceSerialiser);

        soft.assertAll();
    }

    @Test
    void createTerminalControlLoop() {
        final GameFactory gameFactory = mock(GameFactory.class);
        final DefinitelyInput input = mock(DefinitelyInput.class);
        final ArgumentCaptor<View> view = ArgumentCaptor.forClass(View.class);
        doReturn(gameFactory).when(FACTORY).createGameFactory(view.capture());
        doReturn(input).when(FACTORY).createTerminalInput();

        final ControlLoop loop = FACTORY.createTerminalControlLoop();

        assertThat(loop).hasFieldOrPropertyWithValue("saveLoad", getField(FACTORY, "saveLoad"));
        assertThat(loop).hasFieldOrPropertyWithValue("gameFactory", gameFactory);
        assertThat(loop).hasFieldOrPropertyWithValue("input", input);
        assertThat(getField(loop, "executorService")).isInstanceOf(ThreadPoolExecutor.class);
        assertThat(view.getValue()).isInstanceOf(TerminalView.class);

        reset(FACTORY);
    }

    @Test
    void createGame() {
        final GameStateProvider provider = mock(GameStateProvider.class);
        final Input input = mock(Input.class);
        final View viewOne = mock(View.class);
        final View viewTwo = mock(View.class);
        final View viewThree = mock(View.class);

        final Game game = FACTORY.createGameFactory(viewOne, viewTwo, viewThree).create(provider, input);

        assertThat(game).hasFieldOrPropertyWithValue("gameStateProvider", provider);
        assertThat(game).hasFieldOrPropertyWithValue("checkRule", getField(FACTORY, "checkRule"));
        assertThat(game).hasFieldOrPropertyWithValue("gameOverChecker", getField(FACTORY, "gameOverChecker"));
        assertThat(game).hasFieldOrPropertyWithValue("input", input);
        assertThat((Iterable) getFieldRec(game, "view", "views")).containsExactlyInAnyOrder(viewOne, viewTwo, viewThree);
    }

    @Test
    void createTerminalInputFactory() {
        final Consumer<InputAction> inputActionConsumer = mock(Consumer.class);

        final DefinitelyInput input = FACTORY.createTerminalInput();

        assertThat(input).isInstanceOf(TerminalInput.class);
        assertThat(input).hasFieldOrPropertyWithValue("inputActionProvider", getField(FACTORY, "inputActionProvider"));
//        assertThat(input).hasFieldOrPropertyWithValue("inputActionConsumer", inputActionConsumer);
    }

    private Object getFieldRec(final Object object, final String...names) {
        return getFieldRec(object, 0, names);
    }

    private Object getFieldRec(final Object object, final int i, final String...names) {
        return i == names.length
             ? object
             : getFieldRec(getField(object, names[i]), i+1, names);
    }
}