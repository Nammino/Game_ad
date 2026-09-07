package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Player extends Character {

    private final double GRAVITY = 0.5;
    private final double JUMP_STRENGTH = -11.0;
    private final double MOVE_SPEED = 4.0;

    public Player() {
        super();
        getPosition().setX(50);
        getPosition().setY(100);
    }

    public void moveLeft() {
        getVelocity().setX(-MOVE_SPEED);
    }

    public void moveRight() {
        getVelocity().setX(MOVE_SPEED);
    }

    public void stop() {
        getVelocity().setX(0);
    }

    public void jump() {
        if (isGrounded()) {
            getVelocity().setY(JUMP_STRENGTH);
            setGrounded(false);
        }
    }

    public void update(ArrayList<String> map) {
        // Applica gravità
        getVelocity().setY(getVelocity().getY() + GRAVITY);

        int tileSize = GameStruct.TILE_SIZE;

        // --- MOVIMENTO ORIZZONTALE (X) ---
        getPosition().setX(getPosition().getX() + getVelocity().getX());
        getBoundingBox().setBounds((int) getPosition().getX(), (int) getPosition().getY(), tileSize, tileSize);

        for (Rectangle tile : getSolidTiles(map, tileSize)) {
            if (getBoundingBox().intersects(tile)) {
                if (getVelocity().getX() > 0) { // Spostamento verso destra
                    getPosition().setX(tile.x - tileSize);
                } else if (getVelocity().getX() < 0) { // Spostamento verso sinistra
                    getPosition().setX(tile.x + tile.width);
                }
                getBoundingBox().setBounds((int) getPosition().getX(), (int) getPosition().getY(), tileSize, tileSize);
            }
        }

        // --- MOVIMENTO VERTICALE (Y) ---
        getPosition().setY(getPosition().getY() + getVelocity().getY());
        getBoundingBox().setBounds((int) getPosition().getX(), (int) getPosition().getY(), tileSize, tileSize);

        setGrounded(false);
        for (Rectangle tile : getSolidTiles(map, tileSize)) {
            if (getBoundingBox().intersects(tile)) {
                if (getVelocity().getY() > 0) { // Caduata verso il basso (Aterragio)
                    getPosition().setY(tile.y - tileSize);
                    getVelocity().setY(0);
                    setGrounded(true);
                } else if (getVelocity().getY() < 0) { // Salto verso l'alto (Impatto con blocco sopra)
                    getPosition().setY(tile.y + tile.height);
                    getVelocity().setY(0);
                }
                getBoundingBox().setBounds((int) getPosition().getX(), (int) getPosition().getY(), tileSize, tileSize);
            }
        }
    }

    @Override
    public void update() {
        update(null);
    }

    // Calcola i blocchi solidi attorno al giocatore per ottimizzare le collisioni
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
}