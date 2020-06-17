package io.github.teonistor.chess.piece;

import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.move.Castle;
import io.github.teonistor.chess.move.KingsFirstMove;
import org.junit.jupiter.api.Test;
import static io.github.teonistor.chess.board.Position.*;
import static io.github.teonistor.chess.core.Player.*;
import static org.mockito.Mockito.mock;


class UnmovedKingTest extends PieceTest {

    @Test
    void computePossibleMovesWhite() {
        init(new UnmovedKing(White, mock(UnderAttackRule.class)));

        assertMove(E1, KingsFirstMove.class,D1,D2,E2,F2,F1, Castle.class,C1,G1);
        assertj.assertAll();
    }

    @Test
    void computePossibleMovesBlack() {
        init(new UnmovedKing(Black, mock(UnderAttackRule.class)));

        assertMove(E8, KingsFirstMove.class,D8,D7,E7,F7,F8, Castle.class,C8,G8);
        assertj.assertAll();
    }
}