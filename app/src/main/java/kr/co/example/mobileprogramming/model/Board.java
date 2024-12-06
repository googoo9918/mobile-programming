package kr.co.example.mobileprogramming.model;

import android.util.Log;

import java.util.List;

public class Board {
    private List<Card> cards;
    private int rows;
    private int columns;

    public Board(int rows, int columns, List<Card> cards) {
        this.rows = this.rows;
        this.columns = this.columns;
        this.cards = cards;
    }

    public boolean flipCard(int position) {
        if (position < 0 || position >= cards.size()) {
            Log.d("Board", "flipcard index error");
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

    // 기타 필요한 메서드
}

