package com.chess.engine.board;

/*
NOTES ON UTILITY CLASSES
- The constructor is private to prevent instantiation.
- The class is final to prevent sub-classing and to improve efficiency at runtime.
- The class is NOT abstract as this implies that the class is not concrete and must be implemented in some way.
    - Note: a concrete class is a class that has an implementation for all of its methods.

- Utility classes should generally not contain nested classes.
- Methods only used by the class itself should be private.
- The class should not have any non-final/non-static fields.

- The utility class can also be statically imported by other classes to improve code readability.
 */

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Board utility methods.
 *
 * The class is final to prevent sub-classing and to improve efficiency at runtime.
 */
public final class BoardUtils {

    private BoardUtils() {
        throw new RuntimeException("The BoardUtils class cannot be instantiated.");
    }

    public static final String[] PGN_SQUARES = initialisePGNSqaures();



    public static final Map<String, Integer> PGN_TO_Position = initialiseSquareToPositionMap();



    public static boolean isValidTilePosition(int position) {
        return 0 <= position && position < 64;
    }

    public static boolean isOnFirstFile(int position) {
        return position % 8 == 0;
    }

    public static boolean isOnLastFile(int position) {
        return position % 8 == 7;
    }

    public static boolean isOnEightRank(int position) { return 0 <= position && position <= 7; }
    public static boolean isOnSeventhRank(int position) {
        return 8 <= position && position <= 15;
    }
    public static boolean isOnSixthRank(int position) { return 16 <= position && position <= 23; }
    public static boolean isOnFifthRank(int position) { return 24 <= position && position <= 31; }
    public static boolean isOnFourthRank(int position) { return 32 <= position && position <= 39; }
    public static boolean isOnThirdRank(int position) { return 40 <= position && position <= 47; }
    public static boolean isOnSecondRank(int position) {
        return 48 <= position && position <= 55;
    }
    public static boolean isOnFirstRank(int position) { return 56 <= position && position <= 63; }

    private static String[] initialisePGNSqaures() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
    }

    private static Map<String, Integer> initialiseSquareToPositionMap() {
        final Map<String, Integer> squareToPositionMap = new HashMap<>();

        for (int i = 0; i < 63; i++) {
            squareToPositionMap.put(PGN_SQUARES[i], i);
        }

        return ImmutableMap.copyOf(squareToPositionMap);
    }

    public static String getPGNSquare(int positionCoordinate) {
        return PGN_SQUARES[positionCoordinate];
    }

    public static int getPositionFromPGNSquare(final String pgnSquare) {
        return PGN_TO_Position.get(pgnSquare);

    }

}
