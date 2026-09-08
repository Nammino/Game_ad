package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShootingEnemy implements Entity {
    private Vector2D position;
    private Vector2D velocity;
    private boolean grounded = false;
    private int width;
    private int height;
    
    private double speed = 1.0;
    private double visionRange = 300.0;
    
    private int wanderTimer = 0;
    private int currentDirection = 1;
    private Random random = new Random();

    private int shootCooldown = 0;
    private List<Projectile> activeProjectiles = new ArrayList<>();
    
    private boolean facingRight = true;
    
    private int damageCooldown = 0; // Timer per il cooldown del danno da contatto

    public ShootingEnemy(double x, double y) {
        this.width = GameStruct.TILE_SIZE;
        this.height = GameStruct.TILE_SIZE;
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
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
        return new Rectangle(
            (int) position.getX() + (shrinkX / 2), 
            (int) position.getY(), 
            width - shrinkX, 
            height
        );
    }

    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void update(Player player, ArrayList<String> map) {
        if (player == null) return;

        // --- GESTIONE COOLDOWN DANNO DA CONTATTO ---
        if (damageCooldown > 0) {
            damageCooldown--;
        }

        if (getBoundingBox().intersects(player.getBoundingBox())) {
            if (damageCooldown == 0) {
                player.takeDamage(5); // Toglie 5 di vita
                damageCooldown = 180; // 180 frame = circa 3 secondi di attesa prima del prossimo danno
            }
        }

        // --- GESTIONE GRAVITÀ ---
        if (!grounded) {
            velocity.setY(velocity.getY() + 0.5); // Forza di gravità
        }

        double playerX = player.getPosition().getX();
        double playerY = player.getPosition().getY();
        
        double dx = playerX - position.getX();
        double dy = playerY - position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Se il giocatore è a distanza di tiro
        if (distance <= visionRange) {
            shootCooldown++;
            if (shootCooldown >= 80) {  
                shootCooldown = 0;
                Projectile p = new Projectile(position.getX(), position.getY(), playerX, playerY, 5.5);
                activeProjectiles.add(p);
            }
            
            if (dx > 5) {
                velocity.setX(speed * 1.3);
                facingRight = true;  
            } else if (dx < -5) {
                velocity.setX(-speed * 1.3);
                facingRight = false; 
            } else {
                velocity.setX(0);
            }
        } else {
            wanderTimer++;
            if (wanderTimer > 180) {
                wanderTimer = 0;
                int choice = random.nextInt(3);
                currentDirection = (choice == 0) ? 0 : (choice == 1) ? 1 : -1;
            }
            
            if (currentDirection != 0) {
                velocity.setX(speed * 0.5 * currentDirection);
                facingRight = (currentDirection > 0); 
            } else {
                velocity.setX(0);
            }
        }

        // Rimozione proiettili inattivi e aggiornamento degli stessi
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