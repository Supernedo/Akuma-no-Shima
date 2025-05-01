package com.novanation.akumanoshima;


public class CameraControls {
    
    private final Player player;
    private final PlayerAnimation playerAnimation;
    private final InputHandler playerInput;
    private final BackgroundManager backgroundManager;

    // Speeds for camera manipulation
    private final int WORLD_SPEED = 2;                      // This determines the speed of the world
    private final int PLAYER_SPEED = 2;                     // This determines the speed of the player

    // Max size of the world = 3840px
    private final int LEFT_THRESHOLD = 600;                 // Defines the area in which the player must enter before the world moves
    private final int RIGHT_THRESHOLD = 3000;               // Defines the area in which the player must leave before the world stops moving

    private int previousDirection = 0;
    
    // Location
    private double xPos;
    private boolean inThreshold = false;

    public CameraControls(Player player, InputHandler playerInput, BackgroundManager backgroundManager)
    {
        this.player = player;
        this.playerInput = playerInput;
        this.backgroundManager = backgroundManager;

        this.playerAnimation = this.player.getPlayerAnimation();

        xPos = player.getX() + 30;
    }

    public void update()
    {
        if (player == null || playerInput == null) return;

        // TODO: Temporary
        if (playerInput.damage != 0)
        {
            player.getHealth().dealDamage(playerInput.damage, true, player);
            playerInput.damage = 0;
        }
        if (playerInput.health != 0)
        {
            player.getHealth().addHealth(playerInput.health);
            playerInput.health = 0;
        }
            
        if (playerInput.isMoving())
        {
            int newWorldSpeed;
            int newPlayerSpeed;
            int bgDirection;

            if (playerInput.getDirection() == -1)
            {
                newPlayerSpeed = -PLAYER_SPEED;
                newWorldSpeed = WORLD_SPEED;
                bgDirection = -1;

                if (player.getX() - (player.getWidth() / 2) - newPlayerSpeed > 0)
                    moveWorld(newPlayerSpeed, newWorldSpeed, bgDirection);
            }
            else if (playerInput.getDirection() == 1)
            {
                newPlayerSpeed = PLAYER_SPEED;
                newWorldSpeed = -WORLD_SPEED;
                bgDirection = 1;

                if (player.getX() + newPlayerSpeed + (player.getWidth() / 2) + newPlayerSpeed < player.getPanelDimensions().getWidth() - player.getWidth() / 2)
                    moveWorld(newPlayerSpeed, newWorldSpeed, bgDirection);
            }

            // May be best to leave the world height as is (For now)
            if (playerInput.canJump() && !player.isJumping())
                player.jump();
        }

        if (!player.canJump())
            playerInput.stopJump();
    }

    private void updatePlayer(int newPlayerSpeed)
    {
        if (WorldGeneration.getWorldType() == WorldType.END) return;

        xPos += newPlayerSpeed;
        player.setWorldPos((int) xPos);
    }

    public void moveWorld(int newPlayerSpeed, int newWorldSpeed, int bgDirection)
    {
        // Increase player speed while jumping
        if (player.isJumping())
        {
            newPlayerSpeed *= 1.5;
            newWorldSpeed *= 1.5;
        }

        if (player.getInLiquid())
        {
            newPlayerSpeed /= 1.5;
            newWorldSpeed /= 1.5;
        }
        
        if (playerInput.getDirection() == -1 && playerInput.getIsMoving())
        {
            if (previousDirection != -1)
            {
                player.setWidth(-player.getWidth());
                player.setX(player.getX() - player.getWidth() - newPlayerSpeed);

                previousDirection = -1;
            }
            
        }
        if (playerInput.getDirection() == 1 && playerInput.getIsMoving())
        {
            if (previousDirection != 1)
            {
                player.setWidth(-player.getWidth());
                player.setX(player.getX() - player.getWidth() - newPlayerSpeed);
                
                previousDirection = 1;
            }
        }

        if (!player.isColliding(newPlayerSpeed))
            updatePlayer(newPlayerSpeed);
        else
        {
            // If is colliding right, allow left movement
            if (player.getCollisionDirection() == 0 && newPlayerSpeed < 0)
                updatePlayer(newPlayerSpeed);

            // If is colliding left, allow right movement
            if (player.getCollisionDirection() == 1 && newPlayerSpeed > 0)
                updatePlayer(newPlayerSpeed);
        }

        // Handle collisions
        if ((int) xPos < LEFT_THRESHOLD || (int) xPos > RIGHT_THRESHOLD)
            if (!player.isColliding(newPlayerSpeed))
                player.move(newPlayerSpeed);
                
        if ((int) xPos > LEFT_THRESHOLD && (int) xPos < RIGHT_THRESHOLD)
        {
            if (!player.isColliding(newPlayerSpeed) && WorldGeneration.getWorldType() == WorldType.END) // TODO: Add Shop here
                player.move(newPlayerSpeed);

            if (!player.isColliding(newPlayerSpeed) && WorldGeneration.getWorldType() != WorldType.END) // TODO: Add Shop here
            {
                player.getPanel().setWorldOffsetX(newWorldSpeed);

                backgroundManager.move(bgDirection);
                WorldGeneration.move(newWorldSpeed);
                
                EnemyManager.move(newWorldSpeed);
                EnemyManager.moveProjectileWithWorld(Math.abs(newWorldSpeed));
            }
        }
    }
}
