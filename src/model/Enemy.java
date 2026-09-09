package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class Enemy implements Entity {
    private Vector2D position;
    private Vector2D velocity;
    private boolean grounded = false;
    private int width;
    private int height;
    private BufferedImage sprite;
    
    private double speed = 1.0;          
    private double visionRange = 220.0;  
    
    private int wanderTimer = 0;
    private int currentDirection = 1;      
    private Random random = new Random();

    private int damageCooldown = 0; // Timer di cooldown

    public Enemy(double x, double y) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/sprite/greenslime_down_1.png"));
            this.width = GameStruct.TILE_SIZE;
            this.height = GameStruct.TILE_SIZE;
        } catch (IOException | IllegalArgumentException e) {
            this.width = GameStruct.TILE_SIZE;
            this.height = GameStruct.TILE_SIZE;
        }
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public boolean isGrounded() {
        return grounded;
    }

    @Override
    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    @Override
    public Rectangle getBoundingBox() {
        int shrinkX = 10;
        return newRectangleWithShrink(shrinkX);
    }

    private Rectangle newRectangleWithShrink(int shrinkX) {
        return new Rectangle(
            (int) position.getX() + (shrinkX / 2), 
            (int) position.getY(), 
            width - shrinkX, 
            height
        );
    }

    @Override
    public void update(Player player) {
        if (player == null) return;

        // --- GESTIONE TIMER COOLDOWN DANNO ---
        if (damageCooldown > 0) {
            damageCooldown--;
        }

        // Controllo collisione e applicazione danno con cooldown protetto
        if (getBoundingBox().intersects(player.getBoundingBox())) {
            if (damageCooldown == 0) {
                player.takeDamage(5); // Toglie 5 HP
                damageCooldown = 180; // 180 frame = circa 3 secondi di pausa prima del prossimo danno
            }
        }

        // Gestione gravità sull'asse Y
        if (!grounded) {
            velocity.setY(velocity.getY() + 0.5);
        }

        double playerX = player.getPosition().getX();
        double playerY = player.getPosition().getY();
        
        double dx = playerX - position.getX();
        double dy = playerY - position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= visionRange) {
            if (dx > 0) {
                velocity.setX(speed * 1.3);
            } else if (dx < 0) {
                velocity.setX(-speed * 1.3);
            }
        } else {
            wanderTimer++;
            if (wanderTimer > 120) {
                wanderTimer = 0;
                int choice = random.nextInt(3);
                if (choice == 0) {
                    currentDirection = 0;
                } else if (choice == 1) {
                    currentDirection = 1;
                } else {
                    currentDirection = -1;
                }
            }

            if (currentDirection != 0) {
                velocity.setX(speed * 0.5 * currentDirection);
            } else {
                velocity.setX(0);
            }
        }

        // Muove effettivamente l'entità in base alla velocità calcolata
        position.setX(position.getX() + velocity.getX());
        position.setY(position.getY() + velocity.getY());
    }
}