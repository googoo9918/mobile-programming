package kr.co.example.mobileprogramming.model;

//Card 클래스 상속
public class ItemCard extends Card {
    private ItemType itemType;

    public ItemCard(int id, ItemType itemType) {
        super(id, CardType.ITEM);
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

}

