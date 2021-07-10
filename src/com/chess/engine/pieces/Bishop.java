package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishop extends Piece {
    private final static int[] bishopModifiers = {7, 9};

    public Bishop(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }


    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        return null;
    }

    private static boolean isValidBishopSquare(int currentPosition, int newPosition){
        return (currentPosition % 9 == newPosition % 9) || (currentPosition % 7 == newPosition % 7);
    }
}
