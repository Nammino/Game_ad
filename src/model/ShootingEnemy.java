package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShootingEnemy implements Entity {
    private Vector2D position;
    private int width;
    private int height;
    
    private double speed = 1.0;
    private double visionRange = 300.0;
    
    private int wanderTimer = 0;
    private int currentDirection = 1;
    private Random random = new Random();

    private double initialY;
    private double timeStep = 0.0;

    private int shootCooldown = 0;
    private List<Projectile> activeProjectiles = new ArrayList<>();
    
    private boolean facingRight = true; // Gestione direzione

    public ShootingEnemy(double x, double y) {
        this.width = GameStruct.TILE_SIZE;
        this.height = GameStruct.TILE_SIZE;
        this.position = new Vector2D(x, y);
        this.initialY = y;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle((int) position.getX(), (int) position.getY(), width, height);
    }

    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void update(Player player, ArrayList<String> map) {
        if (player == null) return;

        // Danno da contatto fisico
        if (getBoundingBox().intersects(player.getBoundingBox())) {
            player.takeDamage(1);
        }

        double playerX = player.getPosition().getX();
        double playerY = player.getPosition().getY();
        
        double dx = playerX - position.getX();
        double dy = playerY - position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        timeStep += 0.05;

        // Registriamo la posizione precedente per calcolare la direzione di movimento
        double oldX = position.getX();

        // Se il giocatore è a distanza di tiro
        if (distance <= visionRange) {
            shootCooldown++;
            if (shootCooldown >= 80) { 
                shootCooldown = 0;
                Projectile p = new Projectile(position.getX(), position.getY(), playerX, playerY, 5.5);
                activeProjectiles.add(p);
            }
            
            if (dx > 5) {
                position.setX(position.getX() + 0.5);
            } else if (dx < -5) {
                position.setX(position.getX() - 0.5);
            }
            
        } else {
            wanderTimer++;
            if (wanderTimer > 180) {
                wanderTimer = 0;
                int choice = random.nextInt(3);
                currentDirection = (choice == 0) ? 0 : (choice == 1) ? 1 : -1;
            }
            
            if (currentDirection != 0) {
                position.setX(position.getX() + (speed * currentDirection));
            }
        }

        // Aggiorna lo stato della direzione (facingRight) in base allo spostamento sull'asse X
        double newX = position.getX();
        if (newX > oldX) {
            facingRight = true;
        } else if (newX < oldX) {
            facingRight = false;
        }

        // Movimento verticale fluttuante
        double floatingEffect = Math.sin(timeStep) * 0.5;
        position.setY(initialY + floatingEffect);

        activeProjectiles.removeIf(p -> !p.isActive());
        for (Projectile p : activeProjectiles) {
            p.update(player, map);
        }
    }

    @Override
    public void update(Player player) {
        update(player, null);
    }
}