package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.util.Cell;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class GameWindow extends JPanel {

    private Othello othello;
    private int cellSize;

    public GameWindow(Othello othello, int cellSize){
        this.othello = othello;
        this.cellSize = cellSize;
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        for(int row = 0; row < othello.board.getPlayingBoard().length; row++){
            for(int col = 0; col < othello.board.getPlayingBoard()[row].length; col++){
                // Draw cell
                g.setColor(new Color(0, 128, 0)); // green board
                g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(col * cellSize, row * cellSize, cellSize, cellSize);

                // Draw piece
                if (othello.board.getPlayingBoard()[row][col] == Cell.WHITE){
                    g.setColor(Color.WHITE);
                }
                if (othello.board.getPlayingBoard()[row][col] == Cell.BLACK){
                    g.setColor(Color.BLACK);
                }
                if (othello.board.getPlayingBoard()[row][col] != Cell.EMPTY){
                    g.fillOval(col * cellSize + 10, row * cellSize + 10, 60, 60);
                }
            }
        }
    }
}
