package au.id.cmears;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Search {
    GameState bestState;
    Deal deal;
    Random rng;
    Search(Deal deal) {
        bestState = null;
        this.deal = deal;
        rng = new Random();
    }
    GameState initialState() {
        GameState state = new GameState();
        state.consumed = new int[]{0,0,0,0};
        state.score = 0;
        state.deal = deal;
        state.stack = new ArrayList<Card>();
        return state;
    }

    // All the next "stack" moves from here.
    static ArrayList<GameState> stackMoves(GameState state) {
        assert(state.stack.isEmpty());
        ArrayList<GameState> moves = new ArrayList<GameState>();
        stackMoves2(state, moves);
        return moves;
    }
    static void stackMoves2(GameState state, ArrayList<GameState> moves) {
        int value = state.stackValue();
        Boolean validPlay = false;
        for (int pile = 0 ; pile < 4 ; pile++) {
            Optional<Card> top = state.top(pile);
            if (top.isPresent()) {
                Card c = top.get();
                if (value + c.value() <= 31) {
                    validPlay = true;
                    stackMoves2(state.executeMove(new Move(pile)), moves);
                }
            }
        }
        if (!validPlay) {
            moves.add(state.executeMove(new Move(-1)));
        }
    }

    // Play a game to completion and update the best score if it's good enough.
    // The method is:
    //   Look at all the possible "stacks" we can build from the current state.
    //   Choose one at random (biased towards higher scores) and commit to it.
    //   Repeat until finished.
    // The idea is that you call this method repeatedly and hope to strike a good game.
    // Other than the current top-score, there's no memory from one game to the next.
    void probe(GameState state) {
        if (state.finished()) {
            if (bestState == null || state.score > bestState.score) {
                bestState = state;
                System.out.println(bestState.score);
                System.out.println(bestState.pathString());
            }
            return;
        }

        List<GameState> moves = stackMoves(state);
        moves.sort((x,y) -> y.score - x.score);
        // Go through the possible states starting with the highest-scoring one.
        // At each, have a small probability of choosing and committing to it.
        // This means overall we are likely to make "good" choices without
        // doing the same thing every time.
        for (GameState s: moves) {
            if (rng.nextDouble() < 0.001) {
                probe(s);
                break;
            }
        }
        // If we didn't choose anything, just go with the best one.
        probe(moves.get(0));
    }
}
