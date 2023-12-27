package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

import java.util.Collection;
//TODO: Implement alpha-beta pruning.
public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MiniMax(int depth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = depth;
    }

    /**
     * The AI invokes the execute() method on a given board, at a given depth. It uses the minimax algorithm to
     * determine the optimal move at the given depth.
     *
     * @return The move to be made
     */
    @Override
    public Move execute(Board board) {

        Move bestMove = null;

        int maxEval = Integer.MIN_VALUE;
        int minEval = Integer.MAX_VALUE;
        int currentValue;

        Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
        int numMoves = legalMoves.size();

        for(Move move: legalMoves) {
            MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {

                // Ternary: if current player is white, call min; otherwise, call max.
                // This is because white is the maximising player and black is the minimising player.
                currentValue =
                        currentPlayerIsWhite(board) ?
                        min(moveTransition.getTransitionBoard(), this.searchDepth-1) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth-1);

                if (currentPlayerIsWhite(board) && currentValue > maxEval) {
                    maxEval = currentValue;
                    bestMove = move;
                } else if(currentPlayerIsBlack(board) && currentValue < minEval) {
                    minEval = currentValue;
                    bestMove = move;
                }
            }

            return bestMove;
        }



        return null;
    }

    private boolean currentPlayerIsBlack(Board board) {
        return board.getCurrentPlayer().getAlliance().isBlack();
    }

    private boolean currentPlayerIsWhite(Board board) {
        return board.getCurrentPlayer().getAlliance().isWhite();
    }

    public int max(Board board, int depth) {
        if (depth == 0 || isEndGameScenario(board)) { // todo: or game is over.
            return this.boardEvaluator.evaluate(board, depth);
        }

        int maxEval = Integer.MIN_VALUE;

        for (Move move: board.getCurrentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            Board transitionBoard = moveTransition.getTransitionBoard();

            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = min(transitionBoard, depth-1);

                if (currentValue >= maxEval) {
                    maxEval = currentValue;
                }
            }
        }

        return maxEval;
    }

    public int min(Board board, int depth) {
        // Evaluate once max depth reached.
        if(depth == 0 || isEndGameScenario(board)) { // todo: Or if game is over.
            return this.boardEvaluator.evaluate(board, depth);
        }

        int minEval = Integer.MAX_VALUE;

        for (Move move: board.getCurrentPlayer().getLegalMoves()) {
            // Get the MoveTransition object for all of the player's legal moves.
            // For each move that the current player might make, the algorithm must evaluate the strength of the
            // position for White and Black.
            MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);

            // The transition board is passed recursively between min and max.
            Board transitionBoard = moveTransition.getTransitionBoard();

            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = max(transitionBoard, depth-1);

                // If new min value is found, update.
                // This corresponds to finding a better move for Black.
                if (currentValue <= minEval) {
                    minEval = currentValue;
                }
            }

        }

        return minEval;

    }

    private static boolean isEndGameScenario(Board board) {
        return board.getCurrentPlayer().isInCheckMate() || board.getCurrentPlayer().isInStalemate()
                || board.getOpponentPlayer().isInCheckMate() || board.getOpponentPlayer().isInStalemate();
    }


}
