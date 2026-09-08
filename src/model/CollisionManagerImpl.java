package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class CollisionManagerImpl implements CollisionManager {

    @Override
    public boolean checkTileCollisions(Player player, ArrayList<String> map) {
        if (map == null || map.isEmpty()) return false;

        int tileSize = GameStruct.TILE_SIZE;

        int startCol = (int) (player.getPosition().getX() / tileSize) - 1;
        int endCol = (int) ((player.getPosition().getX() + tileSize) / tileSize) + 1;
        int startRow = (int) (player.getPosition().getY() / tileSize) - 1;
        int endRow = (int) ((player.getPosition().getY() + tileSize) / tileSize) + 1;

        startCol = Math.max(0, startCol);
        endCol = Math.min(map.get(0).length() - 1, endCol);
        startRow = Math.max(0, startRow);
        endRow = Math.min(map.size() - 1, endRow);

        boolean levelCompleted = false;

        // --- GESTIONE ASSE Y (Gravità e Salto) ---
        // Se è a terra e non sta saltando, blocchiamo la velocità verticale a 0 ed evito micro-movimenti
        if (player.isGrounded() && player.getVelocity().getY() >= 0) {
            player.getVelocity().setY(0);
        } else {
            // Applica la velocità verticale
            player.getPosition().setY(player.getPosition().getY() + player.getVelocity().getY());
        }

        Rectangle playerBoundsY = new Rectangle(
            (int) Math.round(player.getPosition().getX()), 
            (int) Math.round(player.getPosition().getY()), 
            tileSize, tileSize
        );
        
        player.setGrounded(false);

        // Controllo collisioni asse Y con i tile
        for (int row = startRow; row <= endRow; row++) {
            String line = map.get(row);
            for (int col = startCol; col <= endCol; col++) {
                char tile = line.charAt(col);
                
                if (tile == '=') {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                    if (playerBoundsY.intersects(tileBounds)) {
                        levelCompleted = true;
                    }
                }

                if (isSolid(tile)) {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);

                    if (playerBoundsY.intersects(tileBounds)) {
                        if (player.getVelocity().getY() > 0) {  
                            // Atterraggio: posizionamento pulito ed esatto sul blocco
                            player.getPosition().setY(tileBounds.y - tileSize);
                            player.getVelocity().setY(0);
                            player.setGrounded(true);
                        } else if (player.getVelocity().getY() < 0) {  
                            // Urto contro un blocco dal basso (durante il salto)
                            player.getPosition().setY(tileBounds.y + tileSize);
                            player.getVelocity().setY(0);
                        }
                    }
                }
            }
        }

        // --- GESTIONE ASSE X ---
        player.getPosition().setX(player.getPosition().getX() + player.getVelocity().getX());

        int mapWidthPixels = map.get(0).length() * tileSize;
        if (player.getPosition().getX() < 0) {
            player.getPosition().setX(0);
        }
        if (player.getPosition().getX() + tileSize > mapWidthPixels) {
            player.getPosition().setX(mapWidthPixels - tileSize);
        }

        Rectangle playerBoundsX = new Rectangle(
            (int) Math.round(player.getPosition().getX()), 
            (int) Math.round(player.getPosition().getY()), 
            tileSize, tileSize
        );

        for (int row = startRow; row <= endRow; row++) {
            String line = map.get(row);
            for (int col = startCol; col <= endCol; col++) {
                char tile = line.charAt(col);
                
                if (tile == '=') {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                    if (playerBoundsX.intersects(tileBounds)) {
                        levelCompleted = true;
                    }
                }

                if (isSolid(tile)) {
                    Rectangle tileBounds = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);

                    if (playerBoundsX.intersects(tileBounds)) {
                        if (player.getVelocity().getX() > 0) {  
                            player.getPosition().setX(tileBounds.x - tileSize);
                        } else if (player.getVelocity().getX() < 0) {  
                            player.getPosition().setX(tileBounds.x + tileSize);
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