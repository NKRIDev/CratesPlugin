package fr.nkri.crates.events;

import java.util.List;

import fr.mrcubee.langlib.Lang;
import fr.nkri.crates.MCrates;
import fr.nkri.crates.manager.CrateManager;
import fr.nkri.crates.objects.Clef;
import fr.nkri.crates.objects.Crate;
import fr.nkri.crates.objects.Gain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EInventory implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (event.getView().getTitle().contains(" Crate")){
			for (Crate crate : MCrates.creates)
			{
				if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + crate.getName() + " Crate"))
				{
					event.setCancelled(true);
					break;
				}
			}
		}
		else if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Récompenses en attente")){
			event.setCancelled(true);
			if (event.getAction().equals(InventoryAction.PICKUP_ALL))
			{
				if (event.getCurrentItem() != null || event.getCurrentItem().getType() != Material.AIR)
				{
					ItemStack itemstack = event.getCurrentItem();
					List<Gain> gains = MCrates.getRewards(event.getWhoClicked().getName());
					for (Gain gain : gains)
					{
						if (gain.getItem().equals(itemstack.getType().name())
								&& itemstack.hasItemMeta()
								&& itemstack.getItemMeta().getDisplayName()
								.equals(gain.getName().replaceAll("&", "§")))
						{
							int empty_slot = 0;
							for (int i = 0; i < event.getWhoClicked().getInventory().getSize(); i++)
							    if (event.getWhoClicked().getInventory().getContents()[i] == null || event.getWhoClicked().getInventory().getContents()[i].getType().equals(Material.AIR))
							        empty_slot++;
							for (int i = 0; i < event.getWhoClicked().getInventory().getArmorContents().length; i++)
								if (event.getWhoClicked().getInventory().getArmorContents()[i] == null ||event.getWhoClicked().getInventory().getArmorContents()[i].getType().equals(Material.AIR))
									empty_slot--;
							if (event.getWhoClicked().getInventory().getItemInHand() == null || event.getWhoClicked().getInventory().getItemInHand().getType().equals(Material.AIR))
								empty_slot--;
							
							if (empty_slot >= gain.getItems())
							{
								if (gain.getGainType() == Gain.GainType.COMMAND)
								{
									for (String command : gain.getCommands())
									{
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%name%", event.getWhoClicked().getName()));
									}
								}
								MCrates.removeReward(event.getWhoClicked().getName(), gain);
								CrateManager.openRecompenses((Player)event.getWhoClicked());
								return;
							}
							else
								break;
						}
					}
					((CommandSender) event.getWhoClicked()).sendMessage(ChatColor.DARK_RED + "Vous n'avez pas assez de place dans votre inventaire !");
					event.getWhoClicked().closeInventory();
				}
			}
		}
		else if (event.getView().getTitle().contains(ChatColor.DARK_GREEN + "Récompenses "))
		{
			event.setCancelled(true);
		}
		else if (event.getView().getTitle().equals(ChatColor.GRAY + "Box")){
			event.setCancelled(true);

			if (event.getAction().equals(InventoryAction.PICKUP_ALL)){
				if (event.getCurrentItem() != null
						|| event.getCurrentItem().getType() != Material.AIR){

					ItemStack itemstack = event.getCurrentItem();

					Crate crate = MCrates.getCrate(itemstack.getItemMeta().getDisplayName());
					Clef clef = crate.getClef();

					ItemStack key = null;
					for(ItemStack item : event.getWhoClicked().getInventory().getContents()){
						if (item != null && item.getType() != Material.AIR
								&& clef.getItem().equals(item.getType().name())
								&& item.hasItemMeta()
								&& clef.getName().equals(item.getItemMeta().getDisplayName())){

							key = new ItemStack(item);
							if(item.getAmount()-1 <= 0) {
								event.getWhoClicked().getInventory().remove(item);
							}
							else {
								item.setAmount(item.getAmount()-1);
							}
						}
					}

					if(key != null){

						CrateManager.openCrate((Player) event.getWhoClicked(), crate);
					}
					else{
						final Player player = (Player) event.getWhoClicked();

						player.sendMessage(Lang.getMessage(player, "key.empty", "&cLANG ERROR: key.empty", true,
								crate.getName()));
						event.getWhoClicked().closeInventory();
					}

				}
			}
			else{
				if (event.getCurrentItem() != null
						|| event.getCurrentItem().getType() != Material.AIR){

					ItemStack itemstack = event.getCurrentItem();

					CrateManager.openPreviewCrate((Player) event.getWhoClicked(),
							MCrates.getCrate(itemstack.getItemMeta().getDisplayName()));
				}
			}

		}
	}
}
