package io.github.teonistor.chess.acct;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Game;
import io.github.teonistor.chess.core.GameFactory;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.TerminalView;
import org.mockito.InOrder;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class StepDef implements En {

    private final GameFactory gameFactory = new GameFactory();
    private final Input white = mock(Input.class);
    private final Input black = mock(Input.class);
    private final TerminalView view = spy(new TerminalView());
    private final InOrder viewOrder = inOrder(view);

    private Game game;

    public StepDef() {
        System.out.println("** new StepDef() **");

        Given("a game in its initial state", () -> {
            game = gameFactory.createGame(white, black, view);
        });

        When("the following moves take place", (final DataTable dataTable) -> {
            final List<List<String>> data = dataTable.asLists();

            final Iterator<List<String>> dataIterator = data.iterator();
            when(white.takeInput(any(), any())).then(invocation -> {
                final List<String> nextData = dataIterator.next();
                assertThat(nextData.get(0)).isEqualTo("White");
                return invocation.getArgument(1, BiFunction.class).apply(Position.valueOf(nextData.get(1)), Position.valueOf(nextData.get(2)));
            });
            when(black.takeInput(any(), any())).then(invocation -> {
                final List<String> nextData = dataIterator.next();
                assertThat(nextData.get(0)).isEqualTo("Black");
                return invocation.getArgument(1, BiFunction.class).apply(Position.valueOf(nextData.get(1)), Position.valueOf(nextData.get(2)));
            });

            game.play();

            for (List<String> datum : data) {
                viewOrder.verify(view).announce(String.format("%s moves: %s - %s", datum.get(0), datum.get(1), datum.get(2)));
            }
        });

        Then("{} wins", (Player player) -> {
            viewOrder.verify(view).announce(String.format("%s wins!", player));
        });
    }
}
