package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.King;
import io.vavr.collection.HashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckRuleTest {

    private final UnderAttackRule rule = mock(UnderAttackRule.class);
    private King king;

    @ParameterizedTest
    @EnumSource(Player.class)
    void failIfNoKing(Player player) {
        king = new King(player, rule);

        assertThatIllegalArgumentException().isThrownBy(()-> new CheckRule(rule).validate(HashMap.empty(), player))
                .withMessage("There should be exactly one %s King but found 0", player);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void failIfMultipleKing(Player player) {
        king = new King(player, rule);

        assertThatIllegalArgumentException()
                .isThrownBy(()-> new CheckRule(rule).validate(HashMap.of(Position.A2, king, Position.F1, king, Position.G6, king), player))
                .withMessage("There should be exactly one %s King but found 3", player);
    }

    @ParameterizedTest
    @CsvSource({"White,A2","White,G3","White,F1","White,C8","Black,H7","Black,B4","Black,E5","Black,D6"})
    void validateValid(Player player, Position position) {
        king = new King(player, rule);
        final HashMap<Position,Piece> board = HashMap.of(position, king);
        when(rule.checkAttack(board, position, player)).thenReturn(false);

        assertThat(new CheckRule(rule).validate(board, player)).isTrue();

        verify(rule).checkAttack(board, position, player);
    }

    @ParameterizedTest
    @CsvSource({"Black,A2","Black,G3","Black,F1","Black,C8","White,H7","White,B4","White,E5","White,D6"})
    void validateInvalid(Player player, Position position) {
        king = new King(player, rule);
        final HashMap<Position,Piece> board = HashMap.of(position, king);
        when(rule.checkAttack(board, position, player)).thenReturn(true);

        assertThat(new CheckRule(rule).validate(board, player)).isFalse();

        verify(rule).checkAttack(board, position, player);
    }
}
