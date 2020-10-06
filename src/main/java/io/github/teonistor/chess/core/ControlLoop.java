package io.github.teonistor.chess.core;

import com.google.common.annotations.VisibleForTesting;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.TerminalInput;
import io.github.teonistor.chess.inter.TerminalView;
import io.github.teonistor.chess.inter.View;

public class ControlLoop {

    private final Input white, black;
    private final View view;

    // TODO Temporary... we may need a factory for this or at least static constructors
    public ControlLoop() {
        this(new TerminalInput(), new TerminalInput(), new TerminalView());
    }

    @VisibleForTesting
    ControlLoop(Input white, Input black, View view) {
        this.white = white;
        this.black = black;
        this.view = view;
    }

    public void run() {
        while (true) {
            final InputAction action = white.takeCommonInput();
            if (action.gameStateProvider().isPresent()) {
                System.err.println("[DEBUG] Game state provider provided - starting main control loop");
                loop(action.gameStateProvider().get());
                break;
            }

            if (action.isExit()) {
                System.err.println("[DEBUG] Exit action received - ending early control loop");
                break;
            }
        }
    }

    private void loop(final GameStateProvider provider) {
        final Game game = World.gameFactory().createGame(provider, white, black, view);

        while (true) {
            final InputAction action = white.takeCommonInput();

            if (action.gameInput().isPresent()) {
                game.playRound(action.gameInput().get()._1, action.gameInput().get()._2, view);

                if (game.getCondition() == GameCondition.Continue) {
                    continue;
                } else {
                    System.err.println("[DEBUG] Game condition not 'Continue' - ending main control loop");
                    break;
                }

                /* Rambles... this has become excessively messy and I don't know why
                 * Can we let the game control the loop still? But only with 2 positions at once. UI thread separate
                 * Or make the game a service and keep the state here, along with the inputs, and it gives us back the new state,
                 * situation, and move map newly valid... but then the precomputed stuff! Suddenly we need RedundantState!
                 */
            }

            if (action.savePath().isPresent()) {
                World.saveLoad().doSaveState(game.getState(), action.savePath().get());
                System.err.println("[DEBUG] Game saved");
                continue;
            }

            if (action.isExit()) {
                System.err.println("[DEBUG] Exit action received - ending main control loop");
                break;
            }
        }
    }
}
