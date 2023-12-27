package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    public Piece(final int piecePosition, final Alliance pieceAlliance, final PieceType pieceType, final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { // referentially equivalent
            return true;
        }

        if (!(other instanceof Piece)) {
            return false;
        }

        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
               pieceAlliance == otherPiece.getAlliance() && isFirstMove == ((Piece) other).isFirstMove;
    }



    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    

    public int getPiecePosition() { return this.piecePosition; }

    public Alliance getAlliance() {
        return this.pieceAlliance;
    }
    
    public PieceType getPieceType() { return this.pieceType; }

    public boolean isFirstMove() { return this.isFirstMove; }

    public int getPieceValue() {
        return this.pieceType.getValue();
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

    public abstract Piece movePiece(Move move);

    void addLegalMove(Board board, List<Move> legalMoves, int position) {
        final Tile destinationTile = board.getTile(position);

        if (!destinationTile.isOccupied()) {
            legalMoves.add(new NormalMove(board, this, position));
        } else {
            final Piece pieceAtDestination = destinationTile.getPiece();
            final Alliance occupiedPieceAlliance = pieceAtDestination.getAlliance();

            if (!(this.pieceAlliance == occupiedPieceAlliance)) {
                legalMoves.add(new MajorCaptureMove(board, this, position, pieceAtDestination));
            }
        }
    }


    public enum PieceType {

        PAWN("P", 1),
        KNIGHT("N", 3),
        BISHOP("B", 3),
        ROOK("R", 5),
        QUEEN("Q", 9),
        KING("K", 100000);


        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        public int getValue() {
            return this.pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        /**
         * @return true if piece is a King, false otherwise.
         */
        public boolean isKing() {
            return this == KING;
        }

        public boolean isRook() { return this == ROOK; }
    }
}
