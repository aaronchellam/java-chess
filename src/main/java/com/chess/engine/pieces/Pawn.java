package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.BoardUtils.*;
import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {
    private final static int[] pawnModifiers = {7, 8, 9, 16};

    public Pawn(final int piecePosition,
                final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, true);
    }

    public Pawn(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, isFirstMove);
    }

    private static int calculateEnPassantDirection(int modifier) {
        return modifier == 7 ? 1 : -1;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int modifier : pawnModifiers) {
            int candidatePosition = this.piecePosition + (modifier * this.pieceAlliance.getDirection());

            if (!BoardUtils.isValidTilePosition(candidatePosition)) {
                continue; // skip move
            }

            if (modifier == 8 && !board.getTile(candidatePosition).isOccupied()) {
                final PawnMove move = new PawnMove(board, this, candidatePosition);
                if (this.pieceAlliance.isPawnPromotionSquare(candidatePosition)) {
                    legalMoves.add(new PawnPromotionMove(move));
                } else {
                    legalMoves.add(move);
                }
            } else if (modifier == 16 && this.isFirstMove() && ((isOnSeventhRank(this.piecePosition) && this.pieceAlliance.isBlack()) || (isOnSecondRank(this.piecePosition) && this.pieceAlliance.isWhite()))) {//TODO change Pawn class to store first move
                final int behindCandidatePosition = this.piecePosition + (8 * this.pieceAlliance.getDirection());

                if (!board.getTile(behindCandidatePosition).isOccupied() && !board.getTile(candidatePosition).isOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidatePosition));
                }

            } else if (isValidCaptureDiagonal(modifier, isOnFirstFile(this.piecePosition), isOnLastFile(this.piecePosition))) {
                final int enPassantDirection = calculateEnPassantDirection(modifier);
                if (board.getTile(candidatePosition).isOccupied()) {
                    final Piece pieceOnTile = board.getTile(candidatePosition).getPiece();
                    if (pieceOnTile.getAlliance() != this.pieceAlliance) {
                        final PawnCaptureMove move = new PawnCaptureMove(board, this, candidatePosition, pieceOnTile);
                        if (this.pieceAlliance.isPawnPromotionSquare(candidatePosition)) {
                            legalMoves.add(new PawnPromotionMove(move));
                        } else {
                            legalMoves.add(move);
                        }
                    }
                } else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (enPassantDirection * this.pieceAlliance.getOppositeDirection()))) {
                        final Piece enPassantPawn = board.getEnPassantPawn();
                        if (this.pieceAlliance != enPassantPawn.getAlliance()) {
                            legalMoves.add(new PawnEnPassantCaptureMove(board, this, candidatePosition, enPassantPawn));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationPosition(), move.getMovedPiece().getAlliance());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }

    private boolean isValidCaptureDiagonal(int modifier, boolean onFirstFile, boolean onLastFile) {
        if (modifier == 7) {
            return !((this.pieceAlliance.isBlack() && onFirstFile) || (this.pieceAlliance.isWhite() && onLastFile));
        } else { // modifier == 9
            return !((this.pieceAlliance.isBlack() && onLastFile) || (this.pieceAlliance.isWhite() && onFirstFile));
        }
    }


}
