package fr.nkri.crates.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.mrcubee.langlib.Lang;
import fr.nkri.crates.MCrates;
import fr.nkri.crates.objects.Crate;
import fr.nkri.crates.objects.Gain;
import fr.nkri.crates.objects.ScrollGain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class CrateManager {
	
	public static void openRecompenses(Player player) {
		List<Gain> rewards = MCrates.getRewards(player.getName());

		if (rewards.size() > 0){

			Inventory crate_inv = Bukkit.createInventory(null, 54,
					ChatColor.DARK_GREEN + "Récompenses en attente");
			for (int i = 0; i < rewards.size(); i++){
				if (i >= crate_inv.getSize())
					break;
				Gain gain = rewards.get(i);

				ItemStack gain_view = new ItemStack(Material.getMaterial(gain.getItem()), 1);

				gain_view.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				ItemMeta im = gain_view.getItemMeta();
				im.setDisplayName(gain.getName().replaceAll("&", "§"));
				gain_view.setItemMeta(im);
				crate_inv.setItem(i, gain_view);
			}

			player.openInventory(crate_inv);
		}
		else{
			if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_GREEN + "Récompenses en attente"))
				player.closeInventory();
			player.sendMessage(Lang.getMessage(player, "reward.empty", "&cLANG ERROR: reward.empty", true));
		}
	}
	
	public static void openPreviewCrate(Player player, Crate crate) {

		Inventory crate_inv = Bukkit.createInventory(null,
				9 * (int)Math.ceil((double)crate.getGains().size() / 9.0D),
				ChatColor.DARK_GREEN + "Récompenses " + crate.getName());
		for (int i = 0; i < crate.getGains().size(); i++){

			Gain gain = crate.getGains().get(i);

			ItemStack gain_item = new ItemStack(Material.getMaterial(gain.getItem()), 1);
			gain_item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			ItemMeta im = gain_item.getItemMeta();
			im.setDisplayName(gain.getName().replaceAll("&", "§"));

			List<String> lore = new ArrayList<String>();

			if (im.hasLore())
				lore = im.getLore();
			lore.add("" + gain.getPourcentage() + "%");
			im.setLore(lore);

			gain_item.setItemMeta(im);
			crate_inv.setItem(i, gain_item);
		}
		player.openInventory(crate_inv);
	}

	public static void openCrate(Player player, Crate crate) {


		Inventory crate_inv = Bukkit.createInventory(null, 27,
				ChatColor.DARK_GREEN + crate.getName() + " Crate");

		ItemStack key = new ItemStack(Material.getMaterial(crate.getClef().getItem()), 1);
		if (crate.getClef().isEnchanted())
			key.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta im = key.getItemMeta();

		im.setDisplayName(crate.getClef().getName());
		key.setItemMeta(im);
		crate_inv.setItem(4, key);
		crate_inv.setItem(22, key);

		player.openInventory(crate_inv);
		animateCrate(player, crate, crate_inv);
	}
	
	private static void animateCrate(Player player, Crate crate, Inventory crate_inv)
	{
		Random random = new Random();
		ArrayList<ItemStack> glass_anim = new ArrayList<ItemStack>();

		for (int i = 0; i < 16; i++) {
			ItemStack itemstack = new ItemStack(Material.STAINED_GLASS_PANE, 1);
			if (i > 0)
				itemstack.setDurability((short) i);
			glass_anim.add(itemstack);

		}
		
		ArrayList<Gain> recompenses = new ArrayList<Gain>();
		for (Gain gain : crate.getGains()){

			for (int i = 0; i < gain.getPourcentage(); i++){

				recompenses.add(gain);
			}
		}

		ArrayList<Gain> liste_gains_start = new ArrayList<Gain>();

		int size_scroll = 7;

		for (int i = 0; i < size_scroll; i++){

			liste_gains_start.add(recompenses.get(random.nextInt(recompenses.size())));
		}

		ScrollGain scrollgain = new ScrollGain(size_scroll, liste_gains_start);

		new BukkitRunnable(){
			boolean scroll_enable = true;
			long count = 0;
			@Override
			public void run() {
				if (scroll_enable){

					scrollgain.scrollAndAddGain(recompenses.get(random.nextInt(recompenses.size())));

					for (int i = 0; i <= 26; i++){
						if (i > 9
								&& i < 17){

							Gain gain = scrollgain.getGains().get(i - 10);

							ItemStack item = new ItemStack(Material.getMaterial(gain.getItem()), 1);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(gain.getName().replace("&", "§"));

							item.setItemMeta(meta);
							crate_inv.setItem(i, item);
						}
						else if (i != 4 && i != 22)
							crate_inv.setItem(i, glass_anim.get(random.nextInt(glass_anim.size())));
						
					}
				}

				count++;
				if (count >= 30){

					if (scroll_enable){

						scroll_enable = false;
						Gain gain = scrollgain.getGains().get(3);
						
						if (player.isOnline()){

							int empty_slot = MCrates.getEmptySlots(player);
							
							if (empty_slot >= gain.getItems()){

								if (gain.getGainType() == Gain.GainType.COMMAND) {
									for (String command : gain.getCommands()){

										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
												command.replaceAll("%name%", player.getName()));
									}
								}
							}
							else{
								MCrates.addReward(player.getName(), gain);
								player.sendMessage(Lang.getMessage(player, "reward.win", "&cLANG ERROR: reward.win", true,
										ChatColor.translateAlternateColorCodes('&', gain.getName())));
							}
						}
						else{

							MCrates.addReward(player.getName(), gain);
						}
					}
					else if (count >= 37){

						if (crate_inv.getViewers() != null){

							ArrayList<HumanEntity> close_inventory = new ArrayList<HumanEntity>(crate_inv.getViewers());
							for (HumanEntity viewers : close_inventory){

								viewers.closeInventory();
							}
						}
						this.cancel();
					}
				}
			}
			
		}.runTaskTimer(MCrates.plugin, 0, 2);
	}

	public static void giveCrate(Player player, Crate crate)
	{
	    if (player == null || !player.isOnline() || crate == null)
	      return; 
	    ItemStack crateItem = new ItemStack(Material.getMaterial(crate.getBlock()), 1);

	    ItemMeta crateMeta = crateItem.getItemMeta();

	    crateMeta.setDisplayName(crate.getName() + " Crate!");
	    List<String> lore = new ArrayList<>();

	    lore.add(ChatColor.GRAY + "Posez votre box.");
	    lore.add("");

	    crateMeta.setLore(lore);
	    crateItem.setItemMeta(crateMeta);
	    player.getInventory().addItem(crateItem);

		player.sendMessage(Lang.getMessage(player, "box.send", "&cLANG ERROR: box.send", true,
				crate.getName()));
	}
}
