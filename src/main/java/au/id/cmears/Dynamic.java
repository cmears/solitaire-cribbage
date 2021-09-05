package au.id.cmears;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Dynamic {
    record Entry(int score, ArrayList<Integer> piles) {}
    private HashMap<List<Integer>, Entry> cache;
    final Deal deal;

    Dynamic(Deal deal) {
        this.deal = deal;
        this.cache = new HashMap<List<Integer>, Entry>();
    }

    Entry get(int[] consumed, int depth) {
        String prefix = " ".repeat(2*depth);
        // System.out.println(prefix + "Considering " + Arrays.toString(consumed) + " " + consumed);
        if (Arrays.equals(consumed, new int[]{13,13,13,13})) {
            return new Entry(0, new ArrayList<Integer>());
        }
        List<Integer> key = IntStream.of(consumed).boxed().collect(Collectors.toList());
        if (!cache.containsKey(key)) {
            // System.out.println(prefix + "Not in cache " + Arrays.toString(consumed) + " " + consumed);
            GameState state = new GameState();
            state.consumed = consumed.clone();
            state.deal = deal;
            state.stack = new ArrayList<Card>();
            state.score = 0;
            state.previousMove = null;
            state.previousState = null;

            ArrayList<GameState> moves = Search.stackMoves(state);

            GameState bestMove = moves.get(0);
            int bestScore = bestMove.score + get(bestMove.consumed, depth+1).score();

            for (GameState m: moves) {
                int score = m.score + get(m.consumed, depth+1).score();
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = m;
                }
            }

            Entry e = new Entry(bestScore, bestMove.piles());
            cache.put(key, e);
            // System.out.println("Put " + Arrays.toString(consumed) + " " + consumed + " = " + e);
        }
        return cache.get(key);
    }

    void solve() {
        int[] consumed = {0,0,0,0};
        Entry e = get(consumed, 0);
        System.out.println(e.score);
        while (!Arrays.equals(consumed, new int[]{13,13,13,13})) {
            System.out.println(e.piles());
            for (int p: e.piles()) {
                if (p != -1) {
                    consumed[p]++;
                }
            }
            e = get(consumed, 0);
        }
    }
}
