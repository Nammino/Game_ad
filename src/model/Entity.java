package model;

import java.awt.Rectangle;

public interface Entity {
    Vector2D getPosition();
    Rectangle getBoundingBox();
    void update(Player player);

    // Metodi di default per la fisica, così le classi che non li usano non daranno errore
    default Vector2D getVelocity() {
        return new Vector2D(0, 0);
    }

    default boolean isGrounded() {
        return true; // Di default le entità statiche non cadono
    }

    default void setGrounded(boolean grounded) {
        // Vuoto per le entità che non ne fanno uso
    }
}