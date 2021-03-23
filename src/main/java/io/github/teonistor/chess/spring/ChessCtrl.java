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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("chess-api")
@RequiredArgsConstructor
public class ChessCtrl implements View {

    private final SimpMessagingTemplate ws;
    private final InputActionProvider inputActionProvider;
    private final ControlLoop controlLoop;

//    private final ArrayBlockingQueue<InputAction> q = new ArrayBlockingQueue<>(1);
    private List<Traversable<?>> lastState = List.of(HashMap.empty(), List.empty(), List.empty());

    @PostConstruct
    void initialInput() {
        // TODO Have "new game" button instead of this autostart
        controlLoop.onInput(inputActionProvider.newGame());
    }

    @Override
    public void refresh(final Map<Position, Piece> board, final Player player, final Traversable<Piece> capturedPieces, final Traversable<Tuple2<Position, Position>> possibleMoves) {
        lastState = List.of(board, capturedPieces, possibleMoves);
        System.out.printf("Last state now: %s%n", lastState);
        ws.convertAndSend("/chess-ws/board", lastState);
    }

    @Override
    public void announce(final String message) {
        ws.convertAndSend("/chess-ws/announcements");
    }

    @RequestMapping("start-hotseat")
    RedirectView startHotseat(final HttpServletResponse response) {
        return startGame(response, "hotseat");
    }

    @RequestMapping("start-black")
    RedirectView startBlack(final HttpServletResponse response) {
        return startGame(response, "black");
    }

    @RequestMapping("start-white")
    RedirectView startWhite(final HttpServletResponse response) {
        return startGame(response, "white");
    }

    private RedirectView startGame(final HttpServletResponse response, final String white) {
        response.addCookie(new Cookie("player", white));
        return new RedirectView("/chess/game.html");
    }

    @SubscribeMapping("/board")
    List<Traversable<?>> onSubscribeBoard() {
        return lastState;
    }

    @RequestMapping("/move")
    void onMove(final @RequestBody Tuple2<Position, Position> move) {
        controlLoop.onInput(inputActionProvider.gameInput(move._1, move._2));
    }
}
