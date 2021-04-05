package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.github.teonistor.chess.piece.Rook;
import io.github.teonistor.chess.testmixin.RandomPositionsTestMixin;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class PawnPromotionPartialStateTest implements RandomPositionsTestMixin {

    private @Mock GameState prePromotionState;
    private @Mock Piece piece1;
    private @Mock Piece piece2;
    private final Position position1 = randomPositions.next();
    private final Position position2 = randomPositions.next();
    private Map<Position, Piece> prePromotionBoard;

    @BeforeEach
    void setUp() {
        prePromotionBoard = HashMap.of(position1, piece1, position2, piece2);
        when(prePromotionState.getBoard()).thenReturn(prePromotionBoard);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void triggerViewIfNeeded(final Player player, final @Mock View view) {
        new PawnPromotionPartialState(prePromotionState, position1, player).triggerViewIfNeeded().get().accept(view);
        // TODO Actual method in the View interface
        verify(view).announce("Someone selects piece");
    }

    @Test
    void withPromotionInput() {
        final GameState postPromotionState = new PawnPromotionPartialState(prePromotionState, position1, Player.White).withPromotionInput(new Rook(Player.White));

        assertThat(postPromotionState)
                .extracting(GameState::getBoard, GameState::getPlayer, GameState::getCapturedPieces)
                ;//.containsExactly(Hashma)
    }

    @Test
    void getBoard() {
    }

    @AfterEach
    void tearDown() {
    }
}