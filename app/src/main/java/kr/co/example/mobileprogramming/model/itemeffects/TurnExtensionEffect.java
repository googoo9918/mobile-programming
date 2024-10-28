package kr.co.example.mobileprogramming.model.itemeffects;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;

public class TurnExtensionEffect implements ItemEffect {
    @Override
    public void applyEffect(GameManager gameManager, Player player) {
        // Todo: 턴 연장 로직 구현
    }

    @Override
    public ItemType getItemType() {
        return ItemType.TURN_EXTENSION;
    }
}
