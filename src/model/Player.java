package model;

public class Player extends Character {

    private final double GRAVITY = 0.5;
    private final double JUMP_STRENGTH = -11.0;
    private final double MOVE_SPEED = 4.0;

    public Player() {
        super();
        getPosition().setX(50);
        getPosition().setY(100);
    }

    public void moveLeft() {
        getVelocity().setX(-MOVE_SPEED);
    }

    public void moveRight() {
        getVelocity().setX(MOVE_SPEED);
    }

    public void stop() {
        getVelocity().setX(0);
    }

    public void jump() {
        if (isGrounded()) {
            getVelocity().setY(JUMP_STRENGTH);
            setGrounded(false);
        }
    }

    @Override
    public void update() {
        getVelocity().setY(getVelocity().getY() + GRAVITY);

        getBoundingBox().setBounds(
            (int) getPosition().getX(),
            (int) getPosition().getY(),
            GameStruct.TILE_SIZE,
            GameStruct.TILE_SIZE
        );
    }
}