package org.mrg.venEat;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class VenEat extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerRecipes();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerRecipes() {
        //
        //  TODO: Сделать то же но с подтягиванием данных из папки
        //


        // Рецепт теста
        ShapedRecipe doughRecipe = new ShapedRecipe(new NamespacedKey(this, "dough"), new ItemStack(Material.BREAD, 1));
        doughRecipe.shape(" S ", "W ", "W ");
        doughRecipe.setIngredient('S', Material.SUGAR);
        doughRecipe.setIngredient('W', Material.WHEAT);
        getServer().addRecipe(doughRecipe);

        // Рецепт яблочного пирога
        ShapedRecipe applePieRecipe = new ShapedRecipe(new NamespacedKey(this, "apple_pie"), new ItemStack(Material.APPLE, 1));
        applePieRecipe.shape("DTD", "T T", "DTD");
        applePieRecipe.setIngredient('D', Material.BREAD); // тесто
        applePieRecipe.setIngredient('T', Material.APPLE);
        getServer().addRecipe(applePieRecipe);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();

        //
        // TODO: сделать через позицию блока камня а не игрока
        //

        if (block.getType().name().contains("_SIGN")) {
            Sign sign = (Sign) block.getState();

            if(checkSide(sign.getSide(Side.FRONT)) || checkSide(sign.getSide(Side.BACK))) {
                Block below = block.getRelative(BlockFace.DOWN, 2);
                if (isCustomFurnace(below)) {
                    below.setType(Material.FURNACE);
                    below.getWorld().playSound(below.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1, 1);
                }
            }
        }
    }

    private boolean checkSide(SignSide side) {
        for(Component line : side.lines()) {
        /*
            TODO: Подумать может можно игнорировать регистр как-то
        */
            if(line.contains(Component.text("[печь]")) || line.contains(Component.text("[Печь]"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCustomFurnace(Block centerBlock) {
        if (centerBlock.getType() != Material.AIR) return false;
        /*
            TODO: Переделать.. Очень всратое решение
        */
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                for (int z = -1; z <= 1; z++) {
                    if(x == 0 && y == 0 && z == 0) continue;
                    if (centerBlock
                            .getRelative(BlockFace.UP, y)
                            .getRelative(BlockFace.NORTH, z)
                            .getRelative(BlockFace.EAST, x)
                            .getType() != Material.COBBLESTONE) return false;
                }
            }
        }
        return true;
    }
}