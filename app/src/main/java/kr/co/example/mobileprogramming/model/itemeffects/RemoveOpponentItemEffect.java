package kr.co.example.mobileprogramming.model.itemeffects;

import android.util.Log;

import java.util.List;
import java.util.Random;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;

public class RemoveOpponentItemEffect implements ItemEffect{
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        Player opponent = gameManager.getOpponent(player);

        if (opponent == null || opponent.getItems().isEmpty()) {
            Log.d("ITEM_removeOppenentItem", "no opponent or item");
            return;
        }

        List<ItemEffect> opponentItems = opponent.getItems();
        Random random = new Random();
        int indexToRemove = random.nextInt(opponentItems.size());
        ItemEffect removedItem = opponentItems.remove(indexToRemove);


    }
    @Override
    public ItemType getItemType() {
        return ItemType.REMOVE_OPPONENT_ITEM;
    }
}
