package model;

import java.awt.Rectangle;
import java.util.Random;

public class Enemy implements Entity {
    private Vector2D position;
    private int width;
    private int height;
    
    private double speed = 1.0;          // Velocità generale
    private double visionRange = 220.0;  // Raggio di visibilità
    
    // Variabili per il movimento indipendente (vagare)
    private int wanderTimer = 0;
    private int currentDirection = 1;    // 1 = destra, -1 = sinistra, 0 = fermo
    private Random random = new Random();

    public Enemy(double x, double y) {
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

    @Override
    public void update(Player player) {
        if (player == null) return;

        double playerX = player.getPosition().getX();
        double playerY = player.getPosition().getY();
        
        double dx = playerX - position.getX();
        double dy = playerY - position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // 1. SE IL GIOCATORE È NEL RAGGIO: INSEGUIMENTO ATTIVO
        if (distance <= visionRange) {
            // Insegue il player un po' più velocemente
            if (dx > 0) {
                position.setX(position.getX() + (speed * 1.3));
            } else if (dx < 0) {
                position.setX(position.getX() - (speed * 1.3));
            }
        } 
        // 2. ALTRIMENTI: MOVIMENTO INDIPENDENTE (VAGA)
        else {
            wanderTimer++;
            
            // Ogni tot frame (circa ogni 2 secondi a 60fps), cambia casualmente direzione o si ferma
            if (wanderTimer > 120) {
                wanderTimer = 0;
                int choice = random.nextInt(3); // 0, 1 o 2
                if (choice == 0) {
                    currentDirection = 0; // Fermo
                } else if (choice == 1) {
                    currentDirection = 1; // Va a destra
                } else {
                    currentDirection = -1; // Va a sinistra
                }
            }

            // Muove il nemico a una velocità ridotta mentre vaga
            if (currentDirection != 0) {
                position.setX(position.getX() + (speed * 0.5 * currentDirection));
            }
        }
    }
}