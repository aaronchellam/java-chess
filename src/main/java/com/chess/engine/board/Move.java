package com.chess.engine.board;

import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    protected final Board board;
    protected final  Piece movedPiece;
    protected final int destinationPosition;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    public Move(Board board, Piece movedPiece, int destinationPosition) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationPosition = destinationPosition;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    /**
     * Convenience constructor for null move.
     *
     * @param board
     * @param destinationPosition
     */
    private Move (final Board board,
                  final int destinationPosition) {
        this.board = board;
        this.movedPiece = null;
        this.destinationPosition = destinationPosition;
        this.isFirstMove = false;
    }

    public Board getBoard() {
        return this.board;
    }

    public int getCurrentPosition() {
        return movedPiece.getPiecePosition();
    }

    public int getDestinationPosition() {
        return this.destinationPosition;
    }

    public Piece getMovedPiece() { return this.movedPiece; }

    public Piece getAttackedPiece() {
        return this.board.getTile(this.destinationPosition).getPiece();
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    //TODO Confirm generated equals and hashcode are accurate
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return destinationPosition == move.destinationPosition &&
               movedPiece.equals(move.movedPiece) &&
               this.getCurrentPosition() == move.getCurrentPosition();
    }

    @Override
    public int hashCode() {
        return Objects.hash(movedPiece, destinationPosition, movedPiece.getPiecePosition());
    }

    public Board execute() {
        final Builder builder = new Builder();
        final Collection<Piece> activePieces = new ArrayList<>();
        activePieces.addAll(this.board.getCurrentPlayer().getActivePieces());
        activePieces.addAll(this.board.getOpponentPlayer().getActivePieces());


        for (final Piece piece : activePieces) {
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        //Move the piece
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setNextTurnAlliance(this.board.getOpponentPlayer().getAlliance());

        return builder.build();
    }

    public static final class NormalMove extends Move {

        public NormalMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object o) {
            return this == o || o instanceof NormalMove && super.equals(o);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + BoardUtils.getPGNSquare(this.destinationPosition);
        }

    }

    public static class CaptureMove extends Move {
        final Piece attackedPiece;

        public CaptureMove(Board board, Piece movedPiece, int destinationPosition, Piece attackedPiece) {
            super(board, movedPiece, destinationPosition );
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof CaptureMove)) return false;

            final CaptureMove otherCaptureMove = (CaptureMove) o;
            return super.equals(otherCaptureMove) && getAttackedPiece().equals(otherCaptureMove.getAttackedPiece());
        }


        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    public static final class PawnMove extends Move {

        public PawnMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public  boolean equals(final Object o) {
            return this == o || o instanceof PawnMove && super.equals(o);
        }

        @Override
        public String toString() {
            return BoardUtils.getPGNSquare(this.destinationPosition);
        }

    }

    public static class PawnCaptureMove extends CaptureMove {

        public PawnCaptureMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object o) {
            return this == o || o instanceof PawnCaptureMove && super.equals(o);
        }

        @Override
        public String toString() {
            return BoardUtils.getPGNSquare(this.movedPiece.getPiecePosition()).substring(0,1) +
                   "x" +
                    BoardUtils.getPGNSquare(this.destinationPosition);
        }

    }

    public static class MajorCaptureMove extends CaptureMove {

        public MajorCaptureMove(Board board, Piece movedPiece, int destinationPosition, Piece attackedPiece) {
            super(board, movedPiece, destinationPosition, attackedPiece);
        }

        @Override
        public boolean equals (final Object o) {
            return this == o || o instanceof MajorCaptureMove && super.equals(o);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + BoardUtils.getPGNSquare(this.destinationPosition);
        }
    }

    public static final class PawnEnPassantCaptureMove extends PawnCaptureMove {

        public PawnEnPassantCaptureMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals (final Object o) {
            return this == o || o instanceof PawnEnPassantCaptureMove && super.equals(o);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            Iterable<Piece> activePieces = board.getAllActivePieces();

            for (final Piece piece : activePieces) {
                if (!this.movedPiece.equals(piece) && !(this.attackedPiece.equals(piece))) {
                    builder.setPiece(piece);
                }
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setNextTurnAlliance(this.board.getOpponentPlayer().getAlliance());
            return builder.build();
        }

    }

    public static final class PawnPromotionMove extends Move {
        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotionMove(final Move decoratedMove) { // see decorator design pattern
             super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationPosition());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object o) {
            return this == o || o instanceof PawnPromotionMove && super.equals(o);
        }

        @Override
        public Board execute() {
            final Board boardAfterMove = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            final Iterable<Piece> activePieces = boardAfterMove.getAllActivePieces();

            for (final Piece piece : activePieces) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setNextTurnAlliance(this.board.getOpponentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return ""; //TODO
        }



    }


    public static final class PawnJump extends Move {

        public PawnJump(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            final Iterable<Piece> activePieces = this.board.getAllActivePieces();

            for (final Piece piece : activePieces) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setNextTurnAlliance(this.board.getOpponentPlayer().getAlliance());

            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPGNSquare(this.destinationPosition); //TODO
        }
    }

    static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            final Iterable<Piece> activePieces = this.board.getAllActivePieces();

            for (final Piece piece : activePieces) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            final King movedKing = (King) this.movedPiece.movePiece(this);
            builder.setPiece(movedKing);
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getAlliance(), false));
            builder.setNextTurnAlliance(this.board.getOpponentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            CastleMove that = (CastleMove) o;
            return  super.equals(that) &&
                    this.castleRook.equals(that.getCastleRook()) ;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), castleRook, castleRookStart, castleRookDestination);
        }
    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O";
        }

        @Override
        public boolean equals (final Object o) {
            return this == o || o instanceof KingSideCastleMove && super.equals(o);
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                   final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }

        @Override
        public boolean equals (final Object o) {
            return this == o || o instanceof QueenSideCastleMove && super.equals(o);
        }
    }

    public static final class NullMove extends Move {

        public NullMove() {
            super(null,  65);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute null move!");
        }

        @Override
        public int getCurrentPosition() {
            return -1;
        }
    }

    public static class MoveFactory {

        private MoveFactory() {
            throw new RuntimeException("Class not instantiable!");
        }

        public static Move createMove(final Board board, final int currentPosition, final int destinationPosition) {

            for(final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentPosition() == currentPosition &&
                    move.getDestinationPosition() == destinationPosition) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
