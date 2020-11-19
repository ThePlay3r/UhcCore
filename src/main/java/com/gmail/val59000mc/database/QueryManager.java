package com.gmail.val59000mc.database;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.levels.Level;
import com.gmail.val59000mc.statistics.Stat;
import com.gmail.val59000mc.statistics.StatManager;
import com.gmail.val59000mc.statistics.StatPlayer;
import me.pljr.pljrapi.database.DataSource;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class QueryManager implements Listener {
    private final DataSource dataSource;
    private final UhcCore plugin;

    public QueryManager(DataSource dataSource, UhcCore plugin){
        this.dataSource = dataSource;
        this.plugin = plugin;
    }

    public void loadStatPlayerSync(UUID uuid){
        try {
            Connection connectionStats = dataSource.getConnection();
            PreparedStatement statementStats = connectionStats.prepareStatement(
                    "SELECT * FROM uhccore_stats WHERE uuid=?"
            );
            statementStats.setString(1, uuid.toString());
            ResultSet resultsStats = statementStats.executeQuery();

            StatManager statManager = UhcCore.getPlugin().getStatManager();

            if (resultsStats.next()){
                HashMap<Stat, Integer> stats = new HashMap<>();
                for (Stat stat : Stat.values()){
                    stats.put(stat, resultsStats.getInt(stat.toString().toLowerCase()));
                }

                Connection connectionLevels = dataSource.getConnection();
                PreparedStatement statementLevels = connectionLevels.prepareStatement(
                        "SELECT * FROM uhccore_levels WHERE uuid=?"
                );
                statementLevels.setString(1, uuid.toString());
                ResultSet resultsLevels = statementLevels.executeQuery();
                resultsLevels.next();
                Level level = new Level(
                        resultsLevels.getInt("level"),
                        resultsLevels.getInt("xp"),
                        resultsLevels.getString("name"),
                        resultsLevels.getInt("next_cost"));
                dataSource.close(connectionLevels, statementLevels, resultsLevels);

                statManager.setPlayer(uuid, new StatPlayer(uuid, stats, level));
            }else{
                statManager.setPlayer(uuid, new StatPlayer(uuid));
            }
            dataSource.close(connectionStats, statementStats, resultsStats);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void saveStatPlayer(StatPlayer player){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
            try {
                Connection connectionStats = dataSource.getConnection();
                PreparedStatement statementStats = connectionStats.prepareStatement(
                        "REPLACE INTO uhccore_stats VALUES (?,?,?,?)"
                );
                statementStats.setString(1, player.getUuid().toString());
                int loop = 2;
                for (Stat stat : Stat.values()){
                    statementStats.setInt(loop, player.getStat(stat));
                    loop++;
                }
                statementStats.executeUpdate();
                dataSource.close(connectionStats, statementStats, null);

                Connection connectionLevels = dataSource.getConnection();
                PreparedStatement statementLevels = connectionLevels.prepareStatement(
                        "REPLACE INTO uhccore_levels VALUES (?,?,?,?,?)"
                );
                Level level = player.getLevel();
                statementLevels.setString(1, player.getUuid().toString());
                statementLevels.setInt(2, level.getLevel());
                statementLevels.setInt(3, level.getXp());
                statementLevels.setString(4, level.getNameRaw());
                statementLevels.setInt(5, level.getNextCost());
                statementLevels.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }

    public void setupTables(){
        try {
            Connection statsConnection = dataSource.getConnection();
            PreparedStatement statsStatement = statsConnection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS uhccore_stats (" +
                            "uuid char(36) NOT NULL PRIMARY KEY," +
                            "kills int NOT NULL," +
                            "deaths int NOT NULL," +
                            "wins int NOT NULL);"
            );
            statsStatement.executeUpdate();
            dataSource.close(statsConnection, statsStatement, null);

            Connection levelsConnection = dataSource.getConnection();
            PreparedStatement levelsStatement = levelsConnection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS uhccore_levels (" +
                            "uuid char(36) NOT NULL PRIMARY KEY," +
                            "level int NOT NULL," +
                            "xp int NOT NULL," +
                            "name varchar(255) NOT NULL," +
                            "next_cost int NOT NULL);"
            );
            System.out.println("Created Levels Table");
            levelsStatement.executeUpdate();
            dataSource.close(levelsConnection, levelsStatement, null);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
