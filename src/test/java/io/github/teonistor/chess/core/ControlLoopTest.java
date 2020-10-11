package io.github.teonistor.chess.core;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.ctrl.InputAction;
import io.github.teonistor.chess.inter.Input;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.save.SaveLoad;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class ControlLoopTest {

    @Mock private Input white;
    @Mock private Input black;
    @Mock private View view;

    @Mock private GameStateProvider provider;
    @Mock private GameFactory gameFactory;
    @Mock private SaveLoad saveLoad;
    @Mock private Game game;
    @Mock private GameState gameState;

    private ControlLoop controlLoop;

    @BeforeEach
    void setUp() {
        initMocks(this);
        controlLoop = new ControlLoop(white, black, view);
    }

    @Test
    void exitFromRun() {
        when(white.takeCommonInput()).thenReturn(InputAction.exit());
        controlLoop.run();
        verify(white).takeCommonInput();
    }

    @Test
    void exitFromRunAfterAFewInitialLoops() {
        when(white.takeCommonInput())
                .thenReturn(InputAction.saveGame("irrelevant 1"))
                .thenReturn(InputAction.gameInput(Position.A2, Position.E7))
                .thenReturn(InputAction.saveGame("irrelevant 2"))
                .thenReturn(InputAction.exit());
        controlLoop.run();
        verify(white, times(4)).takeCommonInput();
    }

    @Test
    void exitFromLoop() {
        setField(World.class, "gameFactory", gameFactory);
        when(white.takeCommonInput())
                .thenReturn(new InputAction() {
                    @Override
                    public Optional<GameStateProvider> gameStateProvider() {
                        return Optional.of(provider);
                    }})
                .thenReturn(InputAction.exit());
        when(gameFactory.createGame(provider, white, black, view)).thenReturn(game);

        controlLoop.run();

        verify(white, times(2)).takeCommonInput();
        verify(gameFactory).createGame(provider, white, black, view);
    }

    @Test
    void loadSaveAndExit() {
        setField(World.class, "gameFactory", gameFactory);
        setField(World.class, "saveLoad", saveLoad);
        when(saveLoad.load("file to load from")).thenReturn(provider);
        when(white.takeCommonInput())
                .thenReturn(InputAction.loadGame("file to load from"))
                .thenReturn(InputAction.saveGame("file to save to"))
                .thenReturn(InputAction.exit());
        when(gameFactory.createGame(provider, white, black, view)).thenReturn(game);
        when(game.getState()).thenReturn(gameState);

        controlLoop.run();

        verify(white, times(3)).takeCommonInput();
        verify(saveLoad, atLeastOnce()).load("file to load from"); // TODO We could eagerly create the relevant InputAction fields to have exactly once in this kind of place
        verify(gameFactory).createGame(provider, white, black, view);
        verify(saveLoad).save(gameState, "file to save to");
        verify(game).getState();
    }

    @Test
    void exitFromGameAfterAFewLoops() {
        setField(World.class, "gameFactory", gameFactory);
        when(white.takeCommonInput())
                .thenReturn(new InputAction() {
                    @Override
                    public Optional<GameStateProvider> gameStateProvider() {
                        return Optional.of(provider);
                    }})
                .thenReturn(InputAction.exit());
        when(gameFactory.createGame(provider, white, black, view)).thenReturn(game);

        controlLoop.run();

        verify(white, times(2)).takeCommonInput();
        verify(gameFactory).createGame(provider, white, black, view);
    }

    @Test
    void playRounds() {
        setField(World.class, "gameFactory", gameFactory);
        when(white.takeCommonInput())
                .thenReturn(new InputAction() {
                    @Override
                    public Optional<GameStateProvider> gameStateProvider() {
                        return Optional.of(provider);
                    }})
                .thenReturn(InputAction.gameInput(Position.A2, Position.B2))
                .thenReturn(InputAction.gameInput(Position.A4, Position.C4))
                .thenReturn(InputAction.gameInput(Position.D6, Position.F6));
        when(gameFactory.createGame(provider, white, black, view)).thenReturn(game);
        when(game.getCondition()).thenReturn(GameCondition.Continue).thenReturn(GameCondition.Continue).thenReturn(GameCondition.Stalemate);

        controlLoop.run();

        verify(white, times(4)).takeCommonInput();
        verify(gameFactory).createGame(provider, white, black, view);
        verify(game, times(3)).playRound(any(), any(), eq(view));
        verify(game, times(3)).getCondition();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(white, black, view, provider, gameFactory, saveLoad, game, gameState);
    }
}