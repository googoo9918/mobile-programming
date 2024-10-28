package kr.co.example.mobileprogramming.model.itemeffects;

import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;

public interface ItemEffect {
    void applyEffect(GameManager gameManager, Player player);
    ItemType getItemType();
}
