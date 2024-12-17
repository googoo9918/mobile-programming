package kr.co.example.mobileprogramming.model;

public class Card {
    private int id;
    private CardType type;
    //뒤집힌 상태 여부(false가 뒷면)
    private boolean flipped;
    private boolean matched;

    public Card() {}

    public Card(int id, CardType type) {
        this.id = id;
        this.type = type;
        this.flipped = false;
        this.matched = false;
    }

    //카드 뒤집기
    public void flip() {
        flipped = !flipped;
    }

    // Getter 및 Setter
    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public boolean isFlipped() { return flipped; }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}
