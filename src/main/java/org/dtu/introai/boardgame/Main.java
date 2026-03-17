package org.dtu.introai.boardgame;

import org.dtu.introai.boardgame.agents.MonteCarlo;
import org.dtu.introai.boardgame.agents.Player;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.GameFrame;
import org.dtu.introai.boardgame.util.Cell;

public class Main {

    public static void main(String[] args) {

        String agent1 = args[0];
        String agent2 = args[1];
        long time = Long.parseLong(args[2]);

        Agent blackAgent;
        if(agent1.equals("player")) {
            blackAgent = new Player();
        }else{
            blackAgent = new MonteCarlo(Cell.BLACK,time);
        }

        Agent whiteAgent;
        if(agent2.equals("ai")){
            whiteAgent = new MonteCarlo(Cell.WHITE,time);
        }else{
            whiteAgent = new Player();
        }

        new GameFrame(blackAgent, whiteAgent).start();

    }
}