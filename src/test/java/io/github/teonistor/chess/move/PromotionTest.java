package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.core.UnderAttackRule;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.core.GameStateKey.NIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;

class PromotionTest extends MoveTest implements RandomPositionsTestMixin {

    private final Position from = randomPositions.next();
    private final Position to = randomPositions.next();
    private final PieceBox box = new PieceBox(mock(UnderAttackRule.class));

    @Test
    void validateValid() {
        assertThat(new Promotion(from, to).validate(stateWith(HashMap.of(from, box.whitePawn)))).isTrue();
    }

    @Test
    void validateInvalid() {
        assertThat(new Promotion(from, to).validate(stateWith(HashMap.of(to, box.whiteRook)))).isFalse();
    }

    @Test
    void executeWhite() {
        assertThat(new Promotion(from, to)
            .execute(stateWith(HashMap.of(from, box.whitePawn), Player.White)))
            .extracting(
                    op -> op._1.apply(NIL).getWhitePromotion(),
                    st -> st._2.getBoard().get(from),
                    st -> st._2.getBoard().get(to).get())
            .containsExactlyInAnyOrder(
                    tuple(box.whiteRook, Option.none(), box.whiteRook),
                    tuple(box.whiteBishop, Option.none(), box.whiteBishop),
                    tuple(box.whiteKnight, Option.none(), box.whiteKnight),
                    tuple(box.whiteQueen, Option.none(), box.whiteQueen));
    }

    @Test
    void executeBlack() {
        assertThat(new Promotion(from, to)
            .execute(stateWith(HashMap.of(from, box.whitePawn), Player.Black)))
            .extracting(
                    op -> op._1.apply(NIL).getBlackPromotion(),
                    st -> st._2.getBoard().get(from),
                    st -> st._2.getBoard().get(to).get())
            .containsExactlyInAnyOrder(
                    tuple(box.blackRook, Option.none(), box.blackRook),
                    tuple(box.blackBishop, Option.none(), box.blackBishop),
                    tuple(box.blackKnight, Option.none(), box.blackKnight),
                    tuple(box.blackQueen, Option.none(), box.blackQueen));
    }
}