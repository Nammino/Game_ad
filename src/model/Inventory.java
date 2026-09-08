package model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<ItemInstance> items; 

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(String itemName) {
        this.items.add(new ItemInstance(itemName));
    }

    public boolean hasItem(String itemName) {
        for (ItemInstance item : items) {
            if (item.getType().equals(itemName)) return true;
        }
        return false;
    }

    public void removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType().equals(itemName)) {
                items.remove(i);
                break;
            }
        }
    }

    public List<ItemInstance> getItems() {
        return items;
    }

    // Mantenuto per compatibilità se serve altrove
    public List<String> getCollectedItems() {
        List<String> simpleList = new ArrayList<>();
        for (ItemInstance item : items) {
            simpleList.add(item.getType());
        }
        return simpleList;
    }

    public void clear() {
        items.clear();
    }
}