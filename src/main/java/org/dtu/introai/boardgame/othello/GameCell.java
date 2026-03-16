package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.util.Cell;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GameCell extends JButton {

    Cell cell;
    boolean available;

    GameCell(Cell cell){
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        this.cell = cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Fill the button background
        g2.setColor(Color.GREEN);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if(cell.equals(Cell.WHITE)){
            g2.setColor(Color.WHITE);
        } else if (cell.equals(Cell.BLACK)){
            g2.setColor(Color.BLACK);
        }

        int diameter = Math.min(getWidth(), getHeight()) - 10;

        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        if (!cell.equals(Cell.EMPTY)) {
            if (cell.equals(Cell.WHITE)) g2.setColor(Color.WHITE);
            else g2.setColor(Color.BLACK);
            g2.fillOval(x, y, diameter, diameter);
        } else if (available) {
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x, y, diameter, diameter);
        }
    }

}
