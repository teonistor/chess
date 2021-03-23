package io.github.teonistor.chess.core;

import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.inter.View;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@MockitoSettings
class FactoryTest {

    private static final Factory FACTORY = new Factory();

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
        final Object saveLoad = getField(FACTORY, "saveLoad");
        final Object inputActionProvider = getField(FACTORY, "inputActionProvider");
        final Object availableMovesRule = getField(FACTORY, "availableMovesRule");
        final Object nestedMapKeyExtractor = getField(FACTORY, "nestedMapKeyExtractor");

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
        // TODO How to test arbitrarily long constructor chains
//        soft.assertThat(pieceSerialiser).hasFieldOrPropertyWithValue("pieceBox", pieceBox);
        soft.assertThat(pieceSerialiser).isNotNull();
//        soft.assertThat(saveLoad).hasFieldOrPropertyWithValue("pieceSerialiser", pieceSerialiser);
        soft.assertThat(inputActionProvider).hasFieldOrPropertyWithValue("initialStateProvider", initialStateProvider);
        soft.assertThat(inputActionProvider).hasFieldOrPropertyWithValue("saveLoad", saveLoad);
        soft.assertThat(availableMovesRule).hasFieldOrPropertyWithValue("rule", checkRule);
        soft.assertThat(nestedMapKeyExtractor).isNotNull();

        soft.assertAll();
    }

    @Test
    void createControlLoop(final @Mock View view) {
        final ControlLoop loop = FACTORY.createControlLoop(view);

        assertThat(loop).hasFieldOrPropertyWithValue("saveLoad", getField(FACTORY, "saveLoad"));
        assertThat(loop).hasFieldOrPropertyWithValue("gameFactory", FACTORY);
        assertThat(loop).hasFieldOrPropertyWithValue("view", view);
    }

    @Test
    void createNewGame(final @Mock View view, final @Mock InitialStateProvider provider, final @Mock GameState state) {
        final Factory factory = new Factory();
        setField(factory, "initialStateProvider", provider);
        when(provider.createState()).thenReturn(state);

        final Game game = factory.createNewGame();

        assertThat(game).extracting("availableMovesRule", "gameOverChecker", "nestedMapKeyExtractor", "view", "state").containsExactly(
                getField(factory, "availableMovesRule"),
                getField(factory, "gameOverChecker"),
                getField(factory, "nestedMapKeyExtractor"),
                view,
                state);
    }

    @Test
    void createGame(final @Mock View view, final @Mock GameState state) {
        final Game game = FACTORY.createGame(state);

        assertThat(game).extracting("availableMovesRule", "gameOverChecker", "nestedMapKeyExtractor", "view", "state").containsExactly(
                getField(FACTORY, "availableMovesRule"),
                getField(FACTORY, "gameOverChecker"),
                getField(FACTORY, "nestedMapKeyExtractor"),
                view,
                state);
    }

    // TODO Looks like a mixin opportunity
    private Object getFieldRec(final Object object, final String... names) {
        return getFieldRec(object, 0, names);
    }

    private Object getFieldRec(final Object object, final int i, final String... names) {
        return i == names.length
             ? object
             : getFieldRec(getField(object, names[i]), i+1, names);
    }
}