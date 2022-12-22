package fr.nkri.crates;

import fr.nkri.crates.commands.CCrate;
import fr.nkri.crates.events.EBlock;
import fr.nkri.crates.events.EInventory;
import fr.nkri.crates.objects.Clef;
import fr.nkri.crates.objects.Crate;
import fr.nkri.crates.objects.Gain;
import fr.nkri.crates.utils.ColorCrates;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class MCrates extends JavaPlugin {
    public static MCrates plugin;

    public static List<Crate> creates = new ArrayList<Crate>();
    private static File dataFile;
    private static YamlConfiguration data;

    @Override
    public void onEnable()
    {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new EBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EInventory(), this);
        Bukkit.getPluginCommand("crate").setExecutor(new CCrate());
        loadConfig();
    }

    public void loadConfig()
    {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }
        data = new YamlConfiguration();
        try {
            data.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        ConfigurationSection section = getDataConfig().getConfigurationSection("crates_loc");
        if (section != null){

            Set<String> locs = section.getKeys(false);
            for (String loc : locs){
                ConfigurationSection crates_loc = section.getConfigurationSection(loc);

                String name = crates_loc.getString("name");
                String block_name = crates_loc.getString("block");
                String world = crates_loc.getString("world");
                String world_uid = crates_loc.getString("world_uid");

                int X = crates_loc.getInt("X");
                int Y = crates_loc.getInt("Y");
                int Z = crates_loc.getInt("Z");
                World world_obj = Bukkit.getWorld(world);
                Block block = world_obj.getBlockAt(X, Y, Z);
                if (block != null)
                    if (block.getType() != Material.AIR)
                        if (block_name.equals(block.getType().name()))
                            block.setMetadata("CratesPlus.Crate", (MetadataValue)new FixedMetadataValue(this, name));
            }
        }


        creates = new ArrayList<Crate>();
        this.saveDefaultConfig();
        this.reloadConfig();
        ConfigurationSection cratelist = getConfig().getConfigurationSection("Crates");
        Set<String> crates = cratelist.getKeys(false);
        for (String crateName : crates){

            ConfigurationSection crate = cratelist.getConfigurationSection(crateName);
            ConfigurationSection gains = crate.getConfigurationSection("Winnings");
            ArrayList<Gain> gains_liste_object = new ArrayList<Gain>();
            Set<String> gains_liste = gains.getKeys(false);

            int MaxItemGains = 0;
            for (String ID : gains_liste){
                ConfigurationSection gain_detail = gains.getConfigurationSection(ID);
                Gain.GainType type = Gain.GainType.valueOf(gain_detail.getString("Type"));
                String item = gain_detail.getString("Item Type");
                String name = gain_detail.getString("Name");
                int pourcentage = gain_detail.getInt("Percentage");
                int Items = 5;
                if (gain_detail.contains("Items"))
                    Items = gain_detail.getInt("Items");
                if (Items > MaxItemGains)
                    MaxItemGains = Items;
                List<String> commands = gain_detail.getStringList("Commands");
                gains_liste_object.add(new Gain(ID, type, item, name, pourcentage, Items, commands));
            }

            ConfigurationSection key = crate.getConfigurationSection("Key");
            String item = key.getString("Item");
            String name = key.getString("Name").replaceAll("%type%", crateName);
            boolean enchanted = key.getBoolean("Enchanted");
            double kb = crate.getDouble("Knockback");
            boolean broadcast = crate.getBoolean("Broadcast");
            boolean firework = crate.getBoolean("Firework");
            boolean preview = crate.getBoolean("Preview");
            String block = crate.getString("Block");

            ColorCrates color = ColorCrates.valueOf(crate.getString("Color"));
            creates.add(new Crate(crateName, gains_liste_object, new Clef(item, name, enchanted),
                    kb, broadcast, firework, preview, block, color, MaxItemGains));
        }
    }

    public static FileConfiguration getDataConfig() {
        return data;
    }

    public static void saveDataConfig() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addCrate(Crate crate, Location loc, String Block) {
        ConfigurationSection section = getDataConfig().getConfigurationSection("crates_loc");
        if (section == null)
            section = getDataConfig().createSection("crates_loc");
        ConfigurationSection crates_loc = section.createSection(UUID.randomUUID().toString());

        crates_loc.set("name", crate.getName());
        crates_loc.set("block", Block);
        crates_loc.set("world", loc.getWorld().getName());
        crates_loc.set("world_uid", loc.getWorld().getUID().toString());
        crates_loc.set("X", loc.getBlockX());
        crates_loc.set("Y", loc.getBlockY());
        crates_loc.set("Z", loc.getBlockZ());

        saveDataConfig();
    }

    public static List<Gain> getRewards(String player) {
        ArrayList<Gain> gains_joueur = new ArrayList<Gain>();
        ConfigurationSection section = getDataConfig().getConfigurationSection("rewards");
        if (section == null)
            return gains_joueur;
        ConfigurationSection reward = section.getConfigurationSection(player);
        if (reward == null)
            return gains_joueur;
        else if (reward.getKeys(false).size() > 0){

            for (String gain : reward.getKeys(false)){

                ConfigurationSection gain_section = reward.getConfigurationSection(gain);
                String ID = gain_section.getString("ID");
                String name = gain_section.getString("Name");
                String item = gain_section.getString("Item");

                Gain.GainType gainType = Gain.GainType.valueOf(gain_section.getString("GainType"));

                List<String> commands = new ArrayList<String>();

                if (gainType.equals(Gain.GainType.COMMAND))
                    commands = gain_section.getStringList("commands");
                int pourcentage = gain_section.getInt("pourcentage");
                int Items = 5;

                if (gain_section.contains("Items"))
                    Items = gain_section.getInt("Items");
                gains_joueur.add(new Gain(ID, gainType, item, name, pourcentage, Items, commands));
            }
        }
        return gains_joueur;
    }

    public static boolean hasReward(String player) {
        ConfigurationSection section = getDataConfig().getConfigurationSection("rewards");
        if (section == null)
            return false;
        ConfigurationSection reward = section.getConfigurationSection(player);
        if (reward == null)
            return false;
        else if (reward.getKeys(false).size() > 0)
            return true;
        else
            return false;
    }

    public static int countReward(String player) {
        ConfigurationSection section = getDataConfig().getConfigurationSection("rewards");
        if (section == null)
            return 0;
        ConfigurationSection reward = section.getConfigurationSection(player);
        if (reward == null)
            return 0;
        else if (reward.getKeys(false).size() > 0)
            return reward.getKeys(false).size();
        else
            return 0;
    }

    public static void addReward(String player, Gain gain) {
        ConfigurationSection section = getDataConfig().getConfigurationSection("rewards");
        if (section == null)
            section = getDataConfig().createSection("rewards");
        ConfigurationSection reward = section.getConfigurationSection(player);
        if (reward == null)
            reward = section.createSection(player);
        ConfigurationSection gain_section = reward.createSection(UUID.randomUUID().toString());

        gain_section.set("ID", gain.getID());
        gain_section.set("Name", gain.getName());
        gain_section.set("Item", gain.getItem());
        gain_section.set("Items", gain.getItems());
        gain_section.set("GainType", gain.getGainType().name());

        if (gain.getGainType().equals(Gain.GainType.COMMAND))
            gain_section.set("commands", gain.getCommands());
        gain_section.set("pourcentage", gain.getPourcentage());

        saveDataConfig();
    }

    @SuppressWarnings("unused")
    public static void removeReward(String player, Gain gain_obj) {
        ConfigurationSection section = getDataConfig().getConfigurationSection("rewards");
        if (section != null){
            ConfigurationSection reward = section.getConfigurationSection(player);
            if (reward != null){
                Set<String> gain_sections = reward.getKeys(false);
                for (String gain : gain_sections){
                    ConfigurationSection gain_section = reward.getConfigurationSection(gain);

                    String ID = gain_section.getString("ID");
                    String name = gain_section.getString("Name");
                    String item = gain_section.getString("Item");

                    Gain.GainType gainType = Gain.GainType.valueOf(gain_section.getString("GainType"));

                    List<String> commands = new ArrayList<String>();

                    if (gainType.equals(Gain.GainType.COMMAND))
                        commands = gain_section.getStringList("commands");
                    int pourcentage = gain_section.getInt("pourcentage");

                    if (gain_obj.getID().equals(ID)
                            && gain_obj.getItem().equals(item) && gain_obj.getName().equals(name)){
                        reward.set(gain, null);
                        saveDataConfig();
                        return;
                    }
                }
            }
        }
    }

    public static int getEmptySlots(Player player)
    {
        int empty_slot = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++)
            if (player.getInventory().getContents()[i] == null || player.getInventory().getContents()[i].getType().equals(Material.AIR))
                empty_slot++;
        for (int i = 0; i < player.getInventory().getArmorContents().length; i++)
            if (player.getInventory().getArmorContents()[i] == null ||player.getInventory().getArmorContents()[i].getType().equals(Material.AIR))
                empty_slot--;
        if (player.getInventory().getItemInHand() == null || player.getInventory().getItemInHand().getType().equals(Material.AIR))
            empty_slot--;
        return empty_slot;
    }

    public static void removeCrate(Crate crate, Location loc, String Block) {

        ConfigurationSection section = getDataConfig().getConfigurationSection("crates_loc");

        if (section != null){
            Set<String> locs = section.getKeys(false);

            for (String loc1 : locs){
                ConfigurationSection crates_loc = section.getConfigurationSection(loc1);
                String block_name = crates_loc.getString("block");
                String world_uid = crates_loc.getString("world_uid");

                int X = crates_loc.getInt("X");
                int Y = crates_loc.getInt("Y");
                int Z = crates_loc.getInt("Z");

                if (block_name.equals(Block)){
                    if (loc.getBlockX() == X && loc.getBlockY() == Y && loc.getBlockZ() == Z){
                        section.set(loc1, null);
                        saveDataConfig();
                        return;
                    }
                }
            }
        }
    }

    public static Crate getCrate(String name) {
        for (Crate crate : creates){
            if (crate.getName().toLowerCase().equals(name.toLowerCase()))
                return crate;
        }
        return null;
    }

    public static Crate getCrate() {
        for (Crate crate : creates){
                return crate;
        }
        return null;
    }

}
