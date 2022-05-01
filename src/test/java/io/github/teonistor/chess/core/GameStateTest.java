package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static io.github.teonistor.chess.core.Player.Black;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class GameStateTest {

    private @Mock Map<Position,Piece> initialBoard;
    private @Mock Map<Position,Piece> finalBoard;
    private @Mock Piece aPiece;
    private @Mock Piece anotherPiece;

    @Test
    void advanceBoardWhite() {
        final GameState initialState = new GameState(initialBoard, White, List.empty(), null);
        final GameState finalState = initialState.advance(finalBoard);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(Black);
        assertThat(finalState.getCapturedPieces()).isEmpty();
        assertThat(finalState.getPrevious()).isSameAs(initialState);
    }

    @Test
    void advanceBoardBlack() {
        final GameState initialState = new GameState(initialBoard, Black, List.of(aPiece), null);
        final GameState finalState = initialState.advance(finalBoard);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(White);
        assertThat(finalState.getCapturedPieces()).containsExactly(aPiece);
        assertThat(finalState.getPrevious()).isSameAs(initialState);
    }

    @Test
    void advanceBoardAndCaptureWhite() {
        final GameState initialState = new GameState(initialBoard, White, List.empty(), null);
        final GameState finalState = initialState.advance(finalBoard, aPiece);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(Black);
        assertThat(finalState.getCapturedPieces()).containsExactly(aPiece);
        assertThat(finalState.getPrevious()).isSameAs(initialState);
    }

    @Test
    void advanceBoardAndCaptureBlack() {
        final GameState initialState = new GameState(initialBoard, Black, List.of(aPiece), null);
        final GameState finalState = initialState.advance(finalBoard, anotherPiece);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(White);
        assertThat(finalState.getCapturedPieces()).containsExactlyInAnyOrder(aPiece, anotherPiece);
        assertThat(finalState.getPrevious()).isSameAs(initialState);
    }
}
