package org.dtu.introai.boardgame;

import org.dtu.introai.boardgame.agents.Player;
import org.dtu.introai.boardgame.othello.GameFrame;

public class Main {

    public static void main(String[] args) {

        new GameFrame(new Player(), new Player()).start();

    }
}