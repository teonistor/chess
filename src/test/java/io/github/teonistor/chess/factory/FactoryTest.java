package io.github.teonistor.chess.factory;

import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.GameStateKey;
import io.github.teonistor.chess.core.InitialStateProvider;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.inter.View;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.factory.Factory.GameType.PARALLEL;
import static io.github.teonistor.chess.factory.Factory.GameType.STANDARD;
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
        final Object objectMapper = getField(FACTORY, "objectMapper");
        final Object saveLoad = getField(FACTORY, "saveLoad");
        final Object standardAvailableMovesRule = getField(FACTORY, "standardAvailableMovesRule");
        final Object parallelAvailableMovesRule = getField(FACTORY, "parallelAvailableMovesRule");
        final Object positionPairExtractor = getField(FACTORY, "positionPairExtractor");
        final Object promotionRequirementExtractor = getField(FACTORY, "promotionRequirementExtractor");

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
        soft.assertThat(objectMapper).isNotNull();
        soft.assertThat(saveLoad).hasFieldOrPropertyWithValue("objectMapper", objectMapper);
        soft.assertThat(standardAvailableMovesRule).hasFieldOrPropertyWithValue("rule", checkRule);
        soft.assertThat(parallelAvailableMovesRule).hasFieldOrPropertyWithValue("rule", checkRule);
        soft.assertThat(positionPairExtractor).isNotNull();
        soft.assertThat(promotionRequirementExtractor).isNotNull();

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
    void createNewStandardGame(final @Mock InitialStateProvider provider, final @Mock GameState state) {
        final Factory factory = new Factory();
        setField(factory, "initialStateProvider", provider);
        when(provider.createState()).thenReturn(state);

        final Game game = factory.createNewStandardGame();

        assertThat(game).extracting("availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "state", "key").containsExactly(
                getField(factory, "standardAvailableMovesRule"),
                getField(factory, "gameOverChecker"),
                getField(factory, "positionPairExtractor"),
                getField(factory, "promotionRequirementExtractor"),
                STANDARD,
                state,
                GameStateKey.NIL);
    }

    @Test
    void createNewParallelGame(final @Mock InitialStateProvider provider, final @Mock GameState state) {
        final Factory factory = new Factory();
        setField(factory, "initialStateProvider", provider);
        when(provider.createState()).thenReturn(state);

        final Game game = factory.createNewParallelGame();

        assertThat(game).extracting("availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "state", "key").containsExactly(
                getField(factory, "parallelAvailableMovesRule"),
                getField(factory, "gameOverChecker"),
                getField(factory, "positionPairExtractor"),
                getField(factory, "promotionRequirementExtractor"),
                PARALLEL,
                state,
                GameStateKey.NIL);
    }

    @Test
    void createGame(final @Mock GameState state) {
        final Game game = FACTORY.createGame(STANDARD, state);

        assertThat(game).extracting("availableMovesRule", "gameOverChecker", "positionPairExtractor", "promotionRequirementExtractor", "type", "state", "key").containsExactly(
                getField(FACTORY, "standardAvailableMovesRule"),
                getField(FACTORY, "gameOverChecker"),
                getField(FACTORY, "positionPairExtractor"),
                getField(FACTORY, "promotionRequirementExtractor"),
                STANDARD,
                state,
                GameStateKey.NIL);
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