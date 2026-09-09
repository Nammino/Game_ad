package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Projectile extends Object {
    private double speedX;
    private double speedY;
    private boolean active = true;
    private int damage = 10; 

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
    }

    public void update(Player player, ArrayList<String> map) {
        if (!active) return;

        getPosition().setX(getPosition().getX() + speedX);
        getPosition().setY(getPosition().getY() + speedY);
        getBoundingBox().setLocation((int) getPosition().getX(), (int) getPosition().getY());

        if (player != null && getBoundingBox().intersects(player.getBoundingBox())) {
            player.takeDamage(damage);
            active = false; 
            return;
        }

        if (map != null && !map.isEmpty()) {
            int tileSize = GameStruct.TILE_SIZE;
            
            int tileX = (int) (getPosition().getX() + 6) / tileSize; 
            int tileY = (int) (getPosition().getY() + 6) / tileSize;

            if (tileY >= 0 && tileY < map.size() && tileX >= 0 && tileX < map.get(tileY).length()) {
                char tileChar = map.get(tileY).charAt(tileX);
                if (tileChar == '#') {
                    active = false; 
                }
            } else {
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