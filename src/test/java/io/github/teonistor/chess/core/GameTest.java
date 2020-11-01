package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.move.Move;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.stubbing.OngoingStubbing;
import java.util.stream.Stream;

import static io.github.teonistor.chess.board.Position.A1;
import static io.github.teonistor.chess.board.Position.A2;
import static io.github.teonistor.chess.board.Position.A3;
import static io.github.teonistor.chess.board.Position.B2;
import static io.github.teonistor.chess.board.Position.B4;
import static io.github.teonistor.chess.board.Position.B5;
import static io.github.teonistor.chess.board.Position.D7;
import static io.github.teonistor.chess.board.Position.D8;
import static io.github.teonistor.chess.board.Position.E5;
import static io.github.teonistor.chess.board.Position.H6;
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

    private final GameStateProvider provider = mock(InitialStateProvider.class);
    private final CheckRule rule = mock(CheckRule.class);
    private final GameOverChecker checker = mock(GameOverChecker.class);
    private final Input input = mock(Input.class);
    private final View view = mock(View.class);
    private final Piece a1Piece = mock(Piece.class);
    private final Piece b4Piece = mock(Piece.class);
    private final Piece d8Piece = mock(Piece.class);
    private final Piece h6Piece = mock(Piece.class);
    private final HashMap<Position, Piece> board = HashMap.of(A1, a1Piece, B4, b4Piece, D8, d8Piece, H6, h6Piece);
    private final GameState state = spy(new GameState(board, White, List.empty(), null));
    private final Move move = mock(Move.class);

    private Game game;

    @BeforeEach
    void setUp() {
        game = spy(new Game(provider, rule, checker, input, view));
        when(provider.createState()).thenReturn(state);
        when(rule.validate(any(), any())).thenReturn(true);
        doReturn(state).when(state).advance(any());
    }

    @ParameterizedTest(name="{0} - {1}")
    @CsvSource({"1,WhiteWins,White wins!",
                "2,BlackWins,Black wins!",
                "7,Stalemate,Stalemate!",
                "11,WhiteWins,White wins!",
                "15,BlackWins,Black wins!",
                "25,Stalemate,Stalemate!"})
    void loop(final int howManyLoops, final GameCondition endGame, final String endMessage) {
        final Map<Position, Map<Position,GameState>> possibleMoves = mock(Map.class);
        final io.vavr.collection.Stream<Tuple2<Position, Position>> movesAsPairs = mock(io.vavr.collection.Stream.class);

        // Bloody hell don't use the when-return notation with actioning spies!
        doReturn(possibleMoves).when(game).computeAvailableMoves(state);
        doReturn(state).when(game).processInput(possibleMoves);
        doReturn(movesAsPairs).when(game).turnMovesIntoPairs(possibleMoves);

        OngoingStubbing<GameCondition> checkerStub = when(checker.check(board, White, possibleMoves));
        for (int i = 1; i < howManyLoops; i++) {
            checkerStub = checkerStub.thenReturn(Continue);
        }
        checkerStub.thenReturn(endGame);

        game.play();

        verify(provider).createState();
        verify(view, times(howManyLoops)).refresh(board, White, List.empty(), movesAsPairs);
        verify(view).announce(endMessage);
        verify(state, times(howManyLoops * 2)).getBoard();
        verify(state, times(howManyLoops * 2)).getPlayer();
        verify(checker, times(howManyLoops)).check(board, White, possibleMoves);
        verify(game, times(howManyLoops - 1)).processInput(possibleMoves);
    }

    @ParameterizedTest(name="{0}")
    @EnumSource(Player.class)
    void computeAvailableMoves(final Player currentPlayer) {
        final Player otherPlayer = currentPlayer.next();
        final Move selfFilteredMove = mock(Move.class);
        final Move ruleFilteredMove = mock(Move.class);
        final HashMap<Position,Piece> outputBoard = HashMap.of(A2, a1Piece);
        final HashMap<Position,Piece> ruleFilteredBoard = HashMap.of(B5, b4Piece);
        final GameState outputState = new GameState(outputBoard, otherPlayer, List.empty(), null);
        final GameState ruleFilteredState = new GameState(ruleFilteredBoard, otherPlayer, List.empty(), null);

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

    @ParameterizedTest
    @CsvSource({"A2,B5","A7,C1","D3,E3","F1,H8"})
    void processInputGood(Position p1, Position p2) {
        when(input.simpleInput()).thenReturn(new Tuple2<>(p1, p2));

        assertThat(game.processInput(HashMap.of(p1, HashMap.of(p2, state)))).isEqualTo(state);

        verify(input).simpleInput();
    }

    @Test
    void processInputBogus() {
        when(input.simpleInput()).thenReturn(new Tuple2<>(A1, B2)).thenReturn(new Tuple2<>(A2, B4))
                                 .thenReturn(new Tuple2<>(D7, B5)).thenReturn(new Tuple2<>(A2, B5));

        assertThat(game.processInput(HashMap.of(A2, HashMap.of(B5, state)))).isEqualTo(state);

        verify(input, times(4)).simpleInput();
    }

    @ParameterizedTest
    @CsvSource({"D1,C4,G2,A5,H3,B8,D5,D7",
                "G7,H6,F3,E5,B6,C1,E2,A2",
                "D4,A4,G5,A6,A8,C8,G1,C7",
                "E4,A1,B4,C6,E3,G8,D2,H4"})
    void turnMovesIntoPairs(Position p1, Position p2, Position p3, Position p4, Position p5, Position p6, Position p7, Position p8) {
        assertThat(game.turnMovesIntoPairs(HashMap.of(
                p1, HashMap.of(p2, state, p3, state),
                p4, HashMap.of(p3, state),
                p5, HashMap.of(p6, state, p7, state, p8, state),
                p8, HashMap.empty())))
            .containsExactlyInAnyOrder(
                new Tuple2<>(p1, p2),
                new Tuple2<>(p1, p3),
                new Tuple2<>(p4, p3),
                new Tuple2<>(p5, p6),
                new Tuple2<>(p5, p7),
                new Tuple2<>(p5, p8));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(provider, rule, checker, input, view, a1Piece, b4Piece, d8Piece, h6Piece, move);
    }
}