package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.King;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CheckRuleTest implements Move {

    private final UnderAttackRule rule = mock(UnderAttackRule.class);
    private King king;

    @ParameterizedTest
    @EnumSource(Player.class)
    void failIfNoKing(Player player) {
        king = new King(player);

        assertThatIllegalArgumentException().isThrownBy(()-> new CheckRule(rule).validateMove(HashMap.empty(), player, this))
                .withMessage("There should be exactly one %s King but found 0", player);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void failIfMultipleKing(Player player) {
        king = new King(player);

        assertThatIllegalArgumentException()
                .isThrownBy(()-> new CheckRule(rule).validateMove(HashMap.of(Position.A2, king, Position.F1, king, Position.G6, king), player, this))
                .withMessage("There should be exactly one %s King but found 3", player);
    }

    @ParameterizedTest
    @CsvSource({"White,A2","White,G3","White,F1","White,C8","Black,H7","Black,B4","Black,E5","Black,D6"})
    void validateValid(Player player, Position position) {
        king = new King(player);
        final HashMap<Position,Piece> board = HashMap.of(position, king);
        when(rule.checkAttack(board, position, player)).thenReturn(false);

        assertThat(new CheckRule(rule).validateMove(board, player, this)).isTrue();

        verify(rule).checkAttack(board, position, player);
    }

    @ParameterizedTest
    @CsvSource({"Black,A2","Black,G3","Black,F1","Black,C8","White,H7","White,B4","White,E5","White,D6"})
    void validateInvalid(Player player, Position position) {
        king = new King(player);
        final HashMap<Position,Piece> board = HashMap.of(position, king);
        when(rule.checkAttack(board, position, player)).thenReturn(true);

        assertThat(new CheckRule(rule).validateMove(board, player, this)).isFalse();

        verify(rule).checkAttack(board, position, player);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(Map<Position, Piece> board, Function<Map<Position, Piece>, T> nonCapturingCallback, BiFunction<Map<Position, Piece>, Piece, T> capturingCallback) {
        assertThat(nonCapturingCallback.apply(board)).isSameAs(board);
        assertThat(capturingCallback.apply(board, king)).isSameAs(board);
        return (T) board;
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rule);
    }


    @Override
    public Position getFrom() {
        throw new AssertionError("This should never be called");
    }

    @Override
    public Position getTo() {
        throw new AssertionError("This should never be called");
    }

    @Override
    public boolean validate(Map<Position, Piece> board) {
        throw new AssertionError("This should never be called");
    }
}
