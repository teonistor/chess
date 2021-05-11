package io.github.teonistor.chess.spring;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.ctrl.ControlLoop;
import io.github.teonistor.chess.ctrl.NormalGameInput;
import io.github.teonistor.chess.ctrl.PromotionGameInput;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
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

import static io.github.teonistor.chess.spring.ChessFrontpageCtrl.Hotseat;

@RestController
@RequestMapping("chess-api")
@RequiredArgsConstructor
public class ChessCtrl implements View {

    private final SimpMessagingTemplate ws;
    private final ControlLoop controlLoop;

    // Intermediate UI caches, so that a client joining midway sees the state right away. This feels like it's violating some principle, I'm really unsure.
    private ExternalGameState lastStateBlack = ExternalGameState.NIL;
    private ExternalGameState lastStateWhite = ExternalGameState.NIL;
    private ExternalGameState lastStateAll = ExternalGameState.NIL;

    @Override
    public void refresh(final Map<Position, Piece> board, final Traversable<Piece> capturedPieces, final Traversable<Tuple2<Position, Position>> possibleMovesBlack, final Traversable<Tuple2<Position, Position>> possibleMovesWhite, final boolean promotionRequiredBlack, final boolean promotionRequiredWhite) {
        lastStateBlack = new ExternalGameState(board, capturedPieces, possibleMovesBlack, HashMap.empty(), false, promotionRequiredBlack);
        lastStateWhite = new ExternalGameState(board, capturedPieces, possibleMovesWhite, HashMap.empty(), promotionRequiredWhite, false);
        lastStateAll = lastStateWhite.combine(lastStateBlack);

        ws.convertAndSend("/chess-ws/state-white", lastStateWhite);
        ws.convertAndSend("/chess-ws/state-black", lastStateBlack);
        ws.convertAndSend("/chess-ws/state-all", lastStateAll);
    }

    @Override
    public void announce(final String message) {
        ws.convertAndSend("/chess-ws/announcements", message);
    }

    @SubscribeMapping("/state-black")
    ExternalGameState onSubscribeStateBlack() {
        return lastStateBlack;
    }

    @SubscribeMapping("/state-white")
    ExternalGameState onSubscribeStateWhite() {
        return lastStateWhite;
    }

    @SubscribeMapping("/state-all")
    ExternalGameState onSubscribeStateAll() {
        return lastStateAll;
    }

    @RequestMapping("/move")
    void onMove(final @CookieValue("player") String player, final @RequestBody Tuple2<Position, Position> move) {
        controlLoop.gameInput(new NormalGameInput(move._1, move._2));
    }

    @RequestMapping("/promote")
    void onPromote(final @CookieValue("player") String player, final @RequestBody Piece piece) {
        // TODO Why is this business rule here?
        if (Hotseat.equals(player) || piece.getPlayer().name().equals(player))
            controlLoop.gameInput(new PromotionGameInput(piece));
        else
            throw new IllegalArgumentException("Invalid piece to promote");
    }

    @RequestMapping("/state-channel")
    String stateChannel(final @CookieValue("player") String player) {
        try {
            Player.valueOf(player);
            return "state-" + player.toLowerCase();
        } catch (final Exception e) {
            return "state-all";
        }
    }

    @RequestMapping("/new/standard")
    void newStandardGame() {
        controlLoop.newStandardGame();
    }

    @RequestMapping("/new/parallel")
    void newParallelGame() {
        controlLoop.newParallelGame();
    }

    @ExceptionHandler
    ResponseEntity<String> handleException(final Exception e) {
        // TODO Special
        return ResponseEntity.badRequest().body(e.toString());
    }
}
