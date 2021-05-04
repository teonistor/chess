package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.core.Player;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("chess-api")
public class ChessFrontpageCtrl {
    public static final String Hotseat = "Hotseat";

    @RequestMapping("assign-hotseat")
    RedirectView assignHotseat(final HttpServletResponse response) {
        return assignPlayer(response, Hotseat);
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
}
