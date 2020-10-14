package io.github.teonistor.chess.core;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.ReflectionTestUtils.getField;

class GameFactoryTest {

    private static final GameFactory gameFactory = new GameFactory();

    @Test
    void constructorInitialisedFields() {
        final SoftAssertions soft = new SoftAssertions();
        final Object underAttackRule = getField(gameFactory, "underAttackRule");
        final Object checkRule = getField(gameFactory, "checkRule");
        final Object gameOverChecker = getField(gameFactory, "gameOverChecker");
        final Object pieceBox = getField(gameFactory, "pieceBox");
        final Object initialBoardProvider = getField(gameFactory, "initialBoardProvider");
        final Object initialStateProvider = getField(gameFactory, "initialStateProvider");

        soft.assertThat(underAttackRule).isNotNull();
        soft.assertThat(checkRule).isNotNull();
        soft.assertThat(gameOverChecker).isNotNull();
        soft.assertThat(initialBoardProvider).isNotNull();
        soft.assertThat(initialStateProvider).isNotNull();
        soft.assertThat(getField(checkRule, "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getField(gameOverChecker, "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getField(initialBoardProvider, "box")).isEqualTo(pieceBox);
        soft.assertThat(getFieldRec(pieceBox, "whiteKing", "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getFieldRec(pieceBox, "blackKing", "underAttackRule")).isEqualTo(underAttackRule);
        soft.assertThat(getField(initialStateProvider, "initialBoardProvider")).isEqualTo(initialBoardProvider);

        soft.assertAll();
    }

    // TODO Reinstate. Current problem: Game takes user input in constructor and holds up the test forever!
    /*@Test
    void createTerminalGame() {
        final Game game = gameFactory.createTerminalGame();

        assertThat((Input[]) getField(game, "inputs")).allSatisfy(input -> assertThat(input).isInstanceOf(TerminalInput.class));
        assertThat(getField(game, "view")).isInstanceOf(TerminalView.class);
    }

    @Test
    void createGame() {
        final Input inputOne = mock(Input.class);
        final Input inputTwo = mock(Input.class);
        final View viewOne = mock(View.class);
        final View viewTwo = mock(View.class);
        final View viewThree = mock(View.class);

        final Game game = gameFactory.createGame(inputOne, inputTwo, viewOne, viewTwo, viewThree);

        assertThat((Input[]) getField(game, "inputs")).containsExactly(inputOne, inputTwo);
        assertThat((Iterable) getField(getField(game, "view"), "views")).containsExactlyInAnyOrder(viewOne, viewTwo, viewThree);
    }*/

    private Object getFieldRec(Object object, String...names) {
        return getFieldRec(object, 0, names);
    }

    private Object getFieldRec(Object object, int i, String...names) {
        return i == names.length
             ? object
             : getFieldRec(getField(object, names[i]), i+1, names);
    }
}