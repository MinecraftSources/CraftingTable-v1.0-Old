package com.rmb938.mn2.docker.servlets.api;

import com.rmb938.mn2.docker.DatabaseResource;
import com.rmb938.mn2.docker.db.entity.MN2Plugin;
import com.rmb938.mn2.docker.db.entity.MN2ServerType;
import com.rmb938.mn2.docker.db.entity.MN2World;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "APIServerTypeServlet",
        urlPatterns = {"/mn2/api/servertype/all", "/mn2/api/servertype/one", "/mn2/api/servertype/edit", "/mn2/api/servertype/add"})
public class ServerTypeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        if (req.getRequestURI().endsWith("all")) {
            JSONArray serverTypes = new JSONArray();
            for (MN2ServerType serverType : DatabaseResource.getServerTypeLoader().getTypes()) {
                JSONObject serverTypeJSON = new JSONObject();
                serverTypeJSON.put("_id", serverType.get_id().toString());
                serverTypeJSON.put("name", serverType.getName());
                serverTypeJSON.put("players", serverType.getPlayers());
                serverTypeJSON.put("memory", serverType.getMemory());
                serverTypeJSON.put("amount", serverType.getAmount());

                JSONArray plugins = new JSONArray();
                for (MN2Plugin plugin : serverType.getPlugins().keySet()) {
                    MN2Plugin.PluginConfig pluginConfig = serverType.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                serverTypeJSON.put("plugins", plugins);

                JSONArray worlds = new JSONArray();
                for (MN2World world : serverType.getWorlds()) {
                    JSONObject worldJSON = new JSONObject();
                    worldJSON.put("_id", world.get_id().toString());
                    if (world == serverType.getDefaultWorld()) {
                        worldJSON.put("isDefault", true);
                    } else {
                        worldJSON.put("isDefault", false);
                    }

                    worlds.put(worldJSON);
                }
                serverTypeJSON.put("worlds", worlds);

                serverTypes.put(serverTypeJSON);
            }
            jsonObject.put("serverTypes", serverTypes);
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Server Type Method Not allowed");
            return jsonObject;
        }

        return jsonObject;
    }
}
