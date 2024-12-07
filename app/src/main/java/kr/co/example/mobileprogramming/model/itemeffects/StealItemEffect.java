package kr.co.example.mobileprogramming.model.itemeffects;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;

import java.util.List;
import java.util.Random;

public class StealItemEffect implements  ItemEffect{
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        Player opponent = gameManager.getOpponent(player);

        if (opponent == null || opponent.getItems().isEmpty()) {
            return;
        }

        List<ItemEffect> opponentItems = opponent.getItems();
        int randomIndex = new Random().nextInt(opponentItems.size());
        ItemEffect stolenItem = opponentItems.get(randomIndex);

        opponentItems.remove(stolenItem);
        player.addItemEffect(stolenItem);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.STEAL_ITEM;
    }
}
