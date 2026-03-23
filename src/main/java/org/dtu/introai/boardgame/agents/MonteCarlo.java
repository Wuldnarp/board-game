package org.dtu.introai.boardgame.agents;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.Othello;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

import javax.swing.*;

public class MonteCarlo implements Agent {

    private MCTreeNode tree;
    private Cell color;
    private long durationMs;

    public MonteCarlo(Cell color, long durationMs){
        this.color = color;
        this.durationMs = durationMs;
    }

    @Override
    public int[] act(Board board) {
        this.tree = new MCTreeNode(board, null);
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < durationMs){
            MCTreeNode selectedLeaf = select();
            MCTreeNode child = expand(selectedLeaf);
            Cell result = simulate(child);
            backPropagate(result,child);
        }
        return getAction();
    }

    // Helper method for selecting the child with the highest UCB1 value
    // From the book page 209.
    double upperConfidentBound(MCTreeNode node, MCTreeNode parent) {
        // always explore unvisited nodes first
        if (node.getVisits() == 0) {
            return Double.POSITIVE_INFINITY; 
        }

        // exploration constant, squareroot (2) from book example
        double C = 1.4; 

        return ((double) node.getWins() / node.getVisits()) + C * Math.sqrt(Math.log(parent.getVisits()) / node.getVisits());
    }

    // selects the child with the highest upper confident bound value
    MCTreeNode selectChildWithHighestUpperConfidentBound(MCTreeNode node) {
        MCTreeNode best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MCTreeNode child : node.getChildren()) {
            double score = upperConfidentBound(child, node);
            // updates best if this child has a higher score
            if (score > bestScore) {
                bestScore = score;
                best = child;
            }
        }
        return best;
    }


    /**
     * @return the leaf node for expansion
     */
    MCTreeNode select(){
        MCTreeNode node = tree;
        while (!node.getChildren().isEmpty() && node.getChildren().size() == node.getBoard().getAllLegalMoves(node.getPlayer() == null ? color : reverse(node.getPlayer())).size()) {
            node = selectChildWithHighestUpperConfidentBound(node);
        }
        return node;
    }

    /**
     * @param leaf - the node that needs expansion
     * @return the new node (leaf) that needs simulation
     */

    MCTreeNode expand(MCTreeNode leaf){
        // determine whose turn it is at this node
        Cell leafPlayer = leaf.getPlayer() == null ? color : reverse(leaf.getPlayer());
        List<int[]> legal = leaf.getBoard().getAllLegalMoves(leafPlayer);
        if (legal.isEmpty()) return leaf; // terminal node, nothing to expand

        // filter out moves already tried
        List<int[]> untried = legal.stream()
            .filter(m -> leaf.getChildren().stream()
                .noneMatch(c -> java.util.Arrays.equals(c.getMove(), m)))
            .collect(java.util.stream.Collectors.toList());
        if (untried.isEmpty()) return leaf;

        int[] action = untried.get(new Random().nextInt(untried.size()));

        // Copies the board so we don't destroy the parent board
        Board copy = new Board(leaf.getBoard());

        // Applies the move on the copy
        Othello othello = new Othello(copy, null);
        othello.setPiece(action[0], action[1], leafPlayer);

        MCTreeNode child = new MCTreeNode(copy, leaf, action, leafPlayer);
        leaf.addChild(child);
        return child;
    }

    /**
     * @param child - the expanded new node to run a simulation on
     * @return the winner of the simulation
     */
    Cell simulate(MCTreeNode child){

        HashMap<Cell, Supplier<int[]>> supplyMap = new HashMap<>();

        // start simulation from the correct player's turn
        Cell nextPlayer = child.getPlayer() == null ? color : reverse(child.getPlayer());
        Othello simulatedGame = new Othello(child.getBoard(), supplyMap, nextPlayer);

        Supplier<int[]> supplyLoopWhite = new Supplier<int[]>() {
            @Override
            public int[] get() {
                List<int[]> moves = simulatedGame.getBoard().getAllLegalMoves(Cell.WHITE);
                return moves.get(new Random().nextInt(moves.size()));
            }
        };

        Supplier<int[]> supplyLoopBlack = new Supplier<int[]>() {
            @Override
            public int[] get() {
                List<int[]> moves = simulatedGame.getBoard().getAllLegalMoves(Cell.BLACK);
                return moves.get(new Random().nextInt(moves.size()));
            }
        };

        Consumer<Object> consumer = new Consumer<Object>() {
            @Override
            public void accept(Object o) {

            }
        };
        supplyMap.put(Cell.WHITE, supplyLoopWhite);
        supplyMap.put(Cell.BLACK, supplyLoopBlack);
        simulatedGame.setSupplyMap(supplyMap);
        simulatedGame.gameLoop(consumer, consumer);
        return simulatedGame.getWinner();
    }

    /**
     * @param result - the winner of the simulation
     * @param child - the given node the simulation was run on
     */
    void backPropagate(Cell result, MCTreeNode child){
        MCTreeNode curNode = child;

        while(curNode != null){
            curNode.incrementVisit();

            // only reward nodes belonging to the player who won
            if(result == curNode.getPlayer()){
                curNode.incrementWins();
            } else if (result != null && result != Cell.EMPTY && result != curNode.getPlayer()) {
                curNode.incrementLosses();
            }

            curNode = curNode.getParent();
        }
    }

    private Cell reverse(Cell cell){
        return cell.equals(Cell.WHITE) ? Cell.BLACK : Cell.WHITE;
    }

    /**
     * pick the action with the highest number of playouts
     * @return the chosen action
     */
    int[] getAction(){
        return tree.getChildren().stream()
            .max(Comparator.comparingInt(MCTreeNode::getVisits))
            .orElseThrow()
            .getMove();
    }
}