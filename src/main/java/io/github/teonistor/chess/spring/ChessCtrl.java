package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("chess-api")
@RequiredArgsConstructor
public class ChessCtrl implements View {

    private final SimpMessagingTemplate ws;
    private final InputActionProvider inputActionProvider;
    private final ControlLoop controlLoop;

    // Intermediate UI caches. This feels like it's violating some principle, I'm really unsure
    private Map<?,?> lastBoard = HashMap.empty();
    private Traversable<?> lastCapturedPieces = List.empty();
    private Traversable<?> lastPossibleMovesAll = List.empty();
    private Traversable<?> lastPossibleWhiteMoves = List.empty();
    private Traversable<?> lastPossibleBlackMoves = List.empty();

    @Override
    public void refresh(final Map<Position, Piece> board, final Player player, final Traversable<Piece> capturedPieces, final Traversable<Tuple2<Position, Position>> possibleMoves) {
        lastBoard = board;
        lastCapturedPieces = capturedPieces;
        lastPossibleMovesAll = possibleMoves;

        // TODO New moves paradigm
        if (player == Player.White) {
            lastPossibleWhiteMoves = possibleMoves;
            lastPossibleBlackMoves = List.empty();
        } else {
            lastPossibleWhiteMoves = List.empty();
            lastPossibleBlackMoves = possibleMoves;
        }

        ws.convertAndSend("/chess-ws/board", lastBoard);
        ws.convertAndSend("/chess-ws/captured-pieces", lastCapturedPieces);
        ws.convertAndSend("/chess-ws/moves-white", lastPossibleWhiteMoves);
        ws.convertAndSend("/chess-ws/moves-black", lastPossibleBlackMoves);
        ws.convertAndSend("/chess-ws/moves-all", lastPossibleMovesAll);
    }

    @Override
    public void announce(final String message) {
        ws.convertAndSend("/chess-ws/announcements");
    }

    @RequestMapping("assign-hotseat")
    RedirectView assignHotseat(final HttpServletResponse response) {
        return assignPlayer(response, "Hotseat");
    }

    @RequestMapping("assign-black")
    RedirectView assignBlack(final HttpServletResponse response) {
        return assignPlayer(response, Player.Black);
    }

    @RequestMapping("assign-white")
    RedirectView assignWhite(final HttpServletResponse response) {
        return assignPlayer(response, Player.White);
    }

    private RedirectView assignPlayer(final HttpServletResponse response, final Object player) {
        response.addCookie(new Cookie("player", player.toString()));
        return new RedirectView("/chess/game.html");
    }

    @SubscribeMapping("/board")
    Traversable<?> onSubscribeBoard() {
        return lastBoard;
    }

    @SubscribeMapping("/captured-pieces")
    Traversable<?> onSubscribeCapturedPieces() {
        return lastCapturedPieces;
    }

    @SubscribeMapping("/moves-black")
    Traversable<?> onSubscribeMovesBlack() {
        return lastPossibleBlackMoves;
    }

    @SubscribeMapping("/moves-white")
    Traversable<?> onSubscribeMovesWhite() {
        return lastPossibleWhiteMoves;
    }

    @SubscribeMapping("/moves-all")
    Traversable<?> onSubscribeMovesAll() {
        return lastPossibleMovesAll;
    }

    @RequestMapping("/move")
    void onMove(final @RequestBody Tuple2<Position, Position> move) {
        controlLoop.onInput(inputActionProvider.gameInput(move._1, move._2));
    }

    @RequestMapping("/moves-channel")
    String movesChannel(final @CookieValue("player") String player) {
        try {
            Player.valueOf(player);
            return "moves-" + player.toLowerCase();
        } catch (final Exception e) {
            return "moves-all";
        }
    }

    @RequestMapping("/new/standard")
    void newGame() {
        controlLoop.onInput(inputActionProvider.newGame());
    }
}
