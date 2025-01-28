package org.mrg.venEat.custom;

import org.mrg.venEat.Varebles;

import java.sql.*;
import java.util.function.Consumer;

public class DBWorker {
    private Connection con;

    public static DBWorker build() {
        if (!Varebles.isSaveMod()) {
            if (Varebles.isUse_mySQL()) {
                return new DBWorker(Varebles.getMysql_url(), Varebles.getMysql_user(), Varebles.getMysql_password());
            } else if (Varebles.isUse_mySQL_lite()) {
                return new DBWorker(Varebles.getMysql_lite_file());
            } else {
                Varebles.getPlg().getConfig().set("save-mode", true);
                Varebles.lodeConfig();
            }
        }
        Varebles.getPlg().getLogger().warning("Save mode is enable!");
        return null;
    }

    private DBWorker(String url, String user, String password) {
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            Varebles.getPlg().getLogger().warning("Can't connection DB... Plugin will work in save mode!");
            Varebles.getPlg().getLogger().warning("Connect info: " + url + "; user: " + user + "password:" + password);
            Varebles.setSaveMod(true);
            con = null;
        }
    }

    private DBWorker(String file) {
        try {
            con = DriverManager.getConnection(file);
        } catch (SQLException e) {
            Varebles.getPlg().getLogger().warning("Can't connection DB... Plugin will work in save mode!");
            Varebles.getPlg().getLogger().warning("Connect info: " + file);
            Varebles.setSaveMod(true);
            con = null;
        }
    }

    public void Disable() {
        if(con != null) {
            try {
                con.close();
                con = null;
            } catch (SQLException e) {
                Varebles.getPlg().getLogger().warning("Can't close DB connection... It's wrongly!");
            }
        }
    }

    public void query(String qu, Consumer<ResultSet> fn) {
        if(Varebles.isSaveMod()) {return;}

        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(qu)) {

            while (resultSet.next()) {
                fn.accept(resultSet);
            }
        } catch (Exception e) {
            Varebles.getPlg().getLogger().warning("Can't send query from BD!\n\nQuery: " + qu + "\n\n"+ e.getMessage());
        }
    }

    public void query(String qu) {
        if(Varebles.isSaveMod()) {return;}
        try (Statement statement = con.createStatement()) {
            statement.execute(qu);
        } catch (Exception e) {
            Varebles.getPlg().getLogger().warning("Can't send query from BD!\n\nQuery: " + qu + "\n\n"+ e.getMessage());
        }
    }
}