package org.mrg.venEat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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

        // Рецепт Build Tool
        ShapedRecipe buildTool = new ShapedRecipe(new NamespacedKey(this, "build_tool"), BuildItem());
        buildTool.shape(" S ", " S ", " S ");
        buildTool.setIngredient('S', Material.STICK);
        getServer().addRecipe(buildTool);


        // Рецепт теста
        ShapedRecipe doughRecipe = new ShapedRecipe(new NamespacedKey(this, "dough"), new ItemStack(Material.BREAD, 1));
        doughRecipe.shape(" S ", " W ", " W ");
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
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().name().contains("_SIGN")) {
                if (checkBuildItem(event.getItem())) {
                    event.setCancelled(true);
                    check(event.getClickedBlock(), event.getPlayer());
                }
            }
        }
    }

    private ItemStack BuildItem() {
        ItemStack item = Material.STICK.asItemType().createItemStack();
        NamespacedKey key = new NamespacedKey(this, "furnace_build_tool");

        item.editMeta(meta -> {
            meta.setEnchantmentGlintOverride(true);
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            meta.displayName(Component.text("Furnace build tool"));
        });

        return  item;
    }

    private boolean checkBuildItem(ItemStack item) {
        if(item != null && item.getType() == Material.STICK) {
            NamespacedKey key = new NamespacedKey(this, "furnace_build_tool");
            AtomicBoolean has = new AtomicBoolean(false);

            item.editMeta(meta -> {
                if (meta.getPersistentDataContainer().has(key))
                    has.set(true);
            });
            return has.get();
        }
        return false;
    }

    private void check(Block block, Player plr) {
        Sign sign = (Sign) block.getState();
        if(checkSide(sign.getSide(Side.FRONT)) || checkSide(sign.getSide(Side.BACK))) {
            Block center = getCenter(block);
            if (center != null && isCustomFurnace(center)) {
                BuildFurnace(center);
                block.setType(Material.AIR);
                center.getWorld().playSound(center.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 10, 1);
                plr.sendMessage(Component.text("Печь установлена").color(TextColor.color(29, 147, 16)));
            }
        }
    }

    private void BuildFurnace(Block block) {
        MetadataValue value = new FixedMetadataValue(this, block.getLocation());
        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 2; y++)
                for (int z = -1; z < 2; z++)
                    furnacingBlock(block.getRelative(x,y,z), value);
    }

    private void furnacingBlock(Block block, MetadataValue value) {
        block.setType(Material.FURNACE);
        block.setMetadata("FurnaceCenter", value);
    }

    private Block getCenter(Block sign) {
        BlockFace[] faces = new BlockFace[6];
        faces[0] = BlockFace.NORTH;
        faces[1] = BlockFace.WEST;
        faces[2] = BlockFace.EAST;
        faces[3] = BlockFace.SOUTH;
        faces[4] = BlockFace.UP;
        faces[5] = BlockFace.DOWN;

        for (BlockFace face : faces) {
            if(sign.getRelative(face).getType() == Material.getMaterial(Objects.requireNonNull(getConfig().getString("furn-material")))) {
                return sign.getRelative(face, 2);
            }
        }
        return null;
    }

    private boolean checkSide(SignSide side) {
        for(Component line : side.lines()) {
            if(MiniMessage.miniMessage().serialize(line).toLowerCase().contains("[печь]")) {
                return true;
            }
        }
        return false;
    }

    private boolean isCustomFurnace(Block centerBlock) {
        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 2; y++)
                for (int z = -1; z < 2; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;
                    if (centerBlock.getRelative(x, y, z).getType() != Material.getMaterial(Objects.requireNonNull(getConfig().getString("furn-material")))) {
                        return false;
                    }
                }
        return true;
    }
}