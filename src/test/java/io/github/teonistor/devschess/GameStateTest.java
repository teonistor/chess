package io.github.teonistor.devschess;

import io.github.teonistor.devschess.board.Position;
import io.github.teonistor.devschess.piece.Piece;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.github.teonistor.devschess.Player.Black;
import static io.github.teonistor.devschess.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

class GameStateTest {

    private @Mock Map<Position,Piece> initialBoard;
    private @Mock Map<Position,Piece> finalBoard;
    private @Mock Piece aPiece;
    private @Mock Piece anotherPiece;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void advanceBoardWhite() {
        final GameState initialState = new GameState(initialBoard, White, HashSet.empty());
        final GameState finalState = initialState.advance(finalBoard);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(Black);
        assertThat(finalState.getCapturedPieces()).isEmpty();
    }

    @Test
    void advanceBoardBlack() {
        final GameState initialState = new GameState(initialBoard, Black, HashSet.of(aPiece));
        final GameState finalState = initialState.advance(finalBoard);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(White);
        assertThat(finalState.getCapturedPieces()).containsExactly(aPiece);
    }

    @Test
    void advanceBoardAndCaptureWhite() {
        final GameState initialState = new GameState(initialBoard, White, HashSet.empty());
        final GameState finalState = initialState.advance(finalBoard, aPiece);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(Black);
        assertThat(finalState.getCapturedPieces()).containsExactly(aPiece);
    }

    @Test
    void advanceBoardAndCaptureBlack() {
        final GameState initialState = new GameState(initialBoard, Black, HashSet.of(aPiece));
        final GameState finalState = initialState.advance(finalBoard, anotherPiece);

        assertThat(finalState.getBoard()).isEqualTo(finalBoard);
        assertThat(finalState.getPlayer()).isEqualTo(White);
        assertThat(finalState.getCapturedPieces()).containsExactlyInAnyOrder(aPiece, anotherPiece);
    }
}
