package com.novanation.akumanoshima;


import java.util.ArrayList;

public class AmmoStock{

    public ArrayList<Bullet> bullets;
    private int ammoCount;
    private int currAmmoCount;

    public AmmoStock(int ammoCount){
        bullets = new ArrayList<>();
        this.ammoCount = ammoCount;
        initBullets();
}

    public void initBullets(){
        for(int i = 0; i < ammoCount; i++){
            bullets.add(new Bullet());
        }
        currAmmoCount = ammoCount;
    }
}
