package kr.co.example.mobileprogramming.model;

import java.util.ArrayList;
import java.util.List;

import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;

public class Player {
    private String name;
    private int score;
    //보유한 아이템 효과
    private List<ItemEffect> items;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.items = new ArrayList<>();
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void doubleScore() {
        this.score *= 2;
    }

    public void addItemEffect(ItemEffect itemEffect) {
        items.add(itemEffect);
    }


    public boolean useItem(ItemType itemType, GameManager gameManager) {
        for (ItemEffect item : items) {
            if (item.getItemType() == itemType) {
                item.applyEffect(gameManager, this);
                items.remove(item);
                // TODO: item logic
                return true;
            }
        }
        return false;
    }

    public void clearItems() {
        items.clear(); // 모든 아이템 제거
    }

    // Getter&Setter
    public String getName() { return name; }
    public int getScore() { return score; }
    public List<ItemEffect> getItems() { return items; }
}

