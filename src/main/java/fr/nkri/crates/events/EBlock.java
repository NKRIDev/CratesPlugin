package fr.nkri.crates.events;

import fr.mrcubee.langlib.Lang;
import fr.nkri.crates.MCrates;
import fr.nkri.crates.manager.CrateManager;
import fr.nkri.crates.objects.Clef;
import fr.nkri.crates.objects.Crate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


public class EBlock implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
	    ItemStack item = event.getItemInHand();

	    if (item.hasItemMeta()
				&& item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().contains("Crate!")) {

	    	if (event.getPlayer().isOp()
					|| event.getPlayer().hasPermission("crates.admin")){

		      String crateType = item.getItemMeta().getDisplayName().replaceAll(" Crate!", "");
		      Crate crate = MCrates.getCrate(crateType);
		      if (crate != null){

			      event.getBlock().setMetadata("CratesPlus.Crate", new FixedMetadataValue(MCrates.plugin,
						  crate.getName()));
				  MCrates.addCrate(crate, event.getBlock().getLocation(), event.getBlock().getType().name());
		      }
	    	}
	    } 
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock == null
				|| clickedBlock.getType() == Material.AIR)
		      return; 
	    if (!clickedBlock.hasMetadata("CratesPlus.Crate"))
	      return; 
	    String crateType = (clickedBlock.getMetadata("CratesPlus.Crate").get(0)).asString();

	    Crate crate = MCrates.getCrate(crateType);

	    if (crate == null)
	      return;
	    Player player = event.getPlayer();
	    if (clickedBlock.getType().name().equals(crate.getBlock())) {
		    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){

				ItemStack item = player.getItemInHand();
			    Clef clef = crate.getClef();
			    if (item != null && item.getType() != Material.AIR
						&& clef.getItem().equals(item.getType().name())
						&& item.hasItemMeta()
						&& clef.getName().equals(item.getItemMeta().getDisplayName())){

			    	int empty_slot = MCrates.getEmptySlots(player);
			    	if (MCrates.countReward(player.getName()) < 54
							|| empty_slot > crate.getMaxItemGains()){

			    		if(item.getAmount()-1 <= 0) {
					    	player.setItemInHand(new ItemStack(Material.AIR));
			    		}
						else {
					    	item.setAmount(item.getAmount()-1);
			    		}
				    	CrateManager.openCrate(player, crate);

			    	}
			    	else{
						player.sendMessage(Lang.getMessage(player, "reward.limit", "&cLANG ERROR: reward.limit", true));
			    	}
			   	}
			   	else{
					player.sendMessage(Lang.getMessage(player, "key.empty", "&cLANG ERROR: key.empty", true,
							crate.getName()));
			    }
		    }
		    else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){

		    	if (player.isSneaking() && (player.isOp() || player.hasPermission("crates.admin"))){
					player.sendMessage(Lang.getMessage(player, "box.delete", "&cLANG ERROR: box.delete", true));
					MCrates.removeCrate(crate, clickedBlock.getLocation(), clickedBlock.getType().name());
			    	clickedBlock.setType(Material.AIR);
		    	}
		    	else{
		    		CrateManager.openPreviewCrate(player, crate);
		    	}
		    }
    		event.setCancelled(true);
	    }
	}

}
