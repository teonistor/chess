package io.github.teonistor.chess.core;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.ReflectionTestUtils.getField;

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

    // TODO Reinstate. Current problem: createGame() takes user input in stateProvision() and holds up the test forever!
    //   and I thought we were done with that construct
    /*@Test
    void createTerminalGame() {
        final Game game = FACTORY.createTerminalGame();

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

        final Game game = FACTORY.createGame(inputOne, inputTwo, viewOne, viewTwo, viewThree);

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