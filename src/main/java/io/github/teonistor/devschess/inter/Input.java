package io.github.teonistor.devschess.inter;

import io.github.teonistor.devschess.board.Position;

public interface Input {
    Position takeInput();

    // TODO We would like to use a signature kind of like this to allow inputs to give both selections at once (for terminal, it's actually preferred) but also choose not to
    //    void takeInput(Consumer<Position> singleInput, BiConsumer<Position,Position> doubleInput);
}
