package com.gmail.val59000mc.listeners;

import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostJoinEvent;
import com.gmail.val59000mc.players.PlayersManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BukkitPartiesPlayerPostJoinListener implements Listener {

    private final PlayersManager playersManager;

    public BukkitPartiesPlayerPostJoinListener(PlayersManager playersManager){
        this.playersManager = playersManager;
    }

    @EventHandler
    public void onPartyJoin(BukkitPartiesPlayerPostJoinEvent event){
        System.out.println("Party joined.");
        playersManager.assignPartyTeam(event.getPartyPlayer().getPlayerUUID(), event.getInviter());
    }
}
