package com.novanation.akumanoshima;


import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    //Mouse handling 
    private static int mouseX = -1;
    private static int mouseY = -1;
    private static int mouseB = 1;
    private double angle;

    private static final double CONE_ANGLE = Math.PI / 2; // 90 degrees in radians
    private static final double BASE_ANGLE = 0; 
    private static final double FIXED_DISTANCE = 400; 

    // for Jumping
    private boolean isMoving;
    private boolean canJump;

    Player player;
    AssualtWeapon ar;
    private VandalPerk vandalPerk = new VandalPerk();
    private VitalityPerk vitalityPerk = new VitalityPerk();
    private SpeedsterPerk speedsterPerk = new SpeedsterPerk();

    private boolean lockingEnabled = true;
    private Robot robot;
    private boolean isRecentering = false;

    private int direction = 0;

    // TODO: Temporary
    public int damage;
    public int health;

    // In case of multiplayer, this will be instantiated
    public InputHandler(Player player)
    {
        this.player = player;

        try {
            robot = new Robot();
        } catch (AWTException e) { }
    }

    // Accessors
    public boolean canJump() { return canJump; }
    public int getDirection() { return direction; }
    public boolean isMoving() { return isMoving; }

    // Mutators
    public void stopJump() { canJump = false; }

    @Override
    public void keyPressed(KeyEvent e) {
        // I will temporarily configure the physics here for the player
        switch (e.getKeyCode()) {
            // Movement
            case KeyEvent.VK_A -> {
                isMoving = true;
                direction = -1;
            }
            case KeyEvent.VK_D -> {
                 isMoving = true;
                 direction = 1;
            }
            case KeyEvent.VK_SPACE -> {
                if (!canJump)
                {
                    isMoving = true;
                    canJump = true;
                }
            }

            // Mouse
            case KeyEvent.VK_ESCAPE -> {
                lockingEnabled = !lockingEnabled;
            }

            // Perks
            // case KeyEvent.VK_V -> {
            //     if(!vandalPerk.isActive()) {
            //         vandalPerk.applyEffect(player);
            //         vandalPerk.setActive(true);
            //     }
            // }
            // case KeyEvent.VK_B -> { 
            //     if(!vitalityPerk.isActive()) {
            //       vitalityPerk.applyEffect(player);
            //        vitalityPerk.setActive(true);
            //      }
            // }
            // case KeyEvent.VK_N -> {
            //     if (!speedsterPerk.isActive()) {
            //         speedsterPerk.applyEffect(player);
            //     }
            // }
        }
    }
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (direction == 1 && e.getKeyCode() == KeyEvent.VK_D)
            stopMovement();
        if (direction == -1 && e.getKeyCode() == KeyEvent.VK_A)
            stopMovement();
      
        // Temporary
        if (e.getKeyCode() == KeyEvent.VK_K)
            damage = 1;
        if (e.getKeyCode() == KeyEvent.VK_L)
            health = 1;
        // if (e.getKeyCode() == KeyEvent.VK_ENTER)
        //     EnemyManager.killAllEntities();
        // if (e.getKeyCode() == KeyEvent.VK_SLASH)
        //     EnemyManager.summonMinions();
    }

    public boolean getLocking() { return lockingEnabled; }

    public boolean getIsMoving() { return isMoving; }

    private void stopMovement()
    {
        isMoving = false;
        direction = 0;
    }

    public void setWeapon(AssualtWeapon ar) {
        this.ar = ar;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    // Mouse controls

    //getters and setters
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }
    public int getButton() { return mouseB; }
    public double getAngle() { return angle; }

    //Mouse Methods

    private void updateMouseAngle(){
        double distX = mouseX - player.getX();
        double distY = mouseY - player.getY();
        double rawAngle = Math.atan2(distY, distX);
        
        // Clamp the angle to our 90-degree cone
        angle = clampAngle(rawAngle);
        
        // Calculate virtual cursor position at fixed distance
        double virtualX = player.getX() + FIXED_DISTANCE * Math.cos(angle);
        double virtualY = player.getY() + FIXED_DISTANCE * Math.sin(angle);
        
        // Update mouse coordinates to virtual position
        mouseX = (int)virtualX;
        mouseY = (int)virtualY;
    }

    
    private double clampAngle(double rawAngle) {
        // Normalize the angle to be between -PI and PI
        while (rawAngle > Math.PI) rawAngle -= 2 * Math.PI;
        while (rawAngle < -Math.PI) rawAngle += 2 * Math.PI;
        
        // Calculate the half cone for up and down from horizontal
        double halfCone = CONE_ANGLE / 2;
        
        // Clamp the angle to the cone
        if (rawAngle > halfCone) rawAngle = halfCone;
        if (rawAngle < -halfCone) rawAngle = -halfCone;
        
        return rawAngle;
    }

    public void updateWeaponPosition() {
        if (ar == null || player == null) return;
        
        // Offset distance from player center
        int offsetX = 60; // Horizontal offset - adjust as needed
        int offsetY = 30; // Vertical offset - adjust as needed
        
        // Player direction affects where the weapon is positioned
        int directionMultiplier = (mouseX < player.getX()) ? -1 : 1;
        
        // Base position at player center
        double baseX = player.getX();
        double baseY = player.getY() + offsetY; // Adjust Y position with a vertical offset
        
        // Apply offset in the direction of the mouse
        double weaponX = baseX + offsetX * Math.cos(angle) * directionMultiplier;
        double weaponY = baseY + offsetX * Math.sin(angle);

        ar.setX((int)weaponX);
        ar.setY((int)weaponY);
        ar.setRotation(angle);
        
        // Set weapon facing direction based on mouse position
        ar.setFacingLeft(false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        updateMouseAngle();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isRecentering) {
            isRecentering = false;
            return;
        }

        mouseX = e.getX();
        mouseY = e.getY();
        updateMouseAngle();
        updateWeaponPosition();

        if (lockingEnabled && player != null)
        {
            Point panelPos = player.getPanel().getLocationOnScreen();
            
            int mouseScreenX = e.getXOnScreen();
            int mouseScreenY = e.getYOnScreen();

            Rectangle bounds = new Rectangle((int) (player.getPanel().getWidth() / 3) + (int) player.getX(), panelPos.y + 30, 1, 630);

            // Clamp mouse position
            int clampedX = Math.max(bounds.x, Math.min(mouseScreenX, bounds.x + bounds.width - 1));
            int clampedY = Math.max(bounds.y, Math.min(mouseScreenY, bounds.y + bounds.height - 1));

            if (mouseScreenX != clampedX || mouseScreenY != clampedY)
            {
                isRecentering = true;
                robot.mouseMove(clampedX, clampedY);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseB = e.getButton();
        if (e.getButton() == MouseEvent.BUTTON1){ // Left click
            ar.setArCanFire(true);
            ar.setArIsFiring(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseB = -1;
        if (e.getButton() == MouseEvent.BUTTON1) { // Left click released
            ar.setArIsFiring(false);
            ar.setArCanFire(false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
