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

/**
 * Board utility methods.
 *
 * The class is final to prevent sub-classing and to improve efficiency at runtime.
 */
public final class BoardUtils {

    private BoardUtils() {
        throw new RuntimeException("The BoardUtils class cannot be instantiated.");
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return 0 <= coordinate && coordinate < 64;
    }
}
