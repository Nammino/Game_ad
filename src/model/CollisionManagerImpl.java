package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class CollisionManagerImpl implements CollisionManager {

    @Override
    public boolean checkTileCollisions(Entity entity, ArrayList<String> map) {
        if (map == null || map.isEmpty()) return false;

        int tileSize = GameStruct.TILE_SIZE;

        int startCol = (int) (entity.getPosition().getX() / tileSize) - 1;
        int endCol = (int) ((entity.getPosition().getX() + tileSize) / tileSize) + 1;
        int startRow = (int) (entity.getPosition().getY() / tileSize) - 1;
        int endRow = (int) ((entity.getPosition().getY() + tileSize) / tileSize) + 1;

        startCol = Math.max(0, startCol);
        endCol = Math.min(map.get(0).length() - 1, endCol);
        startRow = Math.max(0, startRow);
        endRow = Math.min(map.size() - 1, endRow);

        boolean levelCompleted = false;

        // --- GESTIONE ASSE Y ---
        if (entity.isGrounded() && entity.getVelocity().getY() >= 0) {
            entity.getVelocity().setY(0);
        } else {
            entity.getPosition().setY(entity.getPosition().getY() + entity.getVelocity().getY());
        }

        Rectangle entityBoundsY = new Rectangle(
            (int) Math.round(entity.getPosition().getX()), 
            (int) Math.round(entity.getPosition().getY()), 
            tileSize, tileSize
        );
        
        entity.setGrounded(false);

        for (int row = startRow; row <= endRow; row++) {
            String line = map.get(row);
            for (int col = startCol; col <= endCol; col++) {
                char tile = line.charAt(col);
                
                // Se l'entità è il Player, controlliamo anche il completamento del livello
                if (tile == '=' && entity instanceof Player) {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                    if (entityBoundsY.intersects(tileBounds)) {
                        levelCompleted = true;
                    }
                }

                if (isSolid(tile)) {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);

                    if (entityBoundsY.intersects(tileBounds)) {
                        if (entity.getVelocity().getY() > 0) {  
                            entity.getPosition().setY(tileBounds.y - tileSize);
                            entity.getVelocity().setY(0);
                            entity.setGrounded(true);
                        } else if (entity.getVelocity().getY() < 0) {  
                            entity.getPosition().setY(tileBounds.y + tileSize);
                            entity.getVelocity().setY(0);
                        }
                    }
                }
            }
        }

        // --- GESTIONE ASSE X ---
        entity.getPosition().setX(entity.getPosition().getX() + entity.getVelocity().getX());

        int mapWidthPixels = map.get(0).length() * tileSize;
        if (entity.getPosition().getX() < 0) {
            entity.getPosition().setX(0);
        }
        if (entity.getPosition().getX() + tileSize > mapWidthPixels) {
            entity.getPosition().setX(mapWidthPixels - tileSize);
        }

        Rectangle entityBoundsX = new Rectangle(
            (int) Math.round(entity.getPosition().getX()), 
            (int) Math.round(entity.getPosition().getY()), 
            tileSize, tileSize
        );

        for (int row = startRow; row <= endRow; row++) {
            String line = map.get(row);
            for (int col = startCol; col <= endCol; col++) {
                char tile = line.charAt(col);
                
                if (tile == '=' && entity instanceof Player) {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                    if (entityBoundsX.intersects(tileBounds)) {
                        levelCompleted = true;
                    }
                }

                if (isSolid(tile)) {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);

                    if (entityBoundsX.intersects(tileBounds)) {
                        if (entity.getVelocity().getX() > 0) {  
                            entity.getPosition().setX(tileBounds.x - tileSize);
                        } else if (entity.getVelocity().getX() < 0) {  
                            entity.getPosition().setX(tileBounds.x + tileSize);
                        }
                    }
                }
            }
        }

        return levelCompleted;
    }

    private boolean isSolid(char tile) {
        return tile == '#' || tile == '?';
    }
}