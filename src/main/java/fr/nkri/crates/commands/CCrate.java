package fr.nkri.crates.commands;

import fr.mrcubee.langlib.Lang;
import fr.nkri.crates.MCrates;
import fr.nkri.crates.manager.CrateManager;
import fr.nkri.crates.objects.Clef;
import fr.nkri.crates.objects.Crate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static fr.nkri.crates.MCrates.creates;

public class CCrate implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		if (args.length > 0)
		{
			if (sender instanceof Player){
				final Player playerSender = (Player) sender;

				Player player = (Player)sender;
				if (args[0].equalsIgnoreCase("recup")){

					if (MCrates.hasReward(player.getName())){

						CrateManager.openRecompenses(player);
					}
					else
						player.sendMessage(Lang.getMessage(playerSender, "reward.empty", "&cLANG ERROR: reward.empty", true));
					return true;
				} else if(args[0].equalsIgnoreCase("open")){

					Inventory inventoryCrate = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Box");


					for(Crate crate : creates){
						ItemStack crateItem = new ItemStack(Material.getMaterial(crate.getBlock()), 1);
						ItemMeta meta = crateItem.getItemMeta();
						meta.setDisplayName(crate.getName());
						crateItem.setItemMeta(meta);

						inventoryCrate.addItem(crateItem);
					}

					player.openInventory(inventoryCrate);

				}
			}
		}
		if (!(sender.isOp() || sender.hasPermission("crates.admin")))
			return true;
		if (args.length > 0){

			if (args[0].equalsIgnoreCase("key")){

				if (args.length >= 3){

					if (MCrates.getCrate(args[2]) != null){

						int nombre = 1;
						if (args.length > 3){

							try {
					              nombre = Integer.parseInt(args[3]);
					        } 
							catch (Exception e) {
								  sender.sendMessage(Lang.getMessage("command.invalide.number", "&cLANG ERROR: command.invalide.number", true));
					              sender.sendMessage(Lang.getMessage("command.usage", "&cLANG ERROR: command.usage", true));
					              return true;
					        } 
						}

						Clef clef = Objects.requireNonNull(MCrates.getCrate(args[2])).getClef();

						ItemStack key = new ItemStack(Material.getMaterial(clef.getItem()), nombre);

						if (clef.isEnchanted())
							key.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						ItemMeta im = key.getItemMeta();

						im.setDisplayName(clef.getName());
						key.setItemMeta(im);
						if (args[1].equalsIgnoreCase("all")){

							for (Player player : Bukkit.getOnlinePlayers()){

								player.getInventory().addItem(key);
							}
							sender.sendMessage(Lang.getMessage("command.self.give.all", "&cLANG ERROR", true,
									nombre, clef.getName()));
						    return true;
						}
						Player player = Bukkit.getPlayerExact(args[1]);
						if (player != null){

							if (player.isOnline()){

								player.getInventory().addItem(key);
								sender.sendMessage(Lang.getMessage("command.self.give.player", "&cLANG ERROR: command.self.give.player", true,
										nombre, clef.getName(), player.getName()));
							    return true;
							}
						}
						sender.sendMessage(Lang.getMessage("command.invalide.player", "&cLANG ERROR: command.invalide.player", true));
					    return true;
					}
					sender.sendMessage(Lang.getMessage("box.invalide", "&cLANG ERROR: box.invalide", true));
				}
				sender.sendMessage(Lang.getMessage("command.usage", "&cLANG ERROR: command.usage", true));
				return true;
			}
			if (args[0].equalsIgnoreCase("add")){

				if (sender instanceof Player){

					if (args.length == 2){

						if (MCrates.getCrate(args[1]) != null){
							CrateManager.giveCrate((Player)sender, MCrates.getCrate(args[1]));
							return true;
						}
						sender.sendMessage(Lang.getMessage("box.invalide", "&cLANG ERROR: box.invalide", true));
					}
					sender.sendMessage(Lang.getMessage("box.invalide", "&cLANG ERROR: box.invalide", true));
				}
			}
			return true;
		}
		return false;
	}
	
	
}
