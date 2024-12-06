package kr.co.example.mobileprogramming.model;

public class Card {
    private int id;
    private CardType type;
    //뒤집힌 상태 여부(false가 뒷면)
    private boolean isFlipped;
    private boolean isMatched;
    //카드에 표시할 이미지 경로 or 리소스 이름
    private String imageResource;

    public Card(int id, CardType type) {
        this.id = id;
        this.type = type;
        this.isFlipped = false;
        this.isMatched = false;
    }

    //카드 뒤집기
    public void flip() {
        isFlipped = !isFlipped;
    }

    // Getter 및 Setter
    public int getId() { return id; }
    public CardType getType() { return type; }
    public boolean isFlipped() { return isFlipped; }

    public void setMatched() {
        this.isMatched = true;
    }
    public String getImageResource() { return imageResource; }
}
