package io.github.teonistor.chess.rule;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.C3;
import static io.github.teonistor.chess.board.Position.C4;
import static io.github.teonistor.chess.board.Position.C5;
import static io.github.teonistor.chess.board.Position.C6;
import static io.github.teonistor.chess.board.Position.C7;
import static io.github.teonistor.chess.board.Position.D1;
import static io.github.teonistor.chess.board.Position.D4;
import static io.github.teonistor.chess.board.Position.D5;
import static io.github.teonistor.chess.board.Position.E4;
import static io.github.teonistor.chess.board.Position.E5;
import static io.github.teonistor.chess.board.Position.E6;
import static io.github.teonistor.chess.board.Position.E7;
import static io.github.teonistor.chess.board.Position.F3;
import static io.github.teonistor.chess.board.Position.F5;
import static io.github.teonistor.chess.board.Position.F7;
import static io.github.teonistor.chess.board.Position.G2;
import static io.github.teonistor.chess.board.Position.G5;
import static io.github.teonistor.chess.board.Position.H5;
import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;


class UnderAttackRuleTest {

    // In practice, one instance will be used throughout; the test reflects that (it may also be faster not to build all those nested maps every time)
    private static final UnderAttackRule underAttackRule = new UnderAttackRule();
    private static final PieceBox box = new PieceBox(underAttackRule);

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
                HashMap.of(C5, box.whiteRook),
                HashMap.of(D1, box.whiteRook),
                HashMap.of(F5, box.whiteRook, H5, box.blackRook),
                HashMap.of(F5, box.whiteRook, G5, box.blackQueen),
                HashMap.of(C7, box.whiteKnight, C6, box.blackKnight),
                HashMap.of(C3, box.whiteKnight)
        ).map(Arguments::of);
    }
    private static Stream<Arguments> d5UnderAttackByBlackOnly() {
        return Stream.of(
                HashMap.of(E6, box.blackQueen),
                HashMap.of(C6, box.blackPawn),
                HashMap.of(E7, box.blackKnight, D4, box.whitePawn)
        ).map(Arguments::of);
    }
    private static Stream<Arguments> d5UnderAttackByBoth() {
        return Stream.of(
                HashMap.of(C5, box.whiteRook, E6, box.blackQueen),
                HashMap.of(E5, box.blackQueen, C4, box.whiteBishop)
        ).map(Arguments::of);
    }
    private static Stream<Arguments> d5UnderAttackByNeither() {
        return Stream.of(
                HashMap.of(C6, box.blackKnight),
                HashMap.of(E4, box.whiteKnight, F3, box.whiteQueen),
                HashMap.of(E4, box.whiteKnight, G2, box.blackQueen),
                HashMap.of(E4, box.whiteRook, G2, box.whiteBishop),
                HashMap.of(E6, box.blackRook, F7, box.whiteBishop)
        ).map(Arguments::of);
    }
}