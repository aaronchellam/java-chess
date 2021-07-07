package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/*
If all squares are numbered from 0 to 63, a knight on a given square x will have constant potential moves relative to
position x. For example, x - 6 (up one square, right two squares) is a candidate move as this is equivalent to (-8 + 2)
where -8 denotes moving up one rank, and +2 denotes moving two squares to the right. Notably, a candidate move is
not necessarily a legal move given that the new square may be occupied by a friendly piece or the new square may be
off-board.
 */

public class Knight extends Piece {

    private final static int[] candidate_move_coordinates = {-17, -15, 10, -6, 6, 10, 15, 17};

    private Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        int destinationCoordinate;
        final List<Move> legalMoves = new ArrayList<>();

        for (final int offset : candidate_move_coordinates) {
            destinationCoordinate = this.piecePosition + offset;

            // TODO check coordinate is valid
            if (true /* isValid coordinate*/) {
                final Tile destinationTile = board.getTile(destinationCoordinate);

                if (!destinationTile.isOccupied()) {
                    legalMoves.add(new Move()); // TODO The actual move needs to be defined

                } else { // tile is occupied by a friendly or unfriendly piece

                    final Piece pieceAtDestination = destinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (this.pieceAlliance != pieceAlliance) { // enemy piece
                        legalMoves.add(new Move()); // TODO add move
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }
}
