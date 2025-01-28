package org.mrg.venEat.custom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.mrg.venEat.Varebles;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class FurnaceMethods {
    public static boolean checkBuildItem(ItemStack item) {
        if(item != null && item.getType() == Material.STICK) {
            NamespacedKey key = new NamespacedKey(Varebles.getPlg(), "furnace_build_tool");
            AtomicBoolean has = new AtomicBoolean(false);

            item.editMeta(meta -> {
                if (meta.getPersistentDataContainer().has(key))
                    has.set(true);
            });
            return has.get();
        }
        return false;
    }

    public static Location getFurnLocation(Block blc) {
        if (blc.getType() == Material.FURNACE) {
            if(blc.hasMetadata("FurnaceCenter")) {
                return (Location) blc.getMetadata("FurnaceCenter").getFirst().value();
            }
        }
        return null;
    }

    public static void protectedFurnaceBuilder(Block block, Player plr) {
        Sign sign = (Sign) block.getState();
        if(checkSide(sign.getSide(Side.FRONT)) || checkSide(sign.getSide(Side.BACK))) {
            Block center = getCenter(block);
            if (center != null && isCustomFurnace(center)) {
                BuildFurnace(center);
                block.setType(Material.AIR);
                plr.sendMessage(Component.text("Печь установлена").color(TextColor.color(29, 147, 16)));
            }
        }
    }

    public static void lodeMetadata() {
        String sel = "SELECT x,y,z,world FROM centers";

        if (!Varebles.isSaveMod()) {
            Varebles.counter = 0;
            Varebles.getWorker().query(sel, resultSet -> {
                int err = 0;
                try {
                    String world = resultSet.getString("world");
                    err = 1;
                    int x = resultSet.getInt("x");
                    err = 2;
                    int y = resultSet.getInt("y");
                    err = 3;
                    int z = resultSet.getInt("z");
                    err = 4;

                    Block blc = Objects.requireNonNull(Varebles.getPlg().getServer().getWorld(world)).getBlockAt(x, y, z);
                    MetadataValue value = new FixedMetadataValue(Varebles.getPlg(), blc.getLocation());

                    for (int X = -1; X < 2; X++)
                        for (int Y = -1; Y < 2; Y++)
                            for (int Z = -1; Z < 2; Z++)
                                doBlockFurnace(blc.getRelative(X, Y, Z), value);
                    Varebles.counter++;
                } catch (SQLException ex) {
                    String error_msg = switch (err) {
                        case 0 -> "World is wrongly!!!";
                        case 1 -> "X is wrongly!!!";
                        case 2 -> "Y is wrongly!!!";
                        case 3 -> "Z is wrongly!!!";
                        default -> "";
                    };
                    Varebles.getPlg().getLogger().warning("Can't lode one of furnace... " + error_msg + "\n\n" + ex.getMessage());
                }
            });
            Varebles.getPlg().getLogger().info("Loaded " + Varebles.counter + " furnace.");

        }
    }

    private static boolean checkSide(SignSide side) {
        for(Component line : side.lines()) {
            if(MiniMessage.miniMessage().serialize(line).toLowerCase().contains("[печь]")) {
                return true;
            }
        }
        return false;
    }

    private static Block getCenter(Block sign) {
        BlockFace[] faces = new BlockFace[6];
        faces[0] = BlockFace.NORTH;
        faces[1] = BlockFace.WEST;
        faces[2] = BlockFace.EAST;
        faces[3] = BlockFace.SOUTH;
        faces[4] = BlockFace.UP;
        faces[5] = BlockFace.DOWN;

        for (BlockFace face : faces) {
            if(sign.getRelative(face).getType() == Varebles.getFurnMaterial()) {
                return sign.getRelative(face, 2);
            }
        }
        return null;
    }

    private static boolean isCustomFurnace(Block centerBlock) {
        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 2; y++)
                for (int z = -1; z < 2; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;
                    if (centerBlock.getRelative(x, y, z).getType() != Varebles.getFurnMaterial()) {
                        return false;
                    }
                }
        return true;
    }

    public static void BuildFurnace(Block block) {
        MetadataValue value = new FixedMetadataValue(Varebles.getPlg(), block.getLocation());
        String ins = "INSERT INTO centers(x,y,z, world) VALUES (%d,%d,%d,\"%s\");"
                .formatted(block.getLocation().getBlockX(),
                        block.getLocation().getBlockY(),
                        block.getLocation().getBlockZ(),
                        block.getLocation().getWorld().getName());

        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 2; y++)
                for (int z = -1; z < 2; z++)
                    doBlockFurnace(block.getRelative(x,y,z), value);

        if (!Varebles.isSaveMod())
            Varebles.getWorker().query(ins);
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 10, 1);
    }

    public static void DestroyFurnace(Block block) {
        String del = "DELETE FROM centers WHERE x=%d AND y=%d AND z=%d AND world=\"%s\";"
                .formatted(block.getLocation().getBlockX(),
                        block.getLocation().getBlockY(),
                        block.getLocation().getBlockZ(),
                        block.getLocation().getWorld().getName());

        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 2; y++)
                for (int z = -1; z < 2; z++)
                    block.getRelative(x,y,z).setType(Material.AIR);

        block.getWorld().dropItem(block.getLocation(), new ItemStack(Varebles.getFurnMaterial(), 26));

        if (!Varebles.isSaveMod())
            Varebles.getWorker().query(del);
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 10, 1);
    }

    private static void doBlockFurnace(Block block, MetadataValue value) {
        block.setType(Material.FURNACE);
        block.setMetadata("FurnaceCenter", value);
    }

    public static void updateFurnaceFlaming(Block block, boolean lit) {
        Location loc = getFurnLocation(block);
        if (loc != null) {
            Block center = loc.getBlock();
            for (int x = -1; x < 2; x++)
                for (int y = -1; y < 2; y++)
                    for (int z = -1; z < 2; z++) {
                        Block blc = center.getRelative(x, y, z);
                        Furnace furn = (Furnace) blc.getState().getBlockData();
                        furn.setLit(lit);
                        blc.setBlockData(furn);
                    }
        }
    }
}
