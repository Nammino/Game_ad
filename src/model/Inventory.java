package model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<String> collectedItems;

    public Inventory() {
        this.collectedItems = new ArrayList<>();
    }

    public void addItem(String itemName) {
        this.collectedItems.add(itemName);
    }

    public List<String> getCollectedItems() {
        return collectedItems;
    }

    public void clear() {
        collectedItems.clear();
    }
}