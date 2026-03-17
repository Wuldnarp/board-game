package org.dtu.introai.boardgame;

import org.dtu.introai.boardgame.agents.MonteCarlo;
import org.dtu.introai.boardgame.agents.Player;
import org.dtu.introai.boardgame.othello.GameFrame;
import org.dtu.introai.boardgame.util.Cell;

public class Main {

    public static void main(String[] args) {

        new GameFrame(new MonteCarlo(Cell.BLACK, 500), new MonteCarlo(Cell.WHITE, 2000)).start();

    }
}