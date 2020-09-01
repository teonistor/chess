package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

import java.util.function.Function;
import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A2;
import static io.github.teonistor.chess.board.Position.A3;
import static io.github.teonistor.chess.board.Position.B4;
import static io.github.teonistor.chess.board.Position.B5;
import static io.github.teonistor.chess.board.Position.D8;
import static io.github.teonistor.chess.board.Position.E5;
import static io.github.teonistor.chess.board.Position.H6;
import static io.github.teonistor.chess.board.Position.OutOfBoard;
import static io.github.teonistor.chess.core.GameCondition.Continue;
import static io.github.teonistor.chess.core.Player.White;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GameTest {

    private final InitialStateProvider provider = mock(InitialStateProvider.class);
    private final CheckRule rule = mock(CheckRule.class);
    private final GameOverChecker checker = mock(GameOverChecker.class);
    private final Input white = mock(Input.class);
    private final Input black = mock(Input.class);
    private final View view = mock(View.class);
    private final Piece a1Piece = mock(Piece.class);
    private final Piece b4Piece = mock(Piece.class);
    private final Piece d8Piece = mock(Piece.class);
    private final Piece h6Piece = mock(Piece.class);
    private final HashMap<Position, Piece> board = HashMap.of(A1, a1Piece, B4, b4Piece, D8, d8Piece, H6, h6Piece);
    private final GameState state = spy(new GameState(board, White, HashSet.empty(), null));
    private final Move move = mock(Move.class);

    private Game game;

    @BeforeEach
    void setUp() {
        game = spy(new Game(provider, rule, checker, white, black, view));
        when(provider.createInitialState()).thenReturn(state);
        when(rule.validate(any(),any())).thenReturn(true);
        when(state.advance(any())).thenReturn(state);
    }

    @ParameterizedTest(name="{0} - {1}")
    @CsvSource({"1,WhiteWins,White wins!",
                "2,BlackWins,Black wins!",
                "7,Stalemate,Stalemate!",
                "11,WhiteWins,White wins!",
                "15,BlackWins,Black wins!",
                "25,Stalemate,Stalemate!"})
    void loop(int howManyLoops, GameCondition endGame, String endMessage) {
        final Map<Position, Map<Position,GameState>> possibleMoves = mock(Map.class);

        // Bloody hell don't use the when-return notation with actioning spies!
        doReturn(possibleMoves).when(game).computeAvailableMoves(state);
        doReturn(state).when(game).takeFirstInput(state, possibleMoves);

        OngoingStubbing<GameCondition> checkerStub = when(checker.check(board, White, possibleMoves));
        for (int i = 1; i < howManyLoops; i++) {
            checkerStub = checkerStub.thenReturn(Continue);
        }
        checkerStub.thenReturn(endGame);

        game.play();

        verify(provider).createInitialState();
        verify(view, times(howManyLoops)).refresh(board, White, HashSet.empty(), OutOfBoard, HashSet.empty());
        verify(view).announce(endMessage);
        verify(state, times(howManyLoops * 2)).getBoard();
        verify(state, times(howManyLoops * 2)).getPlayer();
        verify(checker, times(howManyLoops)).check(board, White, possibleMoves);
        verify(game, times(howManyLoops - 1)).takeFirstInput(state, possibleMoves);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void computeAvailableMoves(Player currentPlayer) {
        final Player otherPlayer = currentPlayer.next();
        final Move selfFilteredMove = mock(Move.class);
        final Move ruleFilteredMove = mock(Move.class);
        final HashMap<Position,Piece> outputBoard = HashMap.of(A2, a1Piece);
        final HashMap<Position,Piece> ruleFilteredBoard = HashMap.of(B5, b4Piece);
        final GameState outputState = new GameState(outputBoard, otherPlayer, HashSet.empty(), null);
        final GameState ruleFilteredState = new GameState(ruleFilteredBoard, otherPlayer, HashSet.empty(), null);

        when(move.validate(state)).thenReturn(true);
        when(ruleFilteredMove.validate(state)).thenReturn(true);

        when(state.getPlayer()).thenReturn(currentPlayer);
        when(a1Piece.getPlayer()).thenReturn(currentPlayer);
        when(b4Piece.getPlayer()).thenReturn(otherPlayer);
        when(d8Piece.getPlayer()).thenReturn(currentPlayer);
        when(h6Piece.getPlayer()).thenReturn(otherPlayer);
        when(a1Piece.computePossibleMoves(A1)).thenReturn(Stream.of(move, selfFilteredMove));
        when(d8Piece.computePossibleMoves(D8)).thenReturn(Stream.of(selfFilteredMove, ruleFilteredMove));
        when(move.getTo()).thenReturn(A3);
        when(ruleFilteredMove.getTo()).thenReturn(E5);
        when(move.execute(state)).thenReturn(outputState);
        when(ruleFilteredMove.execute(state)).thenReturn(ruleFilteredState);
        when(rule.validate(outputBoard, currentPlayer)).thenReturn(true);
        when(rule.validate(ruleFilteredBoard, currentPlayer)).thenReturn(false);

        // n.b. We are including pieces of the current user which have nowhere to move
        assertThat(game.computeAvailableMoves(state)).isEqualTo(HashMap.of(A1, HashMap.of(A3, outputState), D8, HashMap.empty()));

        verify(move).validate(state);
        verify(ruleFilteredMove).validate(state);
        verify(state).getPlayer();
        verify(a1Piece).getPlayer();
        verify(b4Piece).getPlayer();
        verify(d8Piece).getPlayer();
        verify(h6Piece).getPlayer();
        verify(a1Piece).computePossibleMoves(A1);
        verify(d8Piece).computePossibleMoves(D8);
        verify(move).getTo();
        verify(ruleFilteredMove).getTo();
        verify(move).execute(state);
        verify(ruleFilteredMove).execute(state);
        verify(rule).validate(outputBoard, currentPlayer);
        verify(rule).validate(ruleFilteredBoard, currentPlayer);
    }

//    @Test
//    void playOne() {
//
//        org.junit.jupiter.api.Assumptions.assumeTrue(false, "TODO");
//
//        when(white.takeInput()).thenReturn(A3).thenReturn(A1).thenReturn(G7).thenReturn(B3);
//        when(piece.getPlayer()).thenReturn(White);
//        when(piece.computePossibleMoves(A1)).thenReturn(Stream.of(move));
//        when(move.validate(any())).thenReturn(true);
//        when(move.getTo()).thenReturn(B3);
//        when(move.execute(eq(board), any(), any())).then(moveStub);
//        game.play();
//
//        verify(provider).createInitialState();
//        verify(checker, times(2)).isOver(board, White, possibleMoves);
//        verify(white, times(4)).takeInput();
//        verify(view, times(3)).refresh(eq(board), any(), eq(HashSet.empty()), any(), any());
//        verify(view).announce("Invalid pickup: A3");
//        verify(view).announce("Invalid move: A1 - G7");
//        verify(view).announce("White moves: A1 - B3");
//        verify(piece).getPlayer();
//        verify(piece).computePossibleMoves(A1);
//        verify(move).validate(state);
//        verify(move).getTo();
//        verify(move).execute(eq(board), any(), any());
//        verify(state).advance(board);
//    }

    // TODO Game deserves, like, a couple dozen tests

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(provider, rule, checker, white, black, view, a1Piece, b4Piece, d8Piece, h6Piece, move);
    }
}