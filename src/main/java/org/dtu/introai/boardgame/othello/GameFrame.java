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
import java.util.stream.Collectors;

public class GameFrame extends JFrame {

    Othello othello;
    GamePanel gamePanel;
    TextArea textArea;
    HashMap<Cell, Agent> agentMap;
    Cell currant;
    boolean skippedLastAgent;

    public GameFrame(){
        super("Othello");

        this.othello = new Othello(new Board(8));
        this.gamePanel = new GamePanel(othello);
        this.textArea = new TextArea();
        this.agentMap = new HashMap<>();

        agentMap.put(Cell.BLACK, new Player());
        agentMap.put(Cell.WHITE, new Player());

        this.currant = Cell.BLACK;

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(textArea, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 800);
        setVisible(true);
        // start game in another Thread
        new Thread(this::start).start();
    }

    private void start() {

        while (true){
            updateText();

            if(othello.getBoard().getAllLegalMoves(currant).isEmpty()){
                if(skippedLastAgent || othello.getBoard().getAllLegalMoves(reverse(currant)).isEmpty()){
                    break;
                }
                currant = reverse(currant);
                skippedLastAgent = true;
                continue;
            }
            skippedLastAgent = false;

            if(agentMap.get(currant) instanceof Player){
                playerLoop();
            }else{
                agentLoop();
            }

            SwingUtilities.invokeLater(() -> {
                gamePanel.hideAvailable();
                gamePanel.setGameCells(othello.getBoard().getPlayingBoard());
                repaint();
            });

            currant = reverse(currant);
        }

        othello.setComplete(true);
        printFinishText();
        System.out.println("game has ended");
    }

    void playerLoop(){
        gamePanel.buttonPressed = new CompletableFuture<>(); // reset first
        SwingUtilities.invokeLater(() -> gamePanel.showAvailable(currant));

        boolean status;
        do {
            int[] move = null;
            try {
                move = gamePanel.getInput();
            } catch (ExecutionException | InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            status = othello.setPiece(move[0], move[1], currant);
            if (!status) {
                gamePanel.buttonPressed = new CompletableFuture<>(); // reset on bad move
            }
        } while (!status);

    }
    void agentLoop(){
        boolean status;
        do{
            int[] move = agentMap.get(currant).act(othello.getBoard());
            status = othello.setPiece(move[0],move[1],currant);
        }while (!status);
    }

    void updateText(){
        SwingUtilities.invokeLater(()->
            textArea.setText(
                    "Currant player: " + currant +"\n"+
                    "Available moves: " + othello.board.getAllLegalMoves(currant).stream()
                            .map(Arrays::toString)
                            .collect(Collectors.joining(", ", "[", "]")) +"\n"+
                    "Score: " + othello.board.countPieces()
            )
        );
    }

    void printFinishText(){
        SwingUtilities.invokeLater(()->
                textArea.setText(
                        "The game is over \n" +
                        "The winder is: " + othello.getWiner() + "\n" +
                        "The Final score is: " + othello.board.countPieces()
                )
        );
    }

    private Cell reverse(Cell cell){
        return cell.equals(Cell.WHITE) ? Cell.BLACK : Cell.WHITE;
    }
}
