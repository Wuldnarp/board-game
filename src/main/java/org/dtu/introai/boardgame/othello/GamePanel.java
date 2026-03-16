package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.util.Cell;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GamePanel extends JPanel {

    private Othello othello;
    private GameCell[][] gameCells;
    CompletableFuture<String> buttonPressed;

    public GamePanel(Othello othello){
        this.othello = othello;
        setBackground(Color.WHITE);
        setFocusable(true);

        this.buttonPressed = new CompletableFuture<>();
        this.gameCells = new GameCell[othello.board.boardSize][othello.board.boardSize];

        setLayout(new GridLayout(othello.board.boardSize,othello.board.boardSize, 2,2));
        setBackground(Color.BLACK);

        for(int i = 0; i < othello.board.getPlayingBoard().length; i++){
            for(int j = 0; j < othello.board.getPlayingBoard()[i].length; j++){
                GameCell gameCell = new GameCell(othello.board.getPlayingBoard()[i][j]);
                int finalI = i;
                int finalJ = j;
                gameCell.addActionListener(e -> buttonPressed.complete(finalI +","+ finalJ));
                add(gameCell);
                gameCells[i][j] = gameCell;
            }
        }
    }

    void showAvailable(Cell cell){
        for(int[] available : othello.board.getAllLegalMoves(cell)){
            gameCells[available[0]][available[1]].setAvailable(true);
        }
    }

    void hideAvailable(){
        for(GameCell[] rows : gameCells){
            for(GameCell cell : rows){
                cell.setAvailable(false);
            }
        }
    }

    int[] getInput() throws ExecutionException, InterruptedException {
        String result = buttonPressed.get();
        String[] parts = result.split(",");

        int[] input = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            input[i] = Integer.parseInt(parts[i].trim());
        }
        return input;
    }

    void setGameCells(Cell[][] cells){
        for(int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                gameCells[i][j].setCell(cells[i][j]);
            }
        }
    }
}
