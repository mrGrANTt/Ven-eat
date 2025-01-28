package org.mrg.venEat.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.mrg.venEat.Varebles;
import org.mrg.venEat.custom.FurnaceMethods;


public class EventHandlerClass implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().name().contains("_SIGN")) {
                if (FurnaceMethods.checkBuildItem(event.getItem())) {
                    FurnaceMethods.protectedFurnaceBuilder(event.getClickedBlock(), event.getPlayer());
                    event.setCancelled(true);
                }
            } else if(event.getAction().isRightClick()) {
                Location loc = FurnaceMethods.getFurnLocation(event.getClickedBlock());
                if(loc != null) {
                    Furnace furn = (Furnace) loc.getBlock().getState();
                    Inventory inv = furn.getInventory();
                    event.getPlayer().openInventory(inv);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent ev) {
        Location loc = FurnaceMethods.getFurnLocation(ev.getBlock());
        if(loc != null) {
            FurnaceMethods.DestroyFurnace(loc.getBlock());
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent ev) {
        for(Block blc : ev.blockList()) {
            Location loc = FurnaceMethods.getFurnLocation(blc);
            if(loc != null) {
                FurnaceMethods.DestroyFurnace(loc.getBlock());
            }
        }
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent ev) {
        FurnaceMethods.updateFurnaceFlaming(ev.getBlock(), true);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(ev.getBlock().getType() == Material.FURNACE) {
                    org.bukkit.block.data.type.Furnace furn = (org.bukkit.block.data.type.Furnace) ev.getBlock().getState().getBlockData();
                    if (!furn.isLit()) {
                        FurnaceMethods.updateFurnaceFlaming(ev.getBlock(), false);
                    }
                }
            }
        }.runTaskLater(Varebles.getPlg(), ev.getBurnTime()+1);
    }
}
