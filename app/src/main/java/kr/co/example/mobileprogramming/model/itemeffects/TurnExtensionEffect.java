package kr.co.example.mobileprogramming.model.itemeffects;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;

public class TurnExtensionEffect implements ItemEffect {
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        gameManager.setExtendTurnFlag(true);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.TURN_EXTENSION;
    }
}
