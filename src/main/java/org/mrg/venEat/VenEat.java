package org.mrg.venEat;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.mrg.venEat.custom.DBWorker;
import org.mrg.venEat.custom.FurnaceMethods;
import org.mrg.venEat.event.EventHandlerClass;


public class VenEat extends JavaPlugin{

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Varebles.setPlg(this);
        Varebles.lodeConfig();
        Varebles.setWorker(DBWorker.build());

        genSQLTables();
        FurnaceMethods.lodeMetadata();

        registerRecipes();

        getServer().getPluginManager().registerEvents(new EventHandlerClass(), this);
    }

    @Override
    public void onDisable() {
        if (!Varebles.isSaveMod())
            Varebles.getWorker().Disable();
        super.onDisable();
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
    }

    private ItemStack BuildItem() {
        ItemStack item = new ItemStack(Material.STICK);
        NamespacedKey key = new NamespacedKey(this, "furnace_build_tool");

        item.editMeta(meta -> {
            meta.setEnchantmentGlintOverride(true);
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            meta.displayName(Component.text("Furnace build tool"));
        });

        return item;
    }

    private void genSQLTables() {
        if (!Varebles.isSaveMod())
            Varebles.getWorker().query("""
                CREATE TABLE IF NOT EXISTS centers(
                    x int NOT NULL,
                    y int NOT NULL,
                    z int NOT NULL,
                    world varchar(64) NOT NULL
                )
                """);
    }
}
