package io.github.teonistor.chess.move;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.GameState;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.PieceBox;
import io.github.teonistor.chess.rule.UnderAttackRule;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import static io.github.teonistor.chess.core.GameStateKey.NIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PromotionTest extends MoveTest implements RandomPositionsTestMixin {

    private final Position from = randomPositions.next();
    private final Position to = randomPositions.next();
    private final SingleOutcomeMove delegate = mock(SingleOutcomeMove.class);
    private final PieceBox box = new PieceBox(mock(UnderAttackRule.class));

    @Test
    void delegateFrom() {
        when(delegate.getFrom()).thenReturn(from);
        assertThat(new Promotion(delegate).getFrom()).isEqualTo(from);
    }

    @Test
    void delegateTo() {
        when(delegate.getTo()).thenReturn(to);
        assertThat(new Promotion(delegate).getTo()).isEqualTo(to);
    }

    @Test
    void delegateValidate() {
        final GameState state = stateWith(HashMap.of(from, box.whitePawn));
        when(delegate.validate(state)).thenReturn(true);
        assertThat(new Promotion(delegate).validate(state)).isTrue();
    }

    @Test
    void executeWhite() {
        final GameState state = stateWith(HashMap.of(from, box.whitePawn), Player.White);
        when(delegate.executeSingleOutcome(eq(state), any())).thenAnswer(invocation -> stateWith(HashMap.of(to, invocation.getArgument(1, Piece.class))));

        assertThat(new Promotion(delegate)
            .execute(state))
            .extracting(
                    op -> op._1.apply(NIL).getWhitePromotion(),
                    st -> st._2.getBoard().get(to).get())
            .containsExactlyInAnyOrder(
                    tuple(box.whiteRook, box.whiteRook),
                    tuple(box.whiteBishop, box.whiteBishop),
                    tuple(box.whiteKnight, box.whiteKnight),
                    tuple(box.whiteQueen, box.whiteQueen));
    }

    @Test
    void executeBlack() {
        final GameState state = stateWith(HashMap.of(from, box.whitePawn), Player.Black);
        when(delegate.executeSingleOutcome(eq(state), any())).thenAnswer(invocation -> stateWith(HashMap.of(to, invocation.getArgument(1, Piece.class))));

        assertThat(new Promotion(delegate)
            .execute(state))
            .extracting(
                    op -> op._1.apply(NIL).getBlackPromotion(),
                    st -> st._2.getBoard().get(to).get())
            .containsExactlyInAnyOrder(
                    tuple(box.blackRook, box.blackRook),
                    tuple(box.blackBishop, box.blackBishop),
                    tuple(box.blackKnight, box.blackKnight),
                    tuple(box.blackQueen, box.blackQueen));
    }
}