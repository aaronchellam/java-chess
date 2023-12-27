package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.chess.engine.board.BoardUtils.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;

    // Game Setup.
    private final GameSetup gameSetup;

    // AI
    private Move computerMove;


    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece activePiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 400);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static String  pieceImagesPath = "art/pieces/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");


    private static Table INSTANCE;

    static {
        try {
            INSTANCE = new Table();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Table is a singleton class.
    private Table() throws IOException {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.chessBoard = Board.createDefaultBoard();

        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;
        
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();

        // Tell the AI watcher to observe the table
        this.addObserver(new TableGameAIWatcher());

        // model == true means that the user must fill in the info before leaving the frame.
        this.gameSetup = new GameSetup(this.gameFrame, true);

        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(gameHistoryPanel, BorderLayout.EAST);

        this.gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() throws IOException {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    /**
     * Creates and returns a menu bar.
     *
     * @return A populated JMenuBar.
     */
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("FILE");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file.");

            }
        });

        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Close game
            }
        });

        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferenceMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");

        flipBoardMenuItem.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            try {
                boardPanel.drawBoard(chessBoard);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });



        final JCheckBoxMenuItem highlightLegalsCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        highlightLegalsCheckbox.addActionListener(e -> highlightLegalMoves = highlightLegalsCheckbox.isSelected());

        preferenceMenu.add(highlightLegalsCheckbox);
        preferenceMenu.add(flipBoardMenuItem);
        return preferenceMenu;
    }

    private JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");

        setupGameMenuItem.addActionListener(e -> {
            Table.get().getGameSetup().promptUser();
            Table.get().setupUpdate(Table.get().getGameSetup());
        });

        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;

    }


    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            Player currentPlayer = Table.get().getGameBoard().getCurrentPlayer();

            // Make the AI move.
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer())
            && !currentPlayer.isInCheckMate()
            && !currentPlayer.isInStalemate()
            ) {
                // Create an AI thread.
                // Execute AI work.
                AIEngine engine = new AIEngine();
                engine.execute();

            }

            if (currentPlayer.isInCheckMate() || currentPlayer.isInStalemate()) {
                System.out.println("Game Over.");
            }
        }
    }

    // Swingworker is a means of threading in java.
    private static class AIEngine extends SwingWorker<Move, String> {
        private AIEngine() {

        }

        // Minimax Algorithm is actually invoked here.
        @Override
        protected Move doInBackground() {
            MoveStrategy miniMax = new MiniMax(4);
            Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        // When the swing worker has finished, cleanup work is performed here.
        @Override
        public void done() {
            Player currentPlayer = Table.get().getGameBoard().getCurrentPlayer();

            try {
                Move bestMove = get();
                Table.get().updateComputerMove(bestMove);

                // Update board after computer makes move.
                Table.get().updateGameBoard(currentPlayer.makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch(InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);

    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    public void updateComputerMove(Move move) {
        this.computerMove = move;
    }

    public void updateGameBoard(Board board) {
        this.chessBoard = board;
    }

    // Use observer pattern to notify AI to make move after human makes a change.
    private void setupUpdate(GameSetup gameSetup) {
        // GameSetup has changed.
        setChanged();

        // Notify the observers of the GameSetup.
        notifyObservers();

    }



    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        /**
         * The Chess Board
         */
        public BoardPanel() throws IOException {
            super(new GridLayout(8, 8)); // 8 x 8 grid
            this.boardTiles = new ArrayList<>();

            for(int i = 0; i < 64; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel); // adds tile to boardTiles list
                add(tilePanel); // adds tile to this BoardPanel container
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate(); // validates component sizes within container
        }

        public void drawBoard(final Board board) throws IOException {
            removeAll();

            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel {
        private final int tileID;


        /**
         * Individual Tiles
         *
         * @param boardPanel The Chess Board
         * @param tileID Tile Position
         */
        public TilePanel(final BoardPanel boardPanel, final int tileID) throws IOException {
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColour();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        activePiece = null;
                    } else if (isLeftMouseButton(e)) {
                        // first click
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileID);
                            activePiece = sourceTile.getPiece();
                            if (activePiece == null) {
                                sourceTile = null;
                            } else {
                                highlightLegals(chessBoard);
                            }
                        } else {
                            // second click
                            destinationTile = chessBoard.getTile(tileID);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTilePosition(), destinationTile.getTilePosition());
                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);

                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }

                            sourceTile = null;
                            destinationTile = null;
                            activePiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                            try {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);

                                // NOTIFY THE AI!
                                if (gameSetup.isAIPlayer(chessBoard.getCurrentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }

                                boardPanel.drawBoard(chessBoard); // re-draw board


                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll(); // remove all components added to the TilePanel

            if (board.getTile(this.tileID).isOccupied()) { // if piece on tile
                try {
                    final Piece piece = board.getTile(this.tileID).getPiece();
                    final BufferedImage image =
                            ImageIO.read(new File(pieceImagesPath +
                            piece.getAlliance().toString().substring(0, 1) +
                            piece.toString() +
                            ".gif"));

                    add(new JLabel(new ImageIcon(image))); // add() is analagous to this.add() where "this" is the JPanel
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) { // user can set preference for highlighting legal moves
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationPosition() == this.tileID) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png"))))); //TODO
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {

            if (activePiece != null && activePiece.getAlliance() == board.getCurrentPlayer().getAlliance()) {
                final Collection<Move> moves = new ArrayList<>();
                if (activePiece.getPieceType().isKing()) {
                    moves.addAll(board.getCurrentPlayer().calculateKingCastles(board.getCurrentPlayer().getLegalMoves(), board.getOpponentPlayer().getLegalMoves()));
                }
                moves.addAll(activePiece.calculateLegalMoves(board));
                return moves;
            }
            return Collections.emptyList(); // no legal moves
        }

        private void assignTileColour() {
            boolean isLight = ((tileID + tileID / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
        }

        public void drawTile(Board board) throws IOException {
            assignTileColour();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }
    }

    /**
     * Stores moves made during the game.
     */
    public static class MoveLog {

        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return moves;
        }


        public void addMove(final Move move) {
            moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(final int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    public enum BoardDirection {
        NORMAL,
        FLIPPED;

        BoardDirection opposite() {
            return this == NORMAL ? FLIPPED : NORMAL;
        }

        List<TilePanel> traverse(final List<TilePanel> boardTiles) {
            if (this == NORMAL) {
                return boardTiles;
            } else {
                return Lists.reverse(boardTiles);
            }
        }
    }
}
