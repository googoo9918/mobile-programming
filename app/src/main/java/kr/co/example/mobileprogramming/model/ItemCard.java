package kr.co.example.mobileprogramming.model;

//Card 클래스 상속
public class ItemCard extends Card {
    private ItemType itemType;

    public ItemCard(int id, String imageResource, ItemType itemType) {
        super(id, CardType.ITEM, imageResource);
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

}

