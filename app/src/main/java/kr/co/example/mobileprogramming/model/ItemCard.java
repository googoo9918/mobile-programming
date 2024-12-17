package kr.co.example.mobileprogramming.model;

import kr.co.example.mobileprogramming.model.itemeffects.DoubleScoreEffect;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;
import kr.co.example.mobileprogramming.model.itemeffects.RemoveOpponentItemEffect;
import kr.co.example.mobileprogramming.model.itemeffects.RevealCardEffect;
import kr.co.example.mobileprogramming.model.itemeffects.StealItemEffect;
import kr.co.example.mobileprogramming.model.itemeffects.TurnExtensionEffect;

//Card 클래스 상속
public class ItemCard extends Card {
    private ItemType itemType;

    public ItemCard() {
        super();
    }

    public ItemCard(int id, ItemType itemType) {
        super(id, CardType.ITEM);
        this.itemType = itemType;
    }

    public ItemEffect createItemEffect() {
        // Return the corresponding effect instance based on the ItemType
        switch (itemType) {
            case TURN_EXTENSION:
                return new TurnExtensionEffect();
            case DOUBLE_SCORE:
                return new DoubleScoreEffect();
            case REMOVE_OPPONENT_ITEM:
                return new RemoveOpponentItemEffect();
            case REVEAL_CARD:
                return new RevealCardEffect();
            case STEAL_ITEM:
                return new StealItemEffect();
            default:
                throw new IllegalArgumentException("Unknown item type: " + itemType);
        }
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType type) {
        this.itemType = type;
    }

}

