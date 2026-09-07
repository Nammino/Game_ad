package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Player extends Character {

    private final double GRAVITY = 0.8;
    private final double JUMP_STRENGTH = -13.9;
    private final double MOVE_SPEED = 4.0;

    private final int maxHealth = 100;
    private int health = 100;

    private boolean left = false;
    private boolean right = false;
    private boolean jumpRequested = false;
    private Inventory inventory = new Inventory();

    public Player() {
        super();
        getPosition().setX(50);
        getPosition().setY(100);
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    
    public void setHealth(int health) { 
        this.health = Math.max(0, Math.min(health, maxHealth)); 
    }
    
    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public void resetHealth() {
        this.health = this.maxHealth;
    }

    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }
    public void setJumpRequested(boolean jumpRequested) { this.jumpRequested = jumpRequested; }

    public void update(ArrayList<String> map) {
        if (left && !right) {
            getVelocity().setX(-MOVE_SPEED);
        } else if (right && !left) {
            getVelocity().setX(MOVE_SPEED);
        } else {
            getVelocity().setX(0);
        }

        if (jumpRequested && isGrounded()) {
            getVelocity().setY(JUMP_STRENGTH);
            setGrounded(false);
            jumpRequested = false;
        }

        getVelocity().setY(getVelocity().getY() + GRAVITY);

        int tileSize = GameStruct.TILE_SIZE;

        getPosition().setX(getPosition().getX() + getVelocity().getX());

        if (getPosition().getX() < 0) {
            getPosition().setX(0);
        } 
        
        if (map != null && !map.isEmpty() && map.get(0) != null) {
            int mapWidthPixels = map.get(0).length() * tileSize;
            if (getPosition().getX() > mapWidthPixels - tileSize) {
                getPosition().setX(mapWidthPixels - tileSize);
            }
        }

        getBoundingBox().setBounds((int) Math.round(getPosition().getX()), (int) Math.round(getPosition().getY()), tileSize, tileSize);

        for (Rectangle tile : getSolidTiles(map, tileSize)) {
            if (getBoundingBox().intersects(tile)) {
                if (getVelocity().getX() > 0) {
                    getPosition().setX(tile.x - tileSize);
                } else if (getVelocity().getX() < 0) {
                    getPosition().setX(tile.x + tile.width);
                }
                getBoundingBox().setBounds((int) Math.round(getPosition().getX()), (int) Math.round(getPosition().getY()), tileSize, tileSize);
            }
        }

        getPosition().setY(getPosition().getY() + getVelocity().getY());
        getBoundingBox().setBounds((int) Math.round(getPosition().getX()), (int) Math.round(getPosition().getY()), tileSize, tileSize);

        setGrounded(false);
        for (Rectangle tile : getSolidTiles(map, tileSize)) {
            if (getBoundingBox().intersects(tile)) {
                if (getVelocity().getY() > 0) { // Atterraggio
                    getPosition().setY(tile.y - tileSize);
                    getVelocity().setY(0);
                    setGrounded(true);
                } else if (getVelocity().getY() < 0) { // Soffitto
                    getPosition().setY(tile.y + tile.height);
                    getVelocity().setY(0);
                }
                getBoundingBox().setBounds((int) Math.round(getPosition().getX()), (int) Math.round(getPosition().getY()), tileSize, tileSize);
            }
        }

        if (map != null && !map.isEmpty()) {
            int mapHeightPixels = map.size() * tileSize;
            if (getPosition().getY() > mapHeightPixels + 100) {
                takeDamage(maxHealth); 
            }
        }
    }

    @Override
    public void update(Player player) {
        // Il player aggiorna se stesso usando la mappa del livello corrente
        // (Puoi mantenere qui la logica di update esistente basata sulla mappa, 
        // oppure puoi recuperare la mappa se ti serve, ma la tua struttura attuale la passa dal GamePanel)
    }
    
    private ArrayList<Rectangle> getSolidTiles(ArrayList<String> map, int tileSize) {
        ArrayList<Rectangle> solidTiles = new ArrayList<>();
        if (map == null || map.isEmpty()) return solidTiles;

        int startCol = Math.max(0, (int) getPosition().getX() / tileSize - 1);
        int endCol = Math.min(map.get(0).length() - 1, ((int) getPosition().getX() + tileSize) / tileSize + 1);
        int startRow = Math.max(0, (int) getPosition().getY() / tileSize - 1);
        int endRow = Math.min(map.size() - 1, ((int) getPosition().getY() + tileSize) / tileSize + 1);

        for (int row = startRow; row <= endRow; row++) {
            String line = map.get(row);
            for (int col = startCol; col <= endCol; col++) {
                char c = line.charAt(col);
                if (c == '#' || c == '?') {
                    solidTiles.add(new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize));
                }
            }
        }
        return solidTiles;
    }
    
    public Inventory getInventory() {
        return inventory;
    }
}