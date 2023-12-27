package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece {
    private boolean isCastled;
    private final static int[] kingModifiers = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.KING, true);
        this.isCastled = false;
    }

    public King(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove, boolean isCastled) {
        super(piecePosition, pieceAlliance, PieceType.KING, isFirstMove);
        this.isCastled = isCastled;
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int modifier : kingModifiers) {
            final int candidatePosition = this.piecePosition + modifier;

            if (isFirstRankException(candidatePosition, modifier) || isEighthRankException(candidatePosition, modifier)) {
                continue;
            }



            if (BoardUtils.isValidTilePosition(candidatePosition)) {
                addLegalMove(board, legalMoves, candidatePosition);
            }
        }


        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getDestinationPosition(), move.getMovedPiece().getAlliance() );
    }


    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    private static boolean isFirstRankException(int position, int modifier) {
        return (position % 8 == 0) && (modifier == -9 || modifier == -1 || modifier == 7);
    }

    private static boolean isEighthRankException(int position, int modifier) {
        return (position % 8 == 7) && (modifier == -7 || modifier == 1 || modifier == 9);
    }

    public boolean isCastled() {
        return this.isCastled;
    }

}
