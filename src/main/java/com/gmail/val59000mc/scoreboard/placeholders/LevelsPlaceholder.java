package com.gmail.val59000mc.scoreboard.placeholders;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.levels.Level;
import com.gmail.val59000mc.levels.LevelManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.Placeholder;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import com.gmail.val59000mc.statistics.StatManager;
import com.gmail.val59000mc.statistics.StatPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LevelsPlaceholder extends Placeholder {

    public LevelsPlaceholder(){
        super("xp-has", "xp-needed", "level", "progress");
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType, String placeholder) {
        UUID playerId = player.getUniqueId();

        StatManager statManager = UhcCore.getPlugin().getStatManager();
        LevelManager levelManager = UhcCore.getPlugin().getLevelManager();

        StatPlayer statPlayer = statManager.getPlayer(playerId);
        Level level = statPlayer.getLevel();

        switch (placeholder){
            case "xp-has":
                return level.getXp()+"";
            case "xp-needed":
                return level.getNextCost()+"";
            case "level":
                return level.getName();
            case "progress":
                return levelManager.getProgress(level);
        }
        return "?";
    }
}
