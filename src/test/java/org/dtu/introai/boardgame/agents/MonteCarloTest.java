package org.dtu.introai.boardgame.agents;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonteCarloTest {

    private MonteCarlo mc;
    private Board board;

    @BeforeEach
    void setup() {
        mc = new MonteCarlo(Cell.BLACK, 100);
        board = new Board(8);
    }

    // --- UCB ---

    @Test
    void UCB_unvisitedNode_returnsInfinity() {
        MCTreeNode parent = new MCTreeNode(board, null);
        MCTreeNode child = new MCTreeNode(board, parent);
        parent.incrementVisit();

        assertEquals(Double.POSITIVE_INFINITY, mc.UCB(child, parent));
    }

    @Test
    void UCB_visitedNode_returnsFiniteValue() {
        MCTreeNode parent = new MCTreeNode(board, null);
        MCTreeNode child = new MCTreeNode(board, parent);
        parent.incrementVisit();
        child.incrementVisit();
        child.incrementWins();

        double result = mc.UCB(child, parent);
        assertTrue(Double.isFinite(result));
    }

    // --- selectChildWithHighestUCB1 ---

    @Test
    void selectChildWithHighestUCB1_returnsChildWithHighestScore() {
        MCTreeNode parent = new MCTreeNode(board, null);
        parent.incrementVisit();
        parent.incrementVisit();

        MCTreeNode child1 = new MCTreeNode(board, parent);
        child1.incrementVisit();
        // no wins — low exploitation

        MCTreeNode child2 = new MCTreeNode(board, parent);
        child2.incrementVisit();
        child2.incrementWins();
        // 1 win — higher exploitation

        parent.addChild(child1);
        parent.addChild(child2);

        MCTreeNode best = mc.selectChildWithHighestUCB1(parent);
        assertEquals(child2, best);
    }

    // --- backPropagate ---

    @Test
    void backPropagate_win_incrementsWinsUpChain() {
        MCTreeNode root = new MCTreeNode(board, null);
        MCTreeNode child = new MCTreeNode(board, root);

        mc.backPropagate(Cell.BLACK, child);

        assertEquals(1, child.getWins());
        assertEquals(1, root.getWins());
        assertEquals(1, child.getVisits());
        assertEquals(1, root.getVisits());
    }

    @Test
    void backPropagate_loss_incrementsLossesUpChain() {
        MCTreeNode root = new MCTreeNode(board, null);
        MCTreeNode child = new MCTreeNode(board, root);

        mc.backPropagate(Cell.WHITE, child);

        assertEquals(1, child.getLosses());
        assertEquals(1, root.getLosses());
    }

    @Test
    void backPropagate_draw_onlyIncrementsVisits() {
        MCTreeNode root = new MCTreeNode(board, null);
        MCTreeNode child = new MCTreeNode(board, root);

        mc.backPropagate(Cell.EMPTY, child);

        assertEquals(0, child.getWins());
        assertEquals(0, child.getLosses());
        assertEquals(1, child.getVisits());
    }

    // --- expand ---

    @Test
    void expand_addsChildToLeaf() {
        MCTreeNode leaf = new MCTreeNode(board, null);
        mc.expand(leaf);
        assertEquals(1, leaf.getChildren().size());
    }

    @Test
    void expand_childHasDifferentBoard() {
        MCTreeNode leaf = new MCTreeNode(board, null);
        MCTreeNode child = mc.expand(leaf);
        assertNotSame(board, child.getBoard());
    }

    @Test
    void expand_childParentIsLeaf() {
        MCTreeNode leaf = new MCTreeNode(board, null);
        MCTreeNode child = mc.expand(leaf);
        assertEquals(leaf, child.getParent());
    }
}
