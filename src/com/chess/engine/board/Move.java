package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Move {
    final private Board board;
    final private Piece movedPiece;
    final private int destinationCoordinate;

    public Move(Board board, Piece movedPiece, int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public static final class NormalMove extends Move {

        public NormalMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }

    public static final class CaptureMove extends Move {
        final Piece attackedPiece;

        public CaptureMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

    }
}
