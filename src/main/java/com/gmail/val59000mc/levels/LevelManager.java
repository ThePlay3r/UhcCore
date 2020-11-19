package com.gmail.val59000mc.levels;

import me.pljr.pljrapi.managers.ConfigManager;
import me.pljr.pljrapi.utils.NumberUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class LevelManager {
    private final HashMap<Integer, Level> levels;

    private final String othersName;
    private final int othersCost;

    private final int rewardMinute;
    private final int rewardKill;
    private final int rewardWin;

    private final String progressSymbol;
    private final String progressUnlockedColor;
    private final String progressLockedColor;
    private final String progressFormat;

    public LevelManager(ConfigManager config){
        levels = new HashMap<>();
        ConfigurationSection csLevels = config.getConfigurationSection("levels.levels");
        if (csLevels != null){
            for (String level : csLevels.getKeys(false)){
                String name = config.getString("levels.levels." + level + ".name");
                int cost = config.getInt("levels.levels." + level + ".rankup-cost");
                if (NumberUtil.isInt(level)){
                    int levelNumber = Integer.parseInt(level);
                    levels.put(levelNumber, new Level(levelNumber, 0, name, cost));
                    continue;
                }
                String[] range = level.split("-");
                if (range.length == 2){
                    String rangeFrom = range[0];
                    String rangeTo = range[1];
                    if (NumberUtil.isInt(rangeFrom) && NumberUtil.isInt(rangeTo)){
                        int rangeFromInt = Integer.parseInt(rangeFrom);
                        int rangeToInt = Integer.parseInt(rangeTo);
                        for (int i = rangeFromInt; i<=rangeToInt; i++){
                            levels.put(i, new Level(i, 0, name, cost));
                        }
                    }
                }
            }
        }
        othersName = config.getString("levels.levels.others.name");
        othersCost = config.getInt("levels.levels.others.rankup-cost");

        rewardMinute = config.getInt("levels.xp.per-minute");
        rewardKill = config.getInt("levels.xp.per-kill");
        rewardWin = config.getInt("levels.xp.game-win");

        progressSymbol = config.getString("levels.progress-bar.symbol");
        progressUnlockedColor = config.getString("levels.progress-bar.unlocked-color");
        progressLockedColor = config.getString("levels.progress-bar.locked-color");
        progressFormat = config.getString("levels.progress-bar.format");
    }

    public Level getLevel(int level){
        if (levels.containsKey(level)){
            return levels.get(level);
        }
        return new Level(level, 0, othersName, othersCost);
    }

    public String getProgress(Level level){
        int cost = level.getNextCost();
        int xp = level.getXp();
        float onePercent = cost / 100;
        float percent = xp / onePercent;
        int symbols = Math.round(percent / 10);
        int missingSymbols = Math.round((100 - percent) / 10);
        StringBuilder progress = new StringBuilder();
        while (symbols != 0){
            progress.append(progressUnlockedColor).append(progressSymbol);
            symbols--;
        }
        while (missingSymbols != 0){
            progress.append(progressLockedColor).append(progressSymbol);
            missingSymbols--;
        }
        return progressFormat.replace("{progress}", progress.toString());
    }

    public int getRewardMinute() {
        return rewardMinute;
    }
    public int getRewardKill() {
        return rewardKill;
    }
    public int getRewardWin() {
        return rewardWin;
    }
}
