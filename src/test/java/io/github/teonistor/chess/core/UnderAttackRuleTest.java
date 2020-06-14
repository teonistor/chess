package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.*;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.*;
import static io.github.teonistor.chess.core.Player.*;
import static org.assertj.core.api.Assertions.assertThat;

class UnderAttackRuleTest {

    // In practice, one instance will be used throughout; the test reflects that (it may also be faster not to build all those nested maps every time)
    private static final UnderAttackRule underAttackRule = new UnderAttackRule();

    @ParameterizedTest
    @MethodSource({"d5UnderAttackByWhiteOnly", "d5UnderAttackByBoth"})
    void blackUnderAttackByWhite(Map<Position,Piece> board) {
        assertThat(underAttackRule.checkAttack(board, D5, Black)).isTrue();
    }

    @ParameterizedTest
    @MethodSource({"d5UnderAttackByBlackOnly", "d5UnderAttackByBoth"})
    void whiteUnderAttackByBlack(Map<Position,Piece> board) {
        assertThat(underAttackRule.checkAttack(board, D5, White)).isTrue();
    }

    @ParameterizedTest
    @MethodSource({"d5UnderAttackByBlackOnly", "d5UnderAttackByNeither"})
    void blackNotUnderAttackByWhite(Map<Position,Piece> board) {
        assertThat(underAttackRule.checkAttack(board, D5, Black)).isFalse();
    }

    @ParameterizedTest
    @MethodSource({"d5UnderAttackByWhiteOnly", "d5UnderAttackByNeither"})
    void whiteNotUnderAttackByBlack(Map<Position,Piece> board) {
        assertThat(underAttackRule.checkAttack(board, D5, White)).isFalse();
    }

    // TODO Many more examples below

    private static Stream<Arguments> d5UnderAttackByWhiteOnly() {
        return Stream.of(
                HashMap.of(C5, new Rook(White)),
                HashMap.of(D1, new Rook(White)),
                HashMap.of(F5, new Rook(White), H5, new Rook(Black)),
                HashMap.of(F5, new Rook(White), G5, new Queen(Black)),
                HashMap.of(C7, new Knight(White), C6, new Knight(Black)),
                HashMap.of(C3, new Knight(White))
        ).map(Arguments::of);
    }
    private static Stream<Arguments> d5UnderAttackByBlackOnly() {
        return Stream.of(
                HashMap.of(E6, new Queen(Black)),
                HashMap.of(C6, new Pawn(Black)),
                HashMap.of(E7, new Knight(Black), D4, new Pawn(White))
        ).map(Arguments::of);
    }
    private static Stream<Arguments> d5UnderAttackByBoth() {
        return Stream.of(
                HashMap.of(C5, new Rook(White), E6, new Queen(Black)),
                HashMap.of(E5, new Queen(Black), C4, new Bishop(White))
        ).map(Arguments::of);
    }
    private static Stream<Arguments> d5UnderAttackByNeither() {
        return Stream.of(
                HashMap.of(C6, new Knight(Black)),
                HashMap.of(E4, new Knight(White), F3, new Queen(White)),
                HashMap.of(E4, new Knight(White), G2, new Queen(Black)),
                HashMap.of(E4, new Rook(White), G2, new Bishop(White)),
                HashMap.of(E6, new Rook(Black), F7, new Bishop(White))
        ).map(Arguments::of);
    }
}