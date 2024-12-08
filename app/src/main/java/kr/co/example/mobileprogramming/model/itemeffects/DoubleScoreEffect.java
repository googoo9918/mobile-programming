package kr.co.example.mobileprogramming.model.itemeffects;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;

public class DoubleScoreEffect implements ItemEffect{
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        player.doubleScore();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DOUBLE_SCORE;
    }
}
