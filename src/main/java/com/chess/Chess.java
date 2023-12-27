package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;

import java.io.IOException;

public class Chess {

    public static void main(String[] args) throws IOException {
        Board board = Board.createDefaultBoard();

        System.out.println(board);

        Table.get().show();
    }
}
