package com.rmb938.mn2.docker.servlets.api;

import com.rmb938.mn2.docker.DatabaseResource;
import com.rmb938.mn2.docker.db.entity.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(
        name = "APIBungeeTypeServlet",
        urlPatterns = {"/mn2/api/bungeetype/all", "/mn2/api/bungeetype/one", "/mn2/api/bungeetype/save", "/mn2/api/bungeetype/add", "/mn2/api/bungeetype/delete"})
@Log4j2
public class BungeeTypeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");

            try {
                MN2BungeeType bungeeType = DatabaseResource.getBungeeTypeLoader().loadEntity(new ObjectId(id));

                if (bungeeType == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Bungee Type " + id);
                    return jsonObject;
                }

                jsonObject.put("_id", bungeeType.get_id().toString());
                jsonObject.put("name", bungeeType.getName());

                JSONArray serverTypes = new JSONArray();
                for (MN2ServerType serverType : bungeeType.getServerTypes().keySet()) {
                    boolean allowRejoin = bungeeType.getServerTypes().get(serverType);
                    JSONObject serverTypeJSON = new JSONObject();
                    serverTypeJSON.put("_id", serverType.get_id());
                    serverTypeJSON.put("allowRejoin", allowRejoin);
                    if (serverType == bungeeType.getDefaultType()) {
                        serverTypeJSON.put("isDefault", true);
                    } else {
                        serverTypeJSON.put("isDefault", false);
                    }
                    serverTypes.put(serverTypeJSON);
                }
                jsonObject.put("serverTypes", serverTypes);

                JSONArray plugins = new JSONArray();
                for (MN2Plugin plugin : bungeeType.getPlugins().keySet()) {
                    MN2Plugin.PluginConfig pluginConfig = bungeeType.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                jsonObject.put("plugins", plugins);
                return jsonObject;
            } catch (Exception ex) {
                resp.setStatus(400);
                jsonObject.put("error", "Invalid Bungee ID " + id);
                return jsonObject;
            }
        } else if (req.getRequestURI().endsWith("all")) {
            JSONArray bungeeTypes = new JSONArray();

            for (MN2BungeeType bungeeType : DatabaseResource.getBungeeTypeLoader().getTypes()) {
                JSONObject bungeeTypeJSON = new JSONObject();

                bungeeTypeJSON.put("_id", bungeeType.get_id().toString());
                bungeeTypeJSON.put("name", bungeeType.getName());

                JSONArray serverTypes = new JSONArray();
                for (MN2ServerType serverType : bungeeType.getServerTypes().keySet()) {
                    boolean allowRejoin = bungeeType.getServerTypes().get(serverType);
                    JSONObject serverTypeJSON = new JSONObject();
                    serverTypeJSON.put("_id", serverType.get_id());
                    serverTypeJSON.put("allowRejoin", allowRejoin);
                    if (serverType == bungeeType.getDefaultType()) {
                        serverTypeJSON.put("isDefault", true);
                    } else {
                        serverTypeJSON.put("isDefault", false);
                    }
                    serverTypes.put(serverTypeJSON);
                }
                bungeeTypeJSON.put("serverTypes", serverTypes);

                JSONArray plugins = new JSONArray();
                for (MN2Plugin plugin : bungeeType.getPlugins().keySet()) {
                    MN2Plugin.PluginConfig pluginConfig = bungeeType.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                bungeeTypeJSON.put("plugins", plugins);

                bungeeTypes.put(bungeeTypeJSON);
            }

            jsonObject.put("bungeeTypes", bungeeTypes);
            return jsonObject;
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Bungee Type Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject postJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.postJSON(req, resp);

        if (req.getRequestURI().endsWith("add")) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                String json = reader.readLine();
                if (json == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "You must send something");
                    return jsonObject;
                }
                JSONObject bungeeTypeJSON = new JSONObject(json);

                MN2BungeeType bungeeType = new MN2BungeeType();
                bungeeType.setName(bungeeTypeJSON.getString("name"));

                JSONArray serverTypes = bungeeTypeJSON.getJSONArray("serverTypes");
                for (int i = 0; i < serverTypes.length(); i++) {
                    JSONObject object = serverTypes.getJSONObject(i);
                    MN2ServerType serverType = DatabaseResource.getServerTypeLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (serverType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Server Type "+object.getString("_id"));
                        return jsonObject;
                    }
                    bungeeType.getServerTypes().put(serverType, object.getBoolean("allowRejoin"));
                    if (object.getBoolean("isDefault")) {
                        bungeeType.setDefaultType(serverType);
                    }
                }

                JSONArray plugins = bungeeTypeJSON.getJSONArray("plugins");
                for (int i = 0; i < plugins.length(); i++) {
                    JSONObject object = plugins.getJSONObject(i);
                    MN2Plugin plugin = DatabaseResource.getPluginLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (plugin == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Plugin Type "+object.getString("_id"));
                        return jsonObject;
                    }
                    MN2Plugin.PluginConfig pluginConfig = null;
                    if (object.has("_configId")) {
                        pluginConfig = plugin.getConfigs().get(new ObjectId("_id"));
                        if (pluginConfig == null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Unknown Plugin Config "+object.getString("_id")+" for plugin "+plugin.getName());
                            return jsonObject;
                        }
                    }
                    if (pluginConfig == null && plugin.getConfigs().size() > 0) {
                        resp.setStatus(400);
                        jsonObject.put("error", "There must be a config for "+plugin.getName());
                        return jsonObject;
                    }
                    bungeeType.getPlugins().put(plugin, pluginConfig);
                }

                if (bungeeType.getDefaultType() == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "No default server type");
                    return jsonObject;
                }
                DatabaseResource.getBungeeTypeLoader().insertEntity(bungeeType);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing POST request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Bungee Type Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject putJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        if (req.getRequestURI().endsWith("save")) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                String json = reader.readLine();
                if (json == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "You must send something");
                    return jsonObject;
                }
                JSONObject bungeeTypeJSON = new JSONObject(json);

                MN2BungeeType bungeeType = new MN2BungeeType();
                bungeeType.set_id(new ObjectId(bungeeTypeJSON.getString("_id")));

                if (DatabaseResource.getBungeeTypeLoader().loadEntity(bungeeType.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown bungee type "+bungeeType.get_id());
                    return jsonObject;
                }

                bungeeType.setName(bungeeTypeJSON.getString("name"));

                JSONArray serverTypes = bungeeTypeJSON.getJSONArray("serverTypes");
                for (int i = 0; i < serverTypes.length(); i++) {
                    JSONObject object = serverTypes.getJSONObject(i);
                    MN2ServerType serverType = DatabaseResource.getServerTypeLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (serverType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Server Type "+object.getString("_id"));
                        return jsonObject;
                    }
                    bungeeType.getServerTypes().put(serverType, object.getBoolean("allowRejoin"));
                    if (object.getBoolean("isDefault")) {
                        if (bungeeType.getDefaultType() != null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Bungee Type already has a default server type "+bungeeType.getDefaultType().getName());
                            return jsonObject;
                        }
                        bungeeType.setDefaultType(serverType);
                    }
                }

                JSONArray plugins = bungeeTypeJSON.getJSONArray("plugins");
                for (int i = 0; i < plugins.length(); i++) {
                    JSONObject object = plugins.getJSONObject(i);
                    MN2Plugin plugin = DatabaseResource.getPluginLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (plugin == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Plugin Type "+object.getString("_id"));
                        return jsonObject;
                    }
                    MN2Plugin.PluginConfig pluginConfig = null;
                    if (object.has("_configId")) {
                        pluginConfig = plugin.getConfigs().get(new ObjectId("_id"));
                        if (pluginConfig == null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Unknown Plugin Config "+object.getString("_id")+" for plugin "+plugin.getName());
                            return jsonObject;
                        }
                    }
                    if (pluginConfig == null && plugin.getConfigs().size() > 0) {
                        resp.setStatus(400);
                        jsonObject.put("error", "There must be a config for "+plugin.getName());
                        return jsonObject;
                    }
                    bungeeType.getPlugins().put(plugin, pluginConfig);
                }

                if (bungeeType.getDefaultType() == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "No default server type");
                    return jsonObject;
                }
                DatabaseResource.getBungeeTypeLoader().saveEntity(bungeeType);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Bungee Type Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject deleteJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        if (req.getRequestURI().endsWith("delete")) {
            String id = req.getParameter("id");
            try {
                MN2BungeeType bungeeType = DatabaseResource.getBungeeTypeLoader().loadEntity(new ObjectId(id));
                if (bungeeType == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Bungee Type "+id);
                    return jsonObject;
                }

                for (MN2Node node : DatabaseResource.getNodeLoader().getNodes()) {
                    if (node.getBungeeType().get_id().equals(bungeeType.get_id())) {
                        resp.setStatus(406);
                        jsonObject.put("error", "Cannot delete bungee type. Please remove from node "+node.getAddress());
                        return jsonObject;
                    }
                }

                DatabaseResource.getBungeeTypeLoader().removeEntity(bungeeType);
                return jsonObject;
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing DELETE request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Bungee Type Method Not allowed");
            return jsonObject;
        }
    }
}
