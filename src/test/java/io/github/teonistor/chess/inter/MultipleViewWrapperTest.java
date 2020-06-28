package io.github.teonistor.chess.inter;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class MultipleViewWrapperTest {

    private final View aView = mock(View.class);
    private final View anotherView = mock(View.class);

    @Test
    void wrapBad() {
        assertThatIllegalArgumentException().isThrownBy(MultipleViewWrapper::wrapIfNeeded)
                .withMessage("At least one view must be given");
        assertThatNullPointerException().isThrownBy(() -> MultipleViewWrapper.wrapIfNeeded(aView, null, anotherView))
                .withMessage("All views must not be null");
        assertThatNullPointerException().isThrownBy(() -> MultipleViewWrapper.wrapIfNeeded((View) null))
                .withMessage("All views must not be null");
    }

    @Test
    void wrapOne() {
        assertThat(MultipleViewWrapper.wrapIfNeeded(aView)).isEqualTo(aView);
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void wrapManyAndRefresh(Player player) {
        final View view = MultipleViewWrapper.wrapIfNeeded(aView, anotherView);
        view.refresh(HashMap.empty(), player, List.empty(), Position.E1, HashSet.of(Position.C2));

        verify(aView).refresh(HashMap.empty(), player, List.empty(), Position.E1, HashSet.of(Position.C2));
        verify(anotherView).refresh(HashMap.empty(), player, List.empty(), Position.E1, HashSet.of(Position.C2));
    }

    @ParameterizedTest
    @EnumSource(Player.class)
    void wrapManyAndAnnounce(Player player) {
        final View view = MultipleViewWrapper.wrapIfNeeded(aView, anotherView);
        view.announce("Very important message");

        verify(aView).announce("Very important message");
        verify(anotherView).announce("Very important message");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(aView, anotherView);
    }
}