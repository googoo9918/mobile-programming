package kr.co.example.mobileprogramming.events;

import kr.co.example.mobileprogramming.model.Card;
import kr.co.example.mobileprogramming.model.ItemCard;
import kr.co.example.mobileprogramming.model.Player;

public interface GameEventListener {
    void onGameStarted();
    void onCardFlipped(int position, Card card);
    void onMatchFound(int position1, int position2);
    void onItemAcquired(ItemCard itemCard);
    void onTurnChanged(Player currentPlayer);
    void onGameOver();
    void onGameStateUpdated(); // 추가된 메서드
}
