package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/*
Key Notes:
- An immutable object is an object whose internal state remains constant after is has been entirely created.
- An immutable class can provide static factories that cache frequently requested instances to avoid new instances when
  existing ones would do.

- ImmutableMap comes from google guava library - stops anyone from changing map.
 */

/**
 * The tileCoord variable is protected final so that it can only be accessed by subclasses and is immutable.
 */
public abstract class Tile {

    protected final int tilePosition; // stores coordinate of tile on chess board
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles(); // stores a map of empty squares

    /**
     * Creates the maximum number of empty squares that are needed for a chess game (64).
     * This is done so that new objects are not created each time an empty tile is needed.
     *
     * @return An immutable map filled with 64 empty squares.
     */
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i < 64; i++) {
            emptyTileMap.put(i, new EmptyTile(i)); // populates map with empty tiles
        }

        return ImmutableMap.copyOf(emptyTileMap);

    }

    /**
     * A public function for the user to create new tiles.
     * Ternary operator checks if the tile should contain a piece or not, then returns the appropriate tile.
     *
     * @param tilePosition tile coordinate
     * @param piece chess piece
     * @return An occupied tile or an empty tile
     */
    public static Tile createTile(final int tilePosition, final Piece piece) {
        return piece != null ? new OccupiedTile(tilePosition, piece) : EMPTY_TILES_CACHE.get(tilePosition);
    }

    /**
     * Constructor for abstract Tile class. Called by subclasses to select tile coordinate.
     *
     * @param tilePosition tile coordinate
     */
    private Tile(int tilePosition) {
        this.tilePosition = tilePosition; // tile coordinate
    }

    public int getTilePosition() {
        return this.tilePosition;
    }

    public abstract boolean isOccupied(); // checks if tile is empty

    public abstract Piece getPiece(); // returns current piece on tile

    /**
     * A nested class that defines an empty tile.
     *
     * Nested classes (as opposed to inner classes) cannot access other members of the enclosing class.
     * Important as EmptyTile/OccupiedTile should not inherit any state from Tile.
     *
     * A final class cannot be extended.
     */
    public static final class EmptyTile extends Tile {
        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public boolean isOccupied(){
            return false;
        }

        /**
         * @return null - no pieces on an empty tile.
         */
        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    /**
     * Nested class that defines tiles containing pieces (non-empty).
     */
    public static final class OccupiedTile extends Tile {
        private final Piece piece; // piece on tile

        /**
         * Constructor for an occupied tile.
         *
         * @param coordinate tile coordinate
         * @param piece chess piece
         */
        private OccupiedTile(final int coordinate, Piece piece) {
            super(coordinate);
            this.piece = piece;
        }

        /**
         * An occupied tile by definition will always be occupied.
         *
         * @return true
         */
        @Override
        public boolean isOccupied() {
            return true;
        }

        /**
         * @return The piece on the occupied tile.
         */
        @Override
        public Piece getPiece() {
            return this.piece;
        }

        @Override
        public String toString() {
            return this.getPiece().getAlliance().isBlack() ? this.getPiece().toString().toLowerCase() : this.getPiece().toString();
        }
    }


}
