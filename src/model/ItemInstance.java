package model;

public class ItemInstance {
    private String type; 
    private int usesLeft; 

    public ItemInstance(String type) {
        this.type = type;
        if (type.equals("SWORD")) {
            this.usesLeft = 15; 
        } else if (type.equals("GUN")) {
            this.usesLeft = 10; 
        } else {
            this.usesLeft = 1;  
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