package model;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Player implements Entity {
    private Vector2D position;
    private Vector2D velocity;
    private int width;
    private int height;
    private int health;
    private int maxHealth;
    private Inventory inventory;
    
    // Stati di movimento e direzione
    private boolean grounded = false;
    private boolean left = false;
    private boolean right = false;
    private boolean jumpRequested = false;
    private boolean facingRight = true;

    // Costruttore vuoto richiesto da GameStructImpl (es. new Player())
    public Player() {
        this(100, 100); // Posizione di default
    }

    public Player(double x, double y) {
        this.width = GameStruct.TILE_SIZE;
        this.height = GameStruct.TILE_SIZE;
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.maxHealth = 100;
        this.health = 100;
        this.inventory = new Inventory();
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle((int) position.getX(), (int) position.getY(), width, height);
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }
    
    public void resetHealth() {
        this.health = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // --- Gestione Direzione Sprite ---
    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    // --- Metodi di Movimento e Stati richiesti da Controller e CollisionManager ---
    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
        if (left) {
            this.facingRight = false; // Se va a sinistra, si gira a sinistra
        }
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
        if (right) {
            this.facingRight = true; // Se va a destra, si gira a destra
        }
    }

    public boolean isJumpRequested() {
        return jumpRequested;
    }

    public void setJumpRequested(boolean jumpRequested) {
        this.jumpRequested = jumpRequested;
    }

    @Override
    public void update(Player player) {
        // Metodo vuoto se richiesto dall'interfaccia Entity
    }
    
    // --- Gestisce solo la velocità idddddn base ai comandi, il movimento vero e proprio lo fa il CollisionManager ---
    public void update(ArrayList<String> map) {
        double gravity = 0.5;
        double moveSpeed = 4.0;
        double jumpStrength = -11;

        // Gestione movimento orizzontale (Velocità X)
        double vx = 0;
        if (left) {
            vx = -moveSpeed;
        }
        if (right) {
            vx = moveSpeed;
        }
        velocity.setX(vx);

        // Gestione salto (se a terra e viene richiesto il salto)
        if (jumpRequested && grounded) {
            velocity.setY(jumpStrength);
            grounded = false;
            jumpRequested = false;
        }

        // Applicazione gravità alla velocità Y se non è a terra
        if (!grounded) {
            velocity.setY(velocity.getY() + gravity);
        }
    }
}