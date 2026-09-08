package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShootingEnemy implements Entity {
    private Vector2D position;
    private int width;
    private int height;
    
    private double speed = 0.8;
    private double visionRange = 300.0;
    
    private int wanderTimer = 0;
    private int currentDirection = 1;
    private Random random = new Random();

    private int shootCooldown = 0;
    private List<Projectile> activeProjectiles = new ArrayList<>();

    public ShootingEnemy(double x, double y) {
        this.width = GameStruct.TILE_SIZE;
        this.height = GameStruct.TILE_SIZE;
        this.position = new Vector2D(x, y);
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

 // Aggiungi un overload o modifica il ciclo di aggiornamento dei proiettili dentro update(Player player):
    // Nota: se vuoi passare la mappa, puoi aggiungere un metodo update(Player player, ArrayList<String> map):
    
    public void update(Player player, ArrayList<String> map) {
        if (player == null) return;

        // Danno da contatto fisico se il player gli va addosso
        if (getBoundingBox().intersects(player.getBoundingBox())) {
            player.takeDamage(1);
        }

        double playerX = player.getPosition().getX();
        double playerY = player.getPosition().getY();
        
        double dx = playerX - position.getX();
        double dy = playerY - position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Se il giocatore è a distanza di tiro, spara periodicamente
        if (distance <= visionRange) {
            shootCooldown++;
            if (shootCooldown >= 90) { // Spara ogni circa 1.5 secondi
                shootCooldown = 0;
                Projectile p = new Projectile(position.getX(), position.getY(), playerX, playerY, 4.0);
                activeProjectiles.add(p);
            }
        } else {
            // Altrimenti vaga
            wanderTimer++;
            if (wanderTimer > 120) {
                wanderTimer = 0;
                int choice = random.nextInt(3);
                currentDirection = (choice == 0) ? 0 : (choice == 1) ? 1 : -1;
            }
            if (currentDirection != 0) {
                position.setX(position.getX() + (speed * currentDirection));
            }
        }

        // Aggiorna e pulisce i proiettili sparati passando la mappa
        activeProjectiles.removeIf(p -> !p.isActive());
        for (Projectile p : activeProjectiles) {
            p.update(player, map);
        }
    }

    @Override
    public void update(Player player) {
        // Metodo di fallback dell'interfaccia Entity
        update(player, null);
    }
}