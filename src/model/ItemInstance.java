package model;

public class ItemInstance {
    private String type; // "POTION", "SWORD", "GUN", "COIN"
    private int usesLeft; // Numero di utilizzi rimasti

    public ItemInstance(String type) {
        this.type = type;
        if (type.equals("SWORD")) {
            this.usesLeft = 15; // Ad esempio, la spada dura 15 utilizzi
        } else if (type.equals("GUN")) {
            this.usesLeft = 10; // La pistola dura 10 utilizzi
        } else {
            this.usesLeft = 1;  // Pozioni o altri oggetti a singolo utilizzo
        }
    }

    public String getType() {
        return type;
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    public void use() {
        if (usesLeft > 0) {
            usesLeft--;
        }
    }

    public boolean isBroken() {
        return usesLeft <= 0;
    }
}