package au.id.cmears;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Dynamic {
    // The cache maps a "position" in the game to a pair of (score, move).
    // If we have
    //   cache[position] = (score, move)
    // it means "if you are looking at *position*, the best total score you can
    // possibly get from here is *score*, and you can get it by playing *move*.
    record Entry(int score, ArrayList<Integer> piles) {}
    private HashMap<List<Integer>, Entry> cache;
    final Deal deal;

    Dynamic(Deal deal) {
        this.deal = deal;
        this.cache = new HashMap<List<Integer>, Entry>();
    }

    // Get the Entry for a given position. If it's already in the cache, it's
    // simply retrieved. If not, it will be computed, stored in the cache and
    // then returned.
    //
    // *consumed* is a length-4 array saying how many cards have been already
    // used up from each pile.  (This is the key for the cache.)
    Entry get(int[] consumed) {
        // The base case is that we've played all the cards and the game is
        // over.
        if (Arrays.equals(consumed, new int[]{13,13,13,13})) {
            return new Entry(0, new ArrayList<Integer>());
        }
        // We can't use the array directly as the cache key so we use a List
        // version of it.
        List<Integer> key = IntStream.of(consumed).boxed().collect(Collectors.toList());
        if (!cache.containsKey(key)) {
            // Construct a GameState from the position described by *consumed*.
            GameState state = new GameState();
            state.consumed = consumed.clone();
            state.deal = deal;
            state.stack = new ArrayList<Card>();
            state.score = 0;
            state.previousMove = null;
            state.previousState = null;

            // For each possible move (a whole stack's worth of cards),
            // we compute
            //   (score of move) + (best score from resulting position)
            // Then the best move is the move that maximises this value.

            // Get all the possible moves.
            ArrayList<GameState> moves = Search.stackMoves(state);

            // Iterate over all moves to find the best.
            GameState bestMove = moves.get(0);
            int bestScore = bestMove.score + get(bestMove.consumed).score();

            for (GameState m: moves) {
                // The best move is the one that maximises this value.
                int score = m.score + get(m.consumed).score();
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = m;
                }
            }

            // Put it in the cache.
            Entry e = new Entry(bestScore, bestMove.piles());
            cache.put(key, e);
        }
        return cache.get(key);
    }

    void solve() {
        // We ask "what's the best move from the starting position?"
        int[] consumed = {0,0,0,0};
        Entry e = get(consumed);

        // Then we follow the moves from there, each time consulting the cache
        // to get the next move.
        System.out.println(e.score);
        while (!Arrays.equals(consumed, new int[]{13,13,13,13})) {
            System.out.println(e.piles());
            for (int p: e.piles()) {
                if (p != -1) {
                    consumed[p]++;
                }
            }
            e = get(consumed);
        }
    }
}
