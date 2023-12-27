package com.chess.engine.player;

public enum MoveStatus {
    DONE,
    ILLEGAL_MOVE,
    PLAYER_IN_CHECK;

    public boolean isDone() {
        return this == DONE;
    }
}
