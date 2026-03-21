package org.dtu.introai.boardgame;

import org.dtu.introai.boardgame.agents.MonteCarlo;
import org.dtu.introai.boardgame.othello.Othello;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

import java.util.HashMap;
import java.util.function.Supplier;

public class Benchmark {

    public static void main(String[] args) {
        int games = args.length > 0 ? Integer.parseInt(args[0]) : 50;
        long time1 = args.length > 1 ? Long.parseLong(args[1]) : 1000;
        long time2 = args.length > 2 ? Long.parseLong(args[2]) : 100;

        int blackWins = 0;
        int whiteWins = 0;
        int draws = 0;

        System.out.println("Running " + games + " games: BLACK=" + time1 + "ms vs WHITE=" + time2 + "ms");

        for (int i = 0; i < games; i++) {
            MonteCarlo black = new MonteCarlo(Cell.BLACK, time1);
            MonteCarlo white = new MonteCarlo(Cell.WHITE, time2);

            HashMap<Cell, Supplier<int[]>> supplyMap = new HashMap<>();
            Board board = new Board(8);
            Othello game = new Othello(board, supplyMap);

            supplyMap.put(Cell.BLACK, () -> black.act(game.getBoard()));
            supplyMap.put(Cell.WHITE, () -> white.act(game.getBoard()));

            game.gameLoop(o -> {}, o -> {});

            Cell winner = game.getWinner();
            if (winner == Cell.BLACK) blackWins++;
            else if (winner == Cell.WHITE) whiteWins++;
            else draws++;

            System.out.println("Game " + (i + 1) + ": " + winner);
        }

        System.out.println();
        System.out.println("Results after " + games + " games:");
        System.out.println("  BLACK (" + time1 + "ms): " + blackWins + " wins");
        System.out.println("  WHITE (" + time2 + "ms): " + whiteWins + " wins");
        System.out.println("  Draws:              " + draws);
    }
}
