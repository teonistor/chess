package io.github.teonistor.chess.spring;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings
class ChessFrontpageCtrlTest {

    private @Mock HttpServletResponse response;
    private @Captor ArgumentCaptor<Cookie> cookie;

    @Test
    void assignHotseat() {
        assertCookieAssignment(new ChessFrontpageCtrl().assignHotseat(response), "Hotseat");
    }

    @Test
    void assignBlack() {
        assertCookieAssignment(new ChessFrontpageCtrl().assignBlack(response), "Black");
    }

    @Test
    void assignWhite() {
        assertCookieAssignment(new ChessFrontpageCtrl().assignWhite(response), "White");
    }

    private void assertCookieAssignment(final RedirectView redirectView, final String player) {
        assertThat(redirectView.getUrl()).isEqualTo("/chess/game.html");

        verify(response).addCookie(cookie.capture());
        assertThat(cookie.getValue()).extracting(Cookie::getName, Cookie::getValue).containsExactly("player", player);
        verifyNoMoreInteractions(response);
    }
}