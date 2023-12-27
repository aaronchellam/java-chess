package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Static import means that Move.NormalMove does not need to be specified.
import static com.chess.engine.board.BoardUtils.isValidTilePosition;

/*
If all squares are numbered from 0 to 63, a knight on a given square x will have constant potential moves relative to
position x. For example, x - 6 (up one square, right two squares) is a candidate move as this is equivalent to (-8 + 2)
where -8 denotes moving up one rank, and +2 denotes moving two squares to the right. Notably, a candidate move is
not necessarily a legal move given that the new square may be occupied by a friendly piece or the new square may be
off-board.
 */

/**
 * A knight has at most 8 legal moves.
 * <p>
 * To calculate candidate legal moves, it must first check the coordinates of the positions that a knight can typically
 * move to. The relative coordinate modifiers are stored in the candidate_move_coordinates array.
 */
public class Knight extends Piece {

    // The relative coordinate modifiers
    private final static int[] candidate_move_offsets = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.KNIGHT, true);
    }

    public Knight(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.KNIGHT, isFirstMove);
    }

    //TODO check that javadoc is accurate for valid squares
    /**
     * This method iterates through the coordinate modifiers for the knight to determine potential squares the piece can
     * move to. If the square itself is valid (i.e on the board and a legitimate knight move), it then checks whether or
     * not the square is occupied by another piece. If it is NOT occupied, the relevant move is added to the list of
     * legal moves. If it is occupied the method then checks whether or not the occupying piece is of an opposing
     * alliance. An allied piece implies that the move would not be legal, whereas an opposing piece results in a
     * capture move being added to the list of legal moves.
     *
     * @param board - The game board.
     * @return A list of legal moves.
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        // Loop through coordinate modifiers
        for (final int offset : candidate_move_offsets) {
            final int candidateDestinationCoordinate = this.piecePosition + offset; // potential position knight can move to

            // TODO check coordinate is valid
            if (isValidTilePosition(candidateDestinationCoordinate) && isValidKnightColumn(this.piecePosition, candidateDestinationCoordinate)) {
                addLegalMove(board, legalMoves, candidateDestinationCoordinate);
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getDestinationPosition(), move.getMovedPiece().getAlliance() );
    }


    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    private static boolean isValidKnightColumn(int currentPosition, int newPosition) {
        return Math.abs((newPosition % 8) - (currentPosition % 8)) <= 2;

    }

}
