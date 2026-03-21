package org.dtu.introai.boardgame;

import org.dtu.introai.boardgame.agents.MonteCarlo;
import org.dtu.introai.boardgame.agents.Player;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.GameFrame;
import org.dtu.introai.boardgame.util.Cell;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: mvn exec:java -Dexec.args=\"<agent1> <agent2> <time1> [time2]\"");
            System.out.println();
            System.out.println("  agent1   player | ai   BLACK (goes first)");
            System.out.println("  agent2   player | ai   WHITE");
            System.out.println("  time1    ms            thinking time for agent1 (and agent2 if time2 omitted)");
            System.out.println("  time2    ms            (optional) thinking time for agent2");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("  player vs ai:           -Dexec.args=\"player ai 1000\"");
            System.out.println("  ai vs ai (same time):   -Dexec.args=\"ai ai 1000\"");
            System.out.println("  ai vs ai (diff time):   -Dexec.args=\"ai ai 1000 100\"");
            return;
        }

        if (args[0].equals("benchmark")) {
            Benchmark.main(java.util.Arrays.copyOfRange(args, 1, args.length));
            return;
        }

        String agent1 = args[0];
        String agent2 = args[1];
        long time1 = Long.parseLong(args[2]);
        long time2 = args.length > 3 ? Long.parseLong(args[3]) : time1;

        Agent blackAgent;
        if(agent1.equals("player")) {
            blackAgent = new Player();
        }else{
            blackAgent = new MonteCarlo(Cell.BLACK, time1);
        }

        Agent whiteAgent;
        if(agent2.equals("ai")){
            whiteAgent = new MonteCarlo(Cell.WHITE, time2);
        }else{
            whiteAgent = new Player();
        }

        new GameFrame(blackAgent, whiteAgent).start();

    }
}