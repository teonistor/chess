package io.github.teonistor.chess.acct;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameFactory;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.TerminalView;

import static org.mockito.Mockito.mock;

public class StepDef implements En {

    private final GameFactory gameFactory = new GameFactory();
    private final Input white = mock(Input.class);
    private final Input black = mock(Input.class);

    private Game game;

    public StepDef() {

        Given("a game in its initial state", () -> {
            game = gameFactory.createGame(() -> white, () -> black, new TerminalView());
        });

        When("the following moves take place", (final DataTable data) -> {

        });

        Then("{} wins", (Player player) -> {
            System.out.println(player);
        });
    }
}
