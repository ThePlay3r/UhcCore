package com.gmail.val59000mc.statistics;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.database.QueryManager;
import com.gmail.val59000mc.events.UhcPlayerKillEvent;
import com.gmail.val59000mc.events.UhcWinEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.levels.LevelManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class StatManager implements Listener {
    private final UhcCore plugin;
    private final HashMap<UUID, StatPlayer> players;

    public StatManager(UhcCore plugin){
        this.plugin = plugin;
        players = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        minuteRunnable();
    }

    public void loadPlayer(UUID uuid){
        QueryManager queryManager = plugin.getQueryManager();
        queryManager.loadStatPlayerSync(uuid);
    }

    public void setPlayer(UUID uuid, StatPlayer statPlayer){
        players.put(uuid, statPlayer);
        System.out.println(players.toString());
    }

    public StatPlayer getPlayer(UUID uuid){
        if (players.get(uuid) == null){
            loadPlayer(uuid);
        }
        return players.get(uuid);
    }

    public void savePlayer(UUID uuid){
        StatPlayer statPlayer = getPlayer(uuid);
        if (statPlayer == null) return;
        QueryManager queryManager = plugin.getQueryManager();
        queryManager.saveStatPlayer(statPlayer);
    }

    @EventHandler
    private void onJoin(AsyncPlayerPreLoginEvent event){
        loadPlayer(event.getUniqueId());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event){
        savePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onKill(UhcPlayerKillEvent event){
        UUID victimId = event.getKilled().getUuid();
        UUID killerId = event.getKiller().getUuid();
        StatPlayer victim = getPlayer(victimId);
        StatPlayer killer = getPlayer(killerId);

        victim.addStat(Stat.DEATHS, 1);
        killer.addStat(Stat.KILLS, 1);

        LevelManager levelManager = UhcCore.getPlugin().getLevelManager();
        int reward = levelManager.getRewardKill();
        event.getKiller().sendMessage("§aUHC §8» §6+" + reward + " Přidané UHC zkušenosti (Zabití).");
        killer.setLevel(killer.getLevel().addXp(reward));

        setPlayer(victimId, victim);
        setPlayer(killerId, killer);
    }

    @EventHandler
    private void onWin(UhcWinEvent event){
        LevelManager levelManager = UhcCore.getPlugin().getLevelManager();
        for (UhcPlayer winner : event.getWinners()){
            UUID id = winner.getUuid();
            StatPlayer statPlayer = getPlayer(id);

            statPlayer.addStat(Stat.WINS, 1);

            int reward = levelManager.getRewardWin();
            winner.sendMessage("§aUHC §8» §6+" + reward + " Přidané UHC zkušenosti (Výhra).");
            statPlayer.setLevel(statPlayer.getLevel().addXp(reward));

            setPlayer(id, statPlayer);
        }
    }

    private void minuteRunnable(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, ()->{
            GameManager gameManager = GameManager.getGameManager();
            if (gameManager.getGameState() == GameState.PLAYING || gameManager.getGameState() == GameState.DEATHMATCH){
                LevelManager levelManager = UhcCore.getPlugin().getLevelManager();
                for (Player player : Bukkit.getOnlinePlayers()){
                    UUID playerId = player.getUniqueId();
                    StatPlayer statPlayer = getPlayer(playerId);

                    int reward = levelManager.getRewardMinute();
                    player.sendMessage("§aUHC §8» §6+" + reward + " Přidané UHC zkušenosti (Herní Čas).");
                    statPlayer.setLevel(statPlayer.getLevel().addXp(reward));

                    setPlayer(playerId, statPlayer);
                }
            }
        }, 1, 20*60);
    }
}
