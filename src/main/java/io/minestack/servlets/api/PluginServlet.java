package io.minestack.servlets.api;

import io.minestack.db.DoubleChest;
import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.proxy.DCProxyType;
import io.minestack.db.entity.proxy.DCProxyTypeDriver;
import io.minestack.db.entity.server.DCServerType;
import io.minestack.db.entity.server.DCServerTypeDriver;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

@WebServlet(
        name = "APIPluginServlet",
        urlPatterns = {"/api/plugin/bungee", "/api/plugin/bukkit", "/api/plugin/all", "/api/plugin/one", "/api/plugin/save", "/api/plugin/add", "/api/plugin/delete"})
public class PluginServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        JSONArray pluginsJSON = new JSONArray();
        ArrayList<DCPlugin> plugins;
        if (req.getRequestURI().endsWith("bungee")) {
            plugins = DoubleChest.getPluginLoader().loadPlugins(DCPlugin.PluginType.BUNGEE);
        } else if (req.getRequestURI().endsWith("bukkit")) {
            plugins = DoubleChest.getPluginLoader().loadPlugins(DCPlugin.PluginType.BUKKIT);
        } else if (req.getRequestURI().endsWith("all")) {
            plugins = DoubleChest.getPluginLoader().loadPlugins();
        } else if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");

            try {

                DCPlugin plugin = DoubleChest.getPluginLoader().loadEntity(new ObjectId(id));

                if (plugin == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Plugin "+id);
                    return jsonObject;
                }

                jsonObject.put("_id", plugin.get_id().toString());
                jsonObject.put("name", plugin.getName());
                jsonObject.put("type", plugin.getType());
                jsonObject.put("baseFolder", plugin.getBaseFolder());
                jsonObject.put("configFolder", plugin.getConfigFolder());

                JSONArray configs = new JSONArray();
                for (DCPlugin.PluginConfig pluginConfig : plugin.getConfigs().values()) {
                    JSONObject configJSON = new JSONObject();

                    configJSON.put("_id", pluginConfig.get_id().toString());
                    configJSON.put("name", pluginConfig.getName());
                    configJSON.put("location", pluginConfig.getLocation());

                    configs.put(configJSON);
                }

                jsonObject.put("configs", configs);

                return jsonObject;
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Plugin "+id);
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Plugin Method Not allowed");
            return jsonObject;
        }
        for (DCPlugin plugin : plugins) {
            JSONObject pluginJSON = new JSONObject();

            pluginJSON.put("_id", plugin.get_id().toString());
            pluginJSON.put("name", plugin.getName());
            pluginJSON.put("type", plugin.getType());
            pluginJSON.put("baseFolder", plugin.getBaseFolder());
            pluginJSON.put("configFolder", plugin.getConfigFolder());

            JSONArray configs = new JSONArray();
            for (DCPlugin.PluginConfig pluginConfig : plugin.getConfigs().values()) {
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
                JSONObject pluginJSON = new JSONObject(json);

                DCPlugin plugin = new DCPlugin();
                plugin.set_id(new ObjectId(pluginJSON.getString("_id")));

                if (DoubleChest.getPluginLoader().loadEntity(plugin.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown plugin "+plugin.get_id());
                    return jsonObject;
                }

                plugin.setName(pluginJSON.getString("name"));
                plugin.setType(DCPlugin.PluginType.valueOf(pluginJSON.getString("type")));
                plugin.setBaseFolder(pluginJSON.getString("baseFolder"));
                plugin.setConfigFolder(pluginJSON.getString("configFolder"));

                JSONArray configs = pluginJSON.getJSONArray("configs");
                for (int i = 0; i < configs.length(); i++) {
                    JSONObject object = configs.getJSONObject(i);
                    DCPlugin.PluginConfig pluginConfig = new DCPlugin.PluginConfig();
                    if (object.has("_id")) {
                        pluginConfig.set_id(new ObjectId(object.getString("_id")));
                    } else {
                        pluginConfig.set_id(new ObjectId());
                    }
                    pluginConfig.setName(object.getString("name"));
                    pluginConfig.setLocation(object.getString("location"));

                    plugin.getConfigs().put(pluginConfig.get_id(), pluginConfig);
                }

                for (DCServerType serverType : DoubleChest.getServerTypeLoader().getTypes()) {
                    DCServerTypeDriver serverTypeDriver = (DCServerTypeDriver) serverType.getDriver();
                    for (DCPlugin plugin1 : serverTypeDriver.getPlugins().keySet()) {
                        if (plugin.get_id().equals(plugin1.get_id())) {
                            DCPlugin.PluginConfig pluginConfig = serverTypeDriver.getPlugins().get(plugin1);
                            if (pluginConfig == null && plugin.getConfigs().isEmpty() == false) {
                                resp.setStatus(406);
                                jsonObject.put("error", "Cannot save plugin. Please remove from server type "+serverType.getName()+" before adding configs");
                                return jsonObject;
                            }
                            if (pluginConfig != null) {
                                boolean hasConfig = false;
                                for (ObjectId configId : plugin.getConfigs().keySet()) {
                                    if (configId.equals(pluginConfig.get_id())) {
                                        hasConfig = true;
                                    }
                                }
                                if (hasConfig == false) {
                                    resp.setStatus(406);
                                    jsonObject.put("error", "Cannot save plugin. Please remove from server type " + serverType.getName() + " before changing configs");
                                    return jsonObject;
                                }
                            }
                            break;
                        }
                    }
                }

                for (DCProxyType proxyType : DoubleChest.getProxyTypeLoader().getTypes()) {
                    DCProxyTypeDriver proxyTypeDriver = (DCProxyTypeDriver) proxyType.getDriver();
                    for (DCPlugin plugin1 : proxyTypeDriver.getPlugins().keySet()) {
                        if (plugin.get_id().equals(plugin1.get_id())) {
                            DCPlugin.PluginConfig pluginConfig = proxyTypeDriver.getPlugins().get(plugin1);
                            if (pluginConfig == null && plugin.getConfigs().isEmpty() == false) {
                                resp.setStatus(406);
                                jsonObject.put("error", "Cannot save plugin. Please remove from bungee type "+proxyType.getName()+" before adding configs");
                                return jsonObject;
                            }
                            if (pluginConfig != null) {
                                boolean hasConfig = false;
                                for (ObjectId configId : plugin.getConfigs().keySet()) {
                                    if (configId.equals(pluginConfig.get_id())) {
                                        hasConfig = true;
                                    }
                                }
                                if (hasConfig == false) {
                                    resp.setStatus(406);
                                    jsonObject.put("error", "Cannot save plugin. Please remove from bungee type " + proxyType.getName() + " before changing configs");
                                    return jsonObject;
                                }
                            }
                            break;
                        }
                    }
                }

                DoubleChest.getPluginLoader().saveEntity(plugin);

                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Plugin Method Not allowed");
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
                JSONObject pluginJSON = new JSONObject(json);

                DCPlugin plugin = new DCPlugin();
                plugin.setName(pluginJSON.getString("name"));
                plugin.setType(DCPlugin.PluginType.valueOf(pluginJSON.getString("type")));
                plugin.setBaseFolder(pluginJSON.getString("baseFolder"));
                plugin.setConfigFolder(pluginJSON.getString("configFolder"));

                JSONArray configs = pluginJSON.getJSONArray("configs");
                for (int i = 0; i < configs.length(); i++) {
                    JSONObject object = configs.getJSONObject(i);
                    DCPlugin.PluginConfig pluginConfig = new DCPlugin.PluginConfig();
                    pluginConfig.set_id(new ObjectId());
                    pluginConfig.setName(object.getString("name"));
                    pluginConfig.setLocation(object.getString("location"));

                    plugin.getConfigs().put(pluginConfig.get_id(), pluginConfig);
                }

                DoubleChest.getPluginLoader().insertEntity(plugin);

                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Plugin Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject deleteJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.deleteJSON(req, resp);
        if (req.getRequestURI().endsWith("delete")) {
            String id = req.getParameter("id");
            try {
                DCPlugin plugin = DoubleChest.getPluginLoader().loadEntity(new ObjectId(id));
                if (plugin == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Plugin "+id);
                    return jsonObject;
                }

                for (DCServerType serverType : DoubleChest.getServerTypeLoader().getTypes()) {
                    DCServerTypeDriver serverTypeDriver = (DCServerTypeDriver) serverType.getDriver();
                    for (DCPlugin plugin1 : serverTypeDriver.getPlugins().keySet()) {
                        if (plugin1.get_id().equals(plugin.get_id())) {
                            resp.setStatus(406);
                            jsonObject.put("error", "Cannot delete plugin. Please remove from server type "+serverType.getName());
                            return jsonObject;
                        }
                    }
                }

                for (DCProxyType proxyType : DoubleChest.getProxyTypeLoader().getTypes()) {
                    DCProxyTypeDriver proxyTypeDriver = (DCProxyTypeDriver) proxyType.getDriver();
                    for (DCPlugin plugin1 : proxyTypeDriver.getPlugins().keySet()) {
                        if (plugin1.get_id().equals(plugin.get_id())) {
                            resp.setStatus(406);
                            jsonObject.put("error", "Cannot delete plugin. Please remove from bungee type "+proxyType.getName());
                            return jsonObject;
                        }
                    }
                }

                DoubleChest.getPluginLoader().removeEntity(plugin);
                return jsonObject;
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing DELETE request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Plugin Method Not allowed");
            return jsonObject;
        }
    }
}
