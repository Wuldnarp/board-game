package org.dtu.introai.boardgame.api;

import org.dtu.introai.boardgame.util.Board;

public interface Agent {

    int[] act(Board board) throws InterruptedException;
}
