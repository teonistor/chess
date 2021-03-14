package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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

    @Override
    public void refresh(Map<Position, Piece> board, Player player, Traversable<Piece> capturedPieces, Traversable<Tuple2<Position, Position>> possibleMoves) {

    }

    @Override
    public void announce(String message) {

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
    void onSubscribeBoard() {

    }
}
