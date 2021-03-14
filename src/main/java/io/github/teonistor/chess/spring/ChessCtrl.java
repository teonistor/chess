package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ArrayBlockingQueue;

@RestController
@RequestMapping("chess-api")
@RequiredArgsConstructor
public class ChessCtrl implements View, Input {

    private final SimpMessagingTemplate ws;
    private final ArrayBlockingQueue<Tuple2<Position,Position>> q = new ArrayBlockingQueue<>(1);
    private List<Traversable<?>> lastState = List.of(HashMap.empty(), List.empty(), List.empty());

    @Override
    public void refresh(Map<Position, Piece> board, Player player, Traversable<Piece> capturedPieces, Traversable<Tuple2<Position, Position>> possibleMoves) {
        lastState = List.of(board, capturedPieces, possibleMoves);
        ws.convertAndSend("/chess-ws/board");
    }

    @Override
    public void announce(String message) {
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

    private RedirectView startGame(HttpServletResponse response, String white) {
        response.addCookie(new Cookie("player", white));
        return new RedirectView("/chess/game.html");
    }

    @SubscribeMapping("/board")
    List<Traversable<?>> onSubscribeBoard() {
        return lastState;
    }

    @MessageMapping("/move")
    void onMove(Tuple2<Position, Position> move) {
        System.out.println(move);
        q.offer(move);
    }

    @Override
    public Tuple2<Position, Position> simpleInput() {
        return null;
    }

    @Override
    public String specialInput(String... options) {
        // TODO
        return options[0];
    }
}
