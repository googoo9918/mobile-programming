package kr.co.example.mobileprogramming.model.itemeffects;

import android.os.Handler;
import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;
import kr.co.example.mobileprogramming.model.Card;

public class RevealCardEffect implements ItemEffect {
    public RevealCardEffect() {

    }
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        // 랜덤으로 미리 볼 카드 선택
        int boardSize = gameManager.getBoard().getCards().size();
        int cardPosition = (int) (Math.random() * boardSize);

        Card cardToReveal = gameManager.getBoard().getCardAt(cardPosition);

        if (cardToReveal.isFlipped()) {
            return;
        }

        cardToReveal.flip();
        gameManager.notifyCardFlipped(cardPosition, cardToReveal);

        System.out.println("Revealing card at position " + cardPosition);

        new Handler().postDelayed(() -> {
            cardToReveal.flip();
            gameManager.notifyCardFlipped(cardPosition, cardToReveal);
            System.out.println("Hiding card at position " + cardPosition);
        }, 1000);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.REVEAL_CARD;
    }
}
