package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.agents.Player;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.TextArea;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameFrame extends JFrame {

    Othello othello;
    GamePanel gamePanel;
    TextArea textArea;
    HashMap<Cell, Agent> agentMap;

    public GameFrame(Agent agent1, Agent agent2){
        super("Othello");

       this.agentMap = new HashMap<>();

        agentMap.put(Cell.BLACK, agent1);
        agentMap.put(Cell.WHITE, agent2);

        HashMap<Cell, Supplier<int[]>> supplyMap = new HashMap<>();

        if(agent1 instanceof Player){
            supplyMap.put(Cell.BLACK, playerLoop());
        } else{
            supplyMap.put(Cell.BLACK,agentLoop());
        }
        if(agent2 instanceof Player){
            supplyMap.put(Cell.WHITE,playerLoop());
        }else {
            supplyMap.put(Cell.WHITE,agentLoop());
        }

        this.othello = new Othello(new Board(8),agentMap,supplyMap);
        this.gamePanel = new GamePanel(othello);
        this.textArea = new TextArea();

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(textArea, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 800);
        setVisible(true);
    }

    public void start() {

        othello.gameLoop(updateCycle(),printFinishText());
        printFinishText();
    }

    Consumer<Object> updateCycle(){
        return new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                SwingUtilities.invokeLater(() -> {
                    gamePanel.hideAvailable();
                    gamePanel.setGameCells(othello.getBoard().getPlayingBoard());
                    updateText();
                    repaint();
                });
            }
        };
    }

    Supplier<int[]> playerLoop(){
        return new Supplier<int[]>() {
            @Override
            public int[] get() {
                SwingUtilities.invokeLater(() -> gamePanel.showAvailable(othello.currant));
                gamePanel.buttonPressed = new CompletableFuture<>();
                int[] move = null;
                try {
                    move = gamePanel.getInput();
                } catch (ExecutionException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return move;
            }
        };
    }

    Supplier<int[]> agentLoop(){
        return new Supplier<int[]>() {
            @Override
            public int[] get() {
                return agentMap.get(othello.currant).act(othello.getBoard());
            }
        };
    }

    void updateText(){
        SwingUtilities.invokeLater(()->
                textArea.setText(
                        "Currant player: " + othello.currant +"\n"+
                                "Available moves: " + othello.board.getAllLegalMoves(othello.currant).stream()
                                .map(Arrays::toString)
                                .collect(Collectors.joining(", ", "[", "]")) +"\n"+
                                "Score: " + othello.board.countPieces()
                )
        );
    }

    Consumer<Object> printFinishText(){
        return new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                SwingUtilities.invokeLater(()->
                        textArea.setText(
                                "The game is over \n" +
                                        "The winder is: " + othello.getWiner() + "\n" +
                                        "The Final score is: " + othello.board.countPieces()
                        )
                );
            }
        };
    }
}
