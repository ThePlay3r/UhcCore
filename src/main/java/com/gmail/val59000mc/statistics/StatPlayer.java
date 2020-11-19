package com.gmail.val59000mc.statistics;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.levels.Level;

import java.util.HashMap;
import java.util.UUID;

public class StatPlayer {
    private final UUID uuid;
    private final HashMap<Stat, Integer> stats;
    private Level level;

    public StatPlayer(UUID uuid){
        this.uuid = uuid;
        this.stats = new HashMap<>();
        this.level = UhcCore.getPlugin().getLevelManager().getLevel(1);
    }

    public StatPlayer(UUID uuid, HashMap<Stat, Integer> stats, Level level){
        this.uuid = uuid;
        this.stats = stats;
        this.level = level;
    }

    public void addStat(Stat stat, int amount){
        if (stats.containsKey(stat)){
            amount+=stats.get(stat);
        }
        stats.put(stat, amount);
    }

    public int getStat(Stat stat){
        if (stats.containsKey(stat)){
            return stats.get(stat);
        }
        return 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
