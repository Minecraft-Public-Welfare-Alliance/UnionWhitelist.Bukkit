package top.baimoqilin.unionwhitelist;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load the configuration file
        saveDefaultConfig();

        // Get the values from the configuration file
        String whitelistDataServerEndpoint = getConfig().getString("whitelist-data-server-endpoint");
        String whitelistAddCommandPrefix = getConfig().getString("whitelist-add-command-prefix");
        int checkInterval = getConfig().getInt("check-interval");

        // Schedule a task to send the GET request every checkInterval minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // Create the URL object with the whitelist data server endpoint
                    URL url = new URL(whitelistDataServerEndpoint);

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // Get the response from the server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Split the response into lines and add each line to the whitelist
                    String[] whitelistData = response.toString().split("\n");
                    for (String playerName : whitelistData) {
                        String command = whitelistAddCommandPrefix + " " + playerName;
                        getServer().dispatchCommand(getServer().getConsoleSender(), command);

                    // Outputs the success message to the console
                    getLogger().info("Successfully added " + whitelistData.length + " players to the whitelist!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, 0, checkInterval * 1200); // Convert minutes to ticks

        // Outputs the enable succeed message to the console
        getLogger().info("UnionWhitelist has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}