package com.chess.gui;

import com.chess.engine.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Allow options to select Human/Computer vs Human/Computer
// JDialog is a top level container used to take some form of user input.
class GameSetup extends JDialog {
    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private JSpinner searchDepthSpinner;

    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";

    GameSetup(JFrame frame, boolean modal) {
        super(frame, modal);

        // Instantiate Buttons and JPanel.
        JPanel myPanel = new JPanel(new GridLayout(0, 1));
        JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
        JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
        JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
        JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);

        // White player.
        whiteHumanButton.setActionCommand(HUMAN_TEXT);
        ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        // Black player.
        ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackHumanButton.setSelected(true);

        // Add buttons to panel.
        getContentPane().add(myPanel);
        myPanel.add(new JLabel("White"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);

        myPanel.add(new JLabel("Black"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        // Add depth spinner.
        this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(6, 0,
                                                                                                    40
                , 1));

        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");


        okButton.addActionListener(e -> {
            // Set player types based upon options.
            whitePlayerType = whiteHumanButton.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
            blackPlayerType = blackHumanButton.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
            GameSetup.this.setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            System.out.println("Cancel");

            // Dialog window not visible if user presses cancel.
            GameSetup.this.setVisible(false);
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    // Called by the table class.
    void promptUser() {
        setVisible(true);
        repaint();
    }
    
    boolean isAIPlayer(Player player) {
        if(player.getAlliance().isWhite()) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        } else {
            return getBlackPlayerType() == PlayerType.COMPUTER;
        }
         
    }

    private PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }

    private PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    private JSpinner addLabeledSpinner(Container c, String label,
                                       SpinnerModel model) {
        JLabel l = new JLabel(label);
        c.add(l);

        JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);

        return spinner;
    }


    int getSearchDepth() {
        return (Integer) this.searchDepthSpinner.getValue();
    }


}
