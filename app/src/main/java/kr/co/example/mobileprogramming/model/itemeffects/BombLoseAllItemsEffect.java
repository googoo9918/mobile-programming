package kr.co.example.mobileprogramming.model.itemeffects;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;

public class BombLoseAllItemsEffect implements ItemEffect {
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        player.clearItems();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.BOMB_LOSE_ALL_ITEMS;
    }
}
