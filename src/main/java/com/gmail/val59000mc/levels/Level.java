package com.gmail.val59000mc.levels;

import com.gmail.val59000mc.UhcCore;

public class Level {
    private final int level;
    private int xp;
    private final String name;
    private final int nextCost;

    public Level(Level level){
        this.level = level.getLevel();
        this.xp = level.getXp();
        this.name = level.getName();
        this.nextCost = level.getNextCost();
    }

    public Level(int level, int xp, String name, int nextCost){
        this.level = level;
        this.xp = xp;
        this.name = name;
        this.nextCost = nextCost;
    }

    public Level addXp(int amount){
        xp+=amount;
        if (xp >= nextCost){
            LevelManager levelManager = UhcCore.getPlugin().getLevelManager();
            return levelManager.getLevel(level+1);
        }
        return this;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public String getName() {
        return name.replace("{number}", level+"");
    }

    public String getNameRaw(){
        return name;
    }

    public int getNextCost() {
        return nextCost;
    }
}
