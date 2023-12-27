package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.BoardUtils.isValidTilePosition;

public class Rook extends Piece {
    private final static int[] rookModifiers = {-8, -1, 1, 8};

    public Rook(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.ROOK, true);
    }

    public Rook(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        final int column = this.piecePosition % 8;

        for (int modifier : rookModifiers) {
            int candidatePosition = this.piecePosition;

            while (isValidTilePosition(candidatePosition)) {
                if (isFirstRankException(candidatePosition, modifier) ||
                        isEighthRankException(candidatePosition, modifier)) {
                    break;
                }

                candidatePosition += modifier;

                if (isValidTilePosition(candidatePosition)) {
                    addLegalMove(board, legalMoves, candidatePosition);

                    if (board.getTile(candidatePosition).isOccupied()) {
                        break;
                    }
                }


            }
        }




        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getDestinationPosition(), move.getMovedPiece().getAlliance() );
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }


    private static boolean isFirstRankException(int position, int modifier) {
        return (position % 8 == 0) && (modifier == -1);
    }

    private static boolean isEighthRankException(int position, int modifier) {
        return (position % 8 == 7) && (modifier == 1);
    }
}
