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

        assertEquals(Double.POSITIVE_INFINITY, mc.upperConfidentBound(child, parent));
    }

    @Test
    void UCB_visitedNode_returnsFiniteValue() {
        MCTreeNode parent = new MCTreeNode(board, null);
        MCTreeNode child = new MCTreeNode(board, parent);
        parent.incrementVisit();
        child.incrementVisit();
        child.incrementWins();

        double result = mc.upperConfidentBound(child, parent);
        assertTrue(Double.isFinite(result));
    }

    // --- selectChildWithHighestUpperConfidentBound ---

    @Test
    void selectChildWithHighestUpperConfidentBound_returnsChildWithHighestScore() {
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

        MCTreeNode best = mc.selectChildWithHighestUpperConfidentBound(parent);
        assertEquals(child2, best);
    }

    // --- backPropagate ---

    @Test
    void backPropagate_incrementsVisitsForAllNodes() {
        MCTreeNode root = new MCTreeNode(board, null, null, Cell.WHITE);
        MCTreeNode child = new MCTreeNode(board, root, null, Cell.BLACK);

        mc.backPropagate(Cell.BLACK, child);

        assertEquals(1, child.getVisits());
        assertEquals(1, root.getVisits());
    }

    @Test
    void backPropagate_onlyIncrementsWinsForMatchingPlayer() {
        // root is WHITE, child is BLACK — BLACK wins
        MCTreeNode root = new MCTreeNode(board, null, null, Cell.WHITE);
        MCTreeNode child = new MCTreeNode(board, root, null, Cell.BLACK);

        mc.backPropagate(Cell.BLACK, child);

        // child (BLACK) won — gets a win
        assertEquals(1, child.getWins());
        assertEquals(0, child.getLosses());

        // root (WHITE) lost — gets a loss
        assertEquals(0, root.getWins());
        assertEquals(1, root.getLosses());
    }

    @Test
    void backPropagate_draw_onlyIncrementsVisits() {
        MCTreeNode root = new MCTreeNode(board, null, null, Cell.WHITE);
        MCTreeNode child = new MCTreeNode(board, root, null, Cell.BLACK);

        mc.backPropagate(Cell.EMPTY, child);

        assertEquals(0, child.getWins());
        assertEquals(0, child.getLosses());
        assertEquals(1, child.getVisits());
    }

    // --- expand player ---

    @Test
    void expand_rootHasNullPlayer_childIsColor() {
        MCTreeNode leaf = new MCTreeNode(board, null, null, null);
        MCTreeNode child = mc.expand(leaf);
        assertEquals(Cell.BLACK, child.getPlayer());
    }

    @Test
    void expand_childPlayerAlternates() {
        MCTreeNode leaf = new MCTreeNode(board, null, null, null);
        MCTreeNode child = mc.expand(leaf);
        MCTreeNode grandchild = mc.expand(child);
        assertNotEquals(child.getPlayer(), grandchild.getPlayer());
    }

    // --- select ---

    @Test
    void select_returnsRoot_whenNoChildren() {
        mc.act(board); // initializes tree
        // root has no children yet — select should return root
        MCTreeNode result = mc.select();
        assertNotNull(result);
    }

    @Test
    void select_walksDeeper_whenRootFullyExpanded() {
        mc.act(board);
        // run enough iterations to build some tree depth
        MonteCarlo mc2 = new MonteCarlo(Cell.BLACK, 200);
        mc2.act(board);
        MCTreeNode result = mc2.select();
        assertNotNull(result);
    }

    // --- simulate ---

    @Test
    void simulate_returnsNonNullWinner() {
        MCTreeNode node = new MCTreeNode(board, null, null, Cell.BLACK);
        Cell winner = mc.simulate(node);
        // winner is BLACK, WHITE, or EMPTY (draw) — never null
        assertTrue(winner == Cell.BLACK || winner == Cell.WHITE || winner == Cell.EMPTY);
    }

    // --- getAction ---

    @Test
    void getAction_returnsMoveFromMostVisitedChild() {
        mc.act(board); // initializes tree with 100ms of simulations
        int[] action = mc.getAction();
        assertNotNull(action);
        assertEquals(2, action.length);
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
