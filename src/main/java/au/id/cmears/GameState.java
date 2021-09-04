package au.id.cmears;

import java.util.ArrayList;
import java.util.Optional;

record Card(int face) {
    // 1 = Ace, 11 = Jack, 12 = Queen, 13 = King
    // "Face" is what's written on the card,
    // "value" is what it contributes to the stack total.
    int value() {
        if (face >= 10) {
            return 10;
        } else {
            return face;
        }
    }
}

// A "deal" is the arrangement of cards at the start of the game.
// In each pile, the cards in order of play.
// (That is, the cards[0][0] is the card at the bottom-left of screen.)
class Deal {
    final Card[][] cards;
    Deal(int[] faces) {
        cards = new Card[4][13];
        int i = 0;
        for (int j = 0 ; j < 4 ; j++) {
            for (int k = 0 ; k < 13 ; k++) {
                cards[j][k] = new Card(faces[i]);
                i++;
            }
        }
    }
}

// Piles are numbered 0,1,2,3.
// -1 means "new stack".
record Move(int pile) { }

public class GameState {
    // Every GameState can share the same Deal object.
    Deal deal;

    // How many cards have been consumed from each pile.
    int[] consumed;

    // The current stack, with the first card played at the front.
    ArrayList<Card> stack;

    // The total score (not for this stack, but the whole game).
    int score;

    // The move taken to get here (null for the initial state).
    Move previousMove;

    // The previous GameState (null for the initial state).
    GameState previousState;

    // Have we played all the cards?
    Boolean finished() {
        return consumed[0] == 13 && consumed[1] == 13 && consumed[2] == 13 && consumed[3] == 13;
    }

    // The value of the stack so far.
    int stackValue() {
        int value = stack.stream().mapToInt((c) -> c.value()).sum();
        assert(0 <= value && value <= 31);
        return value;
    }

    // Do the most recent "n" cards on the stack form a run of "n" consecutive cards, in some order?
    // Note that aces are only low (so Q-K-A is *not* a run of three).
    Boolean isRunOf(int n) {
        if (stack.size() < n) return false;

        ArrayList<Card> sublist = new ArrayList<Card>(stack.subList(stack.size() - n, stack.size()));
        sublist.sort((c1,c2) -> c2.face() - c1.face());
        for (int i = 1 ; i < sublist.size() ; i++) {
            if (sublist.get(i).face() != sublist.get(i-1).face() + 1) {
                return false;
            }
        }
        return true;
    }

    // Add whatever points are due to the most recently played card on the stack.
    void updateScore() {
        assert(!stack.isEmpty());
        int value = stackValue();

        // Special scores.
        if (value == 15 || value == 31) score += 2;

        // Initial jack.
        if (stack.size() == 1 && stack.get(0).face() == 11) score += 2;

        // 2/3/4 of a kind.
        int multiple = 1;
        int lastFace = stack.get(stack.size()-1).face();
        for (int i = stack.size()-2 ; i >= 0 ; i--) {
            if (stack.get(i).face() != lastFace) break;
            multiple++;
        }
        switch (multiple) {
            case 2: score += 2; break;
            case 3: score += 6; break;
            case 4: score += 12; break;
        }

        // Runs of 3-7 consecutive cards.
        if (isRunOf(7)) score += 7;
        else if (isRunOf(6)) score += 6;
        else if (isRunOf(5)) score += 5;
        else if (isRunOf(4)) score += 4;
        else if (isRunOf(3)) score += 3;
    }

    // What card (if any) is playable in the given pile?
    Optional<Card> top(int pile) {
        if (consumed[pile] == 13) return Optional.empty();
        else return Optional.of(deal.cards[pile][consumed[pile]]);
    }

    // Execute a move, yielding a new state.
    GameState executeMove(Move move) {
        GameState state = new GameState();
        state.deal = deal;
        state.consumed = consumed.clone();
        state.score = score;
        state.previousMove = move;
        state.previousState = this;
        if (move.pile() == -1) {
            state.stack = new ArrayList<Card>();
        } else {
            Card card = top(move.pile()).get();
            state.consumed[move.pile()]++;
            state.stack = new ArrayList<Card>(stack);
            state.stack.add(card);
            state.updateScore();
        }
        return state;
    }

    // Compute the path to this state, by following all the "previous" values as a linked list.
    // Return a string suitable for printing out.
    String pathString() {
        String output = "";
        ArrayList<GameState> history = new ArrayList<GameState>();
        for (GameState s = this ; s != null ; s = s.previousState) {
            history.add(s);
        }
        Boolean first = true;
        while (!history.isEmpty()) {
            GameState s = history.remove(history.size()-1);
            if (s.previousMove != null) {
                if (!first) {
                    output += " ";
                }
                first = false;
                output += s.previousMove.pile();
            }
        }
        return output;
    }
}
