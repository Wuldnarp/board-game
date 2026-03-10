package org.dtu.introai.boardgame;

import org.dtu.introai.boardgame.agents.Player;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.GameWindow;
import org.dtu.introai.boardgame.othello.Othello;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.event.MouseListener;
import java.util.HashMap;

public class Main {

    Othello othello;
    GameWindow gameWindow;
    HashMap<Cell,Agent> agentMap;
    Cell currant;

    boolean skippedLastAgent;

    private Main(){
        this.othello = new Othello(new Board(8));
        this.gameWindow = new GameWindow(othello,80);
        this.agentMap = new HashMap<>();

        agentMap.put(Cell.BLACK, new Player(80));
        agentMap.put(Cell.WHITE, new Player(80));

        this.currant = Cell.BLACK;

        gameWindow.addMouseListener((MouseListener) agentMap.get(Cell.BLACK));
        gameWindow.addMouseListener((MouseListener) agentMap.get(Cell.WHITE));
    }

    private void start() throws InterruptedException {

        while (true){
            if(othello.getBoard().getAllLegalMoves(currant).isEmpty()){
                if(skippedLastAgent || othello.getBoard().getAllLegalMoves(reverse(currant)).isEmpty()){
                    break;
                }
                currant = reverse(currant);
                skippedLastAgent = true;
                continue;
            }
            skippedLastAgent = false;


            boolean status;
            do{
                int[] move = agentMap.get(currant).act(othello.getBoard());
                status = othello.setPiece(move[0],move[1],currant);
            }while (!status);

            currant = reverse(currant);
        }
    }

    private Cell reverse(Cell cell){
        return cell.equals(Cell.WHITE) ? Cell.WHITE : Cell.BLACK;
    }

    public static void main(String[] args) throws InterruptedException {

        Main game = new Main();

        JFrame frame = new JFrame("Othello");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 800);
        frame.add(game.gameWindow);
        frame.setVisible(true);

        game.start();
    }
}