package org.dtu.introai.boardgame.agents.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MCTreeNodeTest {

    @Test
    void incrementVisit_increasesVisitCount() {
        MCTreeNode node = new MCTreeNode(null, null);
        node.incrementVisit();
        node.incrementVisit();
        assertEquals(2, node.getVisits());
    }

    @Test
    void incrementWins_increasesWinCount() {
        MCTreeNode node = new MCTreeNode(null, null);
        node.incrementWins();
        assertEquals(1, node.getWins());
    }

    @Test
    void incrementLosses_increasesLossCount() {
        MCTreeNode node = new MCTreeNode(null, null);
        node.incrementLosses();
        assertEquals(1, node.getLosses());
    }

    @Test
    void addChild_addsToChildrenList() {
        MCTreeNode parent = new MCTreeNode(null, null);
        MCTreeNode child = new MCTreeNode(null, parent);
        parent.addChild(child);
        assertEquals(1, parent.getChildren().size());
        assertEquals(child, parent.getChildren().get(0));
    }

    @Test
    void getParent_returnsCorrectParent() {
        MCTreeNode parent = new MCTreeNode(null, null);
        MCTreeNode child = new MCTreeNode(null, parent);
        assertEquals(parent, child.getParent());
    }
}
