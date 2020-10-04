package io.github.teonistor.chess.remoteclient;

import io.github.teonistor.chess.board.Position;
import io.github.teonistor.chess.core.Player;
import io.github.teonistor.chess.inter.View;
import io.github.teonistor.chess.piece.Piece;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.rmi.Remote;

public interface RemoteView extends Remote, View {
//    @Override
    void refresh(Map<Position, Piece> board, Player player, List<Piece> capturedPieces, Position source, Set<Position> targets);

    @Override
    void announce(String message);
}
