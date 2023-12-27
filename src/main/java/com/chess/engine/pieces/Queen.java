package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.BoardUtils.isValidTilePosition;

public class Queen extends Piece {

    private final static int[] queenModifiers = {-9, -8, -7, -1, 1, 7, 8, 9};

    public Queen(int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.QUEEN, true);
    }

    public Queen(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.QUEEN, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();


        for (int modifier: queenModifiers) {
            int candidatePosition = this.piecePosition;
            while(isValidTilePosition(candidatePosition)) {
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
    public Queen movePiece(final Move move) {
        return new Queen(move.getDestinationPosition(), move.getMovedPiece().getAlliance() );
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }



    private static boolean isFirstRankException(int position, int modifier) {
        return (position % 8 == 0) && (modifier == -9 || modifier == 7 || modifier == -1);
    }

    private static boolean isEighthRankException(int position, int modifier) {
        return (position % 8 == 7) && (modifier == 9 || modifier == -7 || modifier == +1);
    }
}
