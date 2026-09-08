package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Projectile extends Object {
    private double speedX;
    private double speedY;
    private boolean active = true;
    private int damage = 10; // Danno inflitto al player se colpito

    public Projectile(double startX, double startY, double targetX, double targetY, double speed) {
        super();
        getPosition().setX(startX);
        getPosition().setY(startY);
        getBoundingBox().setBounds((int) startX, (int) startY, 12, 12);

        double dx = targetX - startX;
        double dy = targetY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            this.speedX = (dx / distance) * speed;
            this.speedY = (dy / distance) * speed;
        } else {
            this.speedX = speed;
            this.speedY = 0;
        }
    }

    @Override
    public void update(Player player) {
        // Manteniamo questo per compatibilità se richiamato altrove, 
        // ma useremo la versione con la mappa.
    }

    public void update(Player player, ArrayList<String> map) {
        if (!active) return;

        // Muovi il proiettile
        getPosition().setX(getPosition().getX() + speedX);
        getPosition().setY(getPosition().getY() + speedY);
        getBoundingBox().setLocation((int) getPosition().getX(), (int) getPosition().getY());

        // 1. Controllo collisione con il Giocatore
        if (player != null && getBoundingBox().intersects(player.getBoundingBox())) {
            player.takeDamage(damage);
            active = false; // Scompare dopo l'impatto col player
            return;
        }

        // 2. Controllo collisione con i muri della mappa ('#')
        if (map != null && !map.isEmpty()) {
            int tileSize = GameStruct.TILE_SIZE;
            
            // Calcoliamo la riga e la colonna in base al centro o ai bordi del proiettile
            int tileX = (int) (getPosition().getX() + 6) / tileSize; // 6 è metà della dimensione del proiettile (12x12)
            int tileY = (int) (getPosition().getY() + 6) / tileSize;

            // Verifichiamo che i limiti siano dentro la mappa
            if (tileY >= 0 && tileY < map.size() && tileX >= 0 && tileX < map.get(tileY).length()) {
                char tileChar = map.get(tileY).charAt(tileX);
                if (tileChar == '#') {
                    active = false; // Scompare se colpisce un muro
                }
            } else {
                // Se esce fuori dai confini della mappa, disattivalo
                active = false;
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public double getSpeedX() {
        return speedX;
    }
}