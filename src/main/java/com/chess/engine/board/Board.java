package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;


import java.util.*;

import static com.chess.engine.Alliance.BLACK;
import static com.chess.engine.Alliance.WHITE;

public class Board {
    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;


    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Player opponentPlayer;

    private final Pawn enPassantPawn;

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextTurnAlliance.choosePlayer(this.whitePlayer, this.blackPlayer);
        this.opponentPlayer = currentPlayer.getOpponent();

    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if ( (i+1) % 8 == 0) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    public Collection<Piece> getWhitePieces() { return this.whitePieces; }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public WhitePlayer getWhitePlayer() {
        return this.whitePlayer;
    }

    public BlackPlayer getBlackPlayer() {
        return this.blackPlayer;
    }

    public Player getCurrentPlayer() { return this.currentPlayer; }

    public Player getOpponentPlayer() { return this.opponentPlayer; }



    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this)); // adds all legal moves for each piece
        }
        return ImmutableList.copyOf(legalMoves);
    }


    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();

        for (final Tile tile : gameBoard) {
            if (tile.isOccupied()) {
                final Piece pieceOnTile = tile.getPiece();
                if (pieceOnTile.getAlliance() == alliance) {
                    activePieces.add(pieceOnTile);
                }
            }
        }

        return ImmutableList.copyOf(activePieces);
    }







    public Tile getTile(final int tilePosition) {
        return gameBoard.get(tilePosition);
    }

    private static List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[64];
        for (int i = 0; i < 64; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    public static Board createDefaultBoard() {
        final Builder builder = new Builder();

        builder.setPiece(new Rook(0, BLACK));
        builder.setPiece(new Knight(1, BLACK));
        builder.setPiece(new Bishop(2, BLACK));
        builder.setPiece(new Queen(3, BLACK));
        builder.setPiece(new King(4, BLACK));
        builder.setPiece(new Bishop(5, BLACK));
        builder.setPiece(new Knight(6, BLACK));
        builder.setPiece(new Rook(7, BLACK));
        builder.setPiece(new Pawn(8, BLACK));
        builder.setPiece(new Pawn(9, BLACK));
        builder.setPiece(new Pawn(10, BLACK));
        builder.setPiece(new Pawn(11, BLACK));
        builder.setPiece(new Pawn(12, BLACK));
        builder.setPiece(new Pawn(13, BLACK));
        builder.setPiece(new Pawn(14, BLACK));
        builder.setPiece(new Pawn(15, BLACK));

        builder.setPiece(new Pawn(48, WHITE));
        builder.setPiece(new Pawn(49, WHITE));
        builder.setPiece(new Pawn(50, WHITE));
        builder.setPiece(new Pawn(51, WHITE));
        builder.setPiece(new Pawn(52, WHITE));
        builder.setPiece(new Pawn(53, WHITE));
        builder.setPiece(new Pawn(54, WHITE));
        builder.setPiece(new Pawn(55, WHITE));
        builder.setPiece(new Rook(56, WHITE));
        builder.setPiece(new Knight(57, WHITE));
        builder.setPiece(new Bishop(58, WHITE));
        builder.setPiece(new Queen(59, WHITE));
        builder.setPiece(new King(60, WHITE));
        builder.setPiece(new Bishop(61, WHITE));
        builder.setPiece(new Knight(62, WHITE));
        builder.setPiece(new Rook(63, WHITE));

        builder.setNextTurnAlliance(WHITE); // white moves first

        return builder.build();
    }

    /**
     * Makes use of Guava methods to concatenate the whitePlayer's legal moves with the blackPlayer's legal moves.
     *
     * @return An Iterable of all legal moves on the board.
     */
    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public Iterable<Piece> getAllActivePieces() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePieces, this.blackPieces));
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }


    public static class Builder {
        Map<Integer, Piece> boardConfig; // map positions to pieces
        Alliance nextTurnAlliance; // player who moves next
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap<Integer, Piece>() {
            };
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setNextTurnAlliance(final Alliance nextTurnAlliance) {
            this.nextTurnAlliance = nextTurnAlliance;
            return this;
        }


        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
