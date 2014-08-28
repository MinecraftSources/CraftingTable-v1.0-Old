package com.rmb938.mn2.docker.servlets.api;

import com.rmb938.mn2.docker.DatabaseResource;
import com.rmb938.mn2.docker.db.entity.MN2Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@WebServlet(
        name = "APIPluginsServlet",
        urlPatterns = {"/mn2/api/plugin/bungee", "/mn2/api/plugin/bukkit", "/mn2/api/plugin/all"})
public class PluginServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        JSONArray pluginsJSON = new JSONArray();
        ArrayList<MN2Plugin> plugins;
        if (req.getRequestURI().endsWith("bungee")) {
            plugins = DatabaseResource.getPluginLoader().loadPlugins(MN2Plugin.PluginType.BUNGEE);
        } else if (req.getRequestURI().endsWith("bukkit")) {
            plugins = DatabaseResource.getPluginLoader().loadPlugins(MN2Plugin.PluginType.BUKKIT);
        } else if (req.getRequestURI().endsWith("all")) {
            plugins = DatabaseResource.getPluginLoader().loadPlugins();
        } else {
            //nothing else can get things
            return jsonObject;
        }
        for (MN2Plugin plugin : plugins) {
            JSONObject pluginJSON = new JSONObject();

            pluginJSON.put("_id", plugin.get_id().toString());
            pluginJSON.put("name", plugin.getName());
            pluginJSON.put("type", plugin.getType());
            pluginJSON.put("baseFolder", plugin.getBaseFolder());
            pluginJSON.put("configFolder", plugin.getConfigFolder());

            JSONArray configs = new JSONArray();
            for (MN2Plugin.PluginConfig pluginConfig : plugin.getConfigs().values()) {
                JSONObject configJSON = new JSONObject();

                configJSON.put("_id", pluginConfig.get_id().toString());
                configJSON.put("name", pluginConfig.getName());
                configJSON.put("location", pluginConfig.getLocation());

                configs.put(configJSON);
            }

            pluginJSON.put("configs", configs);

            pluginsJSON.put(pluginJSON);
        }

        jsonObject.put("plugins", pluginsJSON);
        return jsonObject;
    }
}
