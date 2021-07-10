package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;
import java.util.List;

public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;

    public Piece(final int piecePosition, final Alliance pieceAlliance) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    /**
     *
     * A collection is used (rather than a set or list) because either a set or list could be used, and a collection is
     * more abstract. That is, List and Set extend Collection.
     * Each type of piece overrides this method since they move in different ways.
     *
     * @param board The game board.
     * @return A set of all legal moves.
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);
}
