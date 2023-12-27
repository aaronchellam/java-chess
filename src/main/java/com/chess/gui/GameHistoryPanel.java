package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {

    private static final Dimension MOVE_HISTORY_PANEL_DIMENSION = new Dimension(100, 400);
    private final DataModel model;
    private final JScrollPane scrollPane;

    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table); // allows for scrolling if there are too many moves in panel
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(MOVE_HISTORY_PANEL_DIMENSION);

        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board,
              final Table.MoveLog moveLog) {
        int currentRow = 0;
        this.model.clear();

        for (final Move move : moveLog.getMoves()) {
            final String moveText = move.toString();

            if (move.getMovedPiece().getAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            } else {
                this.model.setValueAt(moveText, currentRow, 1 );
                currentRow++;
            }
        }

        if (moveLog.getMoves().size() > 0) {
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();
            final String checkOrCheckmateString = calculateCheckandCheckmateString(board);

            if (lastMove.getMovedPiece().getAlliance().isWhite()) {
                this.model.setValueAt(moveText + checkOrCheckmateString, currentRow, 0); // TODO should last param be 0 or 1?

            } else {
                this.model.setValueAt(moveText + checkOrCheckmateString, currentRow - 1, 1);
            }

            final JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum()); // auto scrolls to last value
        }
    }

    private String calculateCheckandCheckmateString(Board board) {
        if (board.getCurrentPlayer().isInCheckMate()) {
            return "#";
        } else if (board.getCurrentPlayer().isInCheck()) {
            return "+";
        } else return "";
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row> rows;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.rows = new ArrayList<>();
        }

        public void clear() {
            this.rows.clear();
            this.setRowCount(0);
        }

        @Override
        public int getRowCount() {
            return this.rows == null ? 0 : this.rows.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.rows.get(row);

            if (column == 0) return currentRow.getWhiteMove();
            else if (column == 1) return currentRow.getBlackMove();
            else return null;
        }

        @Override
        public void setValueAt(final Object cellValue,
                               final int row,
                               final int column) {
            final Row currentRow;

            if (this.rows.size() <= row) { // fewer or equal rows than row index param
                currentRow = new Row();
                this.rows.add(currentRow);
            } else {
                currentRow = this.rows.get(row);
            }
            if (column == 0) {
                currentRow.setWhiteMove((String) cellValue);
                fireTableRowsInserted(row, row);
            } else {
                currentRow.setBlackMove((String) cellValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName( final int column) {
            return NAMES[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row() {

        }

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(String move) {
            this.blackMove = move;
        }
    }
}
