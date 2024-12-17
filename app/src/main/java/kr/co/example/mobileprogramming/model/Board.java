package kr.co.example.mobileprogramming.model;

import android.util.Log;

import java.util.List;

public class Board {
    private List<Card> cards;
    private int rows = 6;
    private int columns = 6;

    public Board() {}

    public Board(List<Card> cards) {
        this.cards = cards;
    }

    public boolean flipCard(int position) {
        if (position < 0 || position >= cards.size()) {
            Log.e("Board", "flipcard index error");
            return false;
        }
        Card card = cards.get(position);
        if (card.isFlipped()) return false;
        card.flip();
        return true;
    }

    public Card getCardAt(int position) {
        if (position < 0 || position >= cards.size()) return null;
        return cards.get(position);
    }

    public List<Card> getCards() {
        return cards;
    }
}

