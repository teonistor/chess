package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.ctrl.InputActionProvider;
import io.github.teonistor.chess.inter.DefinitelyInput;
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

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

@RestController
@RequestMapping("chess-api")
@RequiredArgsConstructor
public class ChessCtrl implements View, DefinitelyInput {

    private final SimpMessagingTemplate ws;
    private final InputActionProvider inputActionProvider;

    private final ArrayBlockingQueue<InputAction> q = new ArrayBlockingQueue<>(1);
    private List<Traversable<?>> lastState = List.of(HashMap.empty(), List.empty(), List.empty());


    @PostConstruct
    void initialInput() {
        // TODO Hacks are piling on...
        q.offer(inputActionProvider.newGame());
    }

    @Override
    public void refresh(Map<Position, Piece> board, Player player, Traversable<Piece> capturedPieces, Traversable<Tuple2<Position, Position>> possibleMoves) {
        lastState = List.of(board, capturedPieces, possibleMoves);
        System.out.printf("Last state now: %s%n", lastState);
        ws.convertAndSend("/chess-ws/board", lastState);
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
        q.offer(inputActionProvider.gameInput(move._1, move._2));
    }

    @Override
    public InputAction simpleInput() {
        try {
            return q.take();

        } catch (InterruptedException e) {
            // TODO This is so horrible my keyboard is melting
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return inputActionProvider.exit();
        }
    }

    @Override
    public String specialInput(String... options) {
        // TODO
        return options[0];
    }
}
