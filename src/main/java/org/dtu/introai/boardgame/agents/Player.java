package org.dtu.introai.boardgame.agents;

import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.util.Board;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player extends MouseAdapter implements Agent  {

    private final Lock lock;
    private final Condition clicked;
    private final int cellSize;

    private int clickedRow = -1;
    private int clickedCol = -1;

    public Player(int cellSize){
        this.cellSize = cellSize;
        this.lock = new ReentrantLock();
        this.clicked = lock.newCondition();
    }

    @Override
    public int[] act(Board board) throws InterruptedException {
        lock.lock();
        try {
            clicked.await(); // blocks until signal
            return new int[]{clickedRow, clickedCol};
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        lock.lock();
        try {
            clickedRow = e.getY() / cellSize;
            clickedCol = e.getX() / cellSize;
            clicked.signal(); // wake up waiting thread
        } finally {
            lock.unlock();
        }
    }
}
