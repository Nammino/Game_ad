package model;

import java.awt.Rectangle;

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
        if (!active) return;

        getPosition().setX(getPosition().getX() + speedX);
        getPosition().setY(getPosition().getY() + speedY);
        getBoundingBox().setLocation((int) getPosition().getX(), (int) getPosition().getY());

        if (player != null && getBoundingBox().intersects(player.getBoundingBox())) {
            player.takeDamage(damage);
            active = false; // Il proiettile scompare dopo l'impatto
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}