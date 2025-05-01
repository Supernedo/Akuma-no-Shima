package com.novanation.akumanoshima;

import java.awt.Graphics2D;
import java.awt.Image;

public class EnemyMaouAnimation {

    // Animations
    private final Animation walkAnim;
    private final Animation attackAnim;


    private final EnemyMaou maou;

    private final int WALK_LENGTH = 6;
    private final int ATTACK_LENGTH = 6;

    private final double SPEED_SCALE = 3.5;

    public EnemyMaouAnimation(EnemyMaou maou)
    {
        this.maou = maou;
        walkAnim = new Animation(true);
        attackAnim = new Animation(false);

        // Dynamically load the animation frames
        for (int i = 1; i <= WALK_LENGTH; i++)
        {
            Image frame = ImageManager.loadImage("/gfx/animations/maou/walk/maou_" + i + ".png");
            walkAnim.addFrame(frame, (int) (1000 / (WALK_LENGTH * SPEED_SCALE)) * 5); // Average is about 2 steps a second
        }

        for (int i = 1; i <= ATTACK_LENGTH; i++)
        {
            Image frame = ImageManager.loadImage("/gfx/animations/maou/attack/maou_" + i + ".png");
            attackAnim.addFrame(frame, (int) (1000 / (i * 200)) * 60); // Average is about 2 steps a second
        }
    }

    public void startWalk() { walkAnim.start(); }
    public void stopWalk() { walkAnim.stop(); }

    public void startAttack() { attackAnim.start(); }
    public void stopAttack() { attackAnim.stop(); }

    public boolean isWalkStillActive() { return walkAnim.isStillActive(); }
    public boolean isAttackStillActive() { return attackAnim.isStillActive(); }
    
    public void draw(Graphics2D g2)
    {
        if (walkAnim.isStillActive())
        {
            walkAnim.update();
            g2.drawImage(walkAnim.getImage(), (int) maou.getX(), (int) maou.getY(), (int) maou.getWidth(), maou.getHeight(), null);
        }

        if (attackAnim.isStillActive())
        {
            attackAnim.update();
            g2.drawImage(attackAnim.getImage(), (int) maou.getX(), (int) maou.getY(), (int) maou.getWidth(), maou.getHeight(), null);
        }
    }
}

