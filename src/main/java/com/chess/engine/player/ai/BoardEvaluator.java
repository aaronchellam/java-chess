package com.chess.engine.player.ai;

import com.chess.engine.board.Board;

// Evaluation function for minimax algorithm.
public interface BoardEvaluator {

    /**
     * Positive evaluation -> white has the better position.
     * Negative evaluation -> black has the better position.
     *
     * @param board
     * @param depth
     * @return Evaluation of position.
     */
    int evaluate(Board board, int depth);
}
