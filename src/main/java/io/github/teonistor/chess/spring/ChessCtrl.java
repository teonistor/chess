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
import io.vavr.collection.Stream;
import io.vavr.collection.Traversable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    // Intermediate UI caches. This feels like it's violating some principle, I'm really unsure. TODO Use Observable (?) to optimise messages sent
    private Map<?,?> lastBoard = HashMap.empty();
    private Traversable<?> lastCapturedPieces = List.empty();
    private Traversable<?> lastPossibleMovesWhite = List.empty();
    private Traversable<?> lastPossibleMovesBlack = List.empty();
    private Traversable<?> lastPossibleMovesAll = List.empty();

    @Override
    public void refresh(final Map<Position, Piece> board, final Traversable<Piece> capturedPieces, final Traversable<Tuple2<Position, Position>> possibleMovesBlack, Traversable<Tuple2<Position, Position>> possibleMovesWhite) {
        lastBoard = board;
        lastCapturedPieces = capturedPieces;
        lastPossibleMovesBlack = possibleMovesBlack;
        lastPossibleMovesWhite = possibleMovesWhite;
        lastPossibleMovesAll = Stream.concat(possibleMovesBlack, possibleMovesWhite);

        ws.convertAndSend("/chess-ws/board", lastBoard);
        ws.convertAndSend("/chess-ws/captured-pieces", lastCapturedPieces);
        ws.convertAndSend("/chess-ws/moves-white", lastPossibleMovesWhite);
        ws.convertAndSend("/chess-ws/moves-black", lastPossibleMovesBlack);
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
        return lastPossibleMovesBlack;
    }

    @SubscribeMapping("/moves-white")
    Traversable<?> onSubscribeMovesWhite() {
        return lastPossibleMovesWhite;
    }

    @SubscribeMapping("/moves-all")
    Traversable<?> onSubscribeMovesAll() {
        return lastPossibleMovesAll;
    }

    @RequestMapping("/move")
    void onMove(final @CookieValue("player") String player, final @RequestBody Tuple2<Position, Position> move) {
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

    @ExceptionHandler
    ResponseEntity<String> handleException(final Exception e) {
        // TODO Special
        return ResponseEntity.badRequest().body(e.toString());
    }
}
