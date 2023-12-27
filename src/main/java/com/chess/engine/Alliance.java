package com.chess.engine;

/*
NOTES ON ENUMS

- Enums are special classes that represent a group of constants (similar to final variables).
 */


import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

/**
 * This enum defines whether pieces/players are white or black.
 */
public enum Alliance {
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.isOnEightRank(position);
        }
    }, BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.isOnFirstRank(position);
        }
    };

    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(int position);

    public int getOppositeDirection() {
        return this.getDirection() * (-1);
    }

    public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
        if (this == WHITE) {
            return whitePlayer;
        } else {
            return blackPlayer;
        }
    }


}
