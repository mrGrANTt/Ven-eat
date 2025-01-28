package org.mrg.venEat;

import org.bukkit.Material;
import org.mrg.venEat.custom.DBWorker;

import java.util.Objects;

public class Varebles {
    public static int counter;
    private static VenEat plg;
    private static DBWorker worker;
    private static Material furn_material;
    private static boolean save_mode;
    private static boolean use_mySQL;
    private static boolean use_mySQL_lite;
    private static String mysql_url;
    private static String mysql_user;
    private static String mysql_password;
    private static String mysql_lite_file;

    public Varebles(VenEat plg) {
        Varebles.plg = plg;
    }

    public static void lodeConfig() {
        furn_material = Material.getMaterial(Objects.requireNonNull(Varebles.getPlg().getConfig().getString("furn-material")));
        save_mode = plg.getConfig().getBoolean("save-mod");
        use_mySQL = plg.getConfig().getBoolean("mysql.use-mysql");
        mysql_user = plg.getConfig().getString("mysql.user");
        mysql_password = plg.getConfig().getString("mysql.password");
        mysql_url = "jdbc:mysql://" + plg.getConfig().getString("mysql.ip") + ":" + plg.getConfig().getString("mysql.port") + "/" + plg.getConfig().getString("mysql.db-name");
        use_mySQL_lite = plg.getConfig().getBoolean("mysql-lite.use-mysql-lite");
        mysql_lite_file = "jdbc:sqlite:plugins/ven-eat/" + plg.getConfig().getString("mysql-lite.file");
    }

    public static VenEat getPlg() {
        return plg;
    }
    public static void setPlg(VenEat plg) {
        Varebles.plg = plg;
    }

    public static DBWorker getWorker() {
        return worker;
    }
    public static void setWorker(DBWorker worker) {
        Varebles.worker = worker;
    }

    public static Material getFurnMaterial() {
        return furn_material;
    }

    public static boolean isSaveMod() {
        return save_mode;
    }

    public static void setSaveMod(boolean new_value) {
        save_mode = new_value;
    }

    public static boolean isUse_mySQL_lite() {
        return use_mySQL_lite;
    }

    public static boolean isUse_mySQL() {
        return use_mySQL;
    }

    public static String getMysql_password() {
        return mysql_password;
    }

    public static String getMysql_user() {
        return mysql_user;
    }

    public static String getMysql_url() {
        return mysql_url;
    }

    public static String getMysql_lite_file() {
        return mysql_lite_file;
    }
}
