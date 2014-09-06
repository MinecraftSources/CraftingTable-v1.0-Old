package io.minestack.servlets.api;

import io.minestack.db.DoubleChest;
import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.DCWorld;
import io.minestack.db.entity.driver.DCDriver;
import io.minestack.db.entity.proxy.DCProxyType;
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

@WebServlet(
        name = "APIServerTypeServlet",
        urlPatterns = {"/api/servertype/all", "/api/servertype/one", "/api/servertype/save", "/api/servertype/add", "/api/servertype/delete"})
public class ServerTypeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        if (req.getRequestURI().endsWith("all")) {
            JSONArray serverTypes = new JSONArray();
            for (DCServerType serverType : DoubleChest.getServerTypeLoader().getTypes()) {
                JSONObject serverTypeJSON = new JSONObject();
                serverTypeJSON.put("_id", serverType.get_id().toString());
                serverTypeJSON.put("name", serverType.getName());
                serverTypeJSON.put("players", serverType.getPlayers());
                serverTypeJSON.put("memory", serverType.getMemory());
                serverTypeJSON.put("amount", serverType.getAmount());
                serverTypeJSON.put("disabled", serverType.isDisabled());

                JSONObject driver = new JSONObject();
                driver.put("driverName", serverType.getDriver().getDriverName());

                DCServerTypeDriver serverTypeDriver = (DCServerTypeDriver) serverType.getDriver();
                JSONArray plugins = new JSONArray();
                for (DCPlugin plugin : serverTypeDriver.getPlugins().keySet()) {
                    DCPlugin.PluginConfig pluginConfig = serverTypeDriver.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                driver.put("plugins", plugins);

                serverTypeJSON.put("driver", driver);

                JSONArray worlds = new JSONArray();
                for (DCWorld world : serverType.getWorlds()) {
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

            return jsonObject;
        } else if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");
            try {
                DCServerType serverType = DoubleChest.getServerTypeLoader().loadEntity(new ObjectId(id));

                if (serverType == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Server Type "+id);
                    return jsonObject;
                }

                jsonObject.put("_id", serverType.get_id().toString());
                jsonObject.put("name", serverType.getName());
                jsonObject.put("players", serverType.getPlayers());
                jsonObject.put("memory", serverType.getMemory());
                jsonObject.put("amount", serverType.getAmount());
                jsonObject.put("disabled", serverType.isDisabled());

                JSONObject driver = new JSONObject();
                driver.put("driverName", serverType.getDriver().getDriverName());

                DCServerTypeDriver serverTypeDriver = (DCServerTypeDriver) serverType.getDriver();
                JSONArray plugins = new JSONArray();
                for (DCPlugin plugin : serverTypeDriver.getPlugins().keySet()) {
                    DCPlugin.PluginConfig pluginConfig = serverTypeDriver.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                driver.put("plugins", plugins);

                jsonObject.put("driver", driver);

                JSONArray worlds = new JSONArray();
                for (DCWorld world : serverType.getWorlds()) {
                    JSONObject worldJSON = new JSONObject();
                    worldJSON.put("_id", world.get_id().toString());
                    if (world == serverType.getDefaultWorld()) {
                        worldJSON.put("isDefault", true);
                    } else {
                        worldJSON.put("isDefault", false);
                    }

                    worlds.put(worldJSON);
                }
                jsonObject.put("worlds", worlds);

                return jsonObject;
            } catch (Exception ex) {
                resp.setStatus(400);
                jsonObject.put("error", "Invalid Server ID " + id);
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Server Type Method Not allowed");
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
                JSONObject serverTypeJSON = new JSONObject(json);

                DCServerType serverType = new DCServerType();

                serverType.setName(serverTypeJSON.getString("name"));
                serverType.setPlayers(serverTypeJSON.getInt("players"));
                serverType.setMemory(serverTypeJSON.getInt("memory"));
                serverType.setAmount(serverTypeJSON.getInt("amount"));
                serverType.setDisabled(serverTypeJSON.getBoolean("disabled"));

                JSONObject driver = serverTypeJSON.getJSONObject("driver");
                String driverName = driver.getString("driverName");

                Class driverClass = DCDriver.getDrivers().get(driverName);

                if (driverClass == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Unknown Driver " + driverName);
                    return jsonObject;
                }

                DCDriver dcDriver = DCDriver.getDrivers().get(driverName).newInstance();

                DCServerTypeDriver serverTypeDriver = (DCServerTypeDriver) dcDriver;
                JSONArray plugins = driver.getJSONArray("plugins");
                for (int i = 0; i < plugins.length(); i++) {
                    JSONObject object = plugins.getJSONObject(i);
                    DCPlugin plugin = DoubleChest.getPluginLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (plugin == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Plugin " + object.getString("_id"));
                        return jsonObject;
                        }
                    DCPlugin.PluginConfig pluginConfig = null;
                    if (object.has("_configId")) {
                        pluginConfig = plugin.getConfigs().get(new ObjectId("_id"));
                        if (pluginConfig == null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Unknown Plugin Config " + object.getString("_id") + " for plugin " + plugin.getName());
                            return jsonObject;
                        }
                    }
                    if (pluginConfig == null && plugin.getConfigs().size() > 0) {
                        resp.setStatus(400);
                        jsonObject.put("error", "There must be a config for " + plugin.getName());
                        return jsonObject;
                    }
                    serverTypeDriver.getPlugins().put(plugin, pluginConfig);
                }
                serverType.setDriver(dcDriver);

                JSONArray worlds = serverTypeJSON.getJSONArray("worlds");
                for (int i = 0; i < worlds.length(); i++) {
                    JSONObject object = worlds.getJSONObject(i);
                    DCWorld world = DoubleChest.getWorldLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (world == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown World "+object.getString("_id"));
                        return jsonObject;
                    }
                    serverType.getWorlds().add(world);
                    if (object.getBoolean("isDefault")) {
                        if (serverType.getDefaultWorld() != null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Server Type already has a default world "+serverType.getDefaultWorld().getName());
                            return jsonObject;
                        }
                        serverType.setDefaultWorld(world);
                    }
                }

                if (serverType.getDefaultWorld() == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "No default world");
                    return jsonObject;
                }
                DoubleChest.getServerTypeLoader().insertEntity(serverType);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing POST request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Server Type Method Not allowed");
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
                JSONObject serverTypeJSON = new JSONObject(json);

                DCServerType serverType = new DCServerType();
                serverType.set_id(new ObjectId(serverTypeJSON.getString("_id")));

                if (DoubleChest.getServerTypeLoader().loadEntity(serverType.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown server type "+serverType.get_id());
                    return jsonObject;
                }

                serverType.setName(serverTypeJSON.getString("name"));
                serverType.setPlayers(serverTypeJSON.getInt("players"));
                serverType.setMemory(serverTypeJSON.getInt("memory"));
                serverType.setAmount(serverTypeJSON.getInt("amount"));
                serverType.setDisabled(serverTypeJSON.getBoolean("disabled"));

                JSONObject driver = serverTypeJSON.getJSONObject("driver");
                String driverName = driver.getString("driverName");

                Class driverClass = DCDriver.getDrivers().get(driverName);

                if (driverClass == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Unknown Driver " + driverName);
                    return jsonObject;
                }

                DCDriver dcDriver = DCDriver.getDrivers().get(driverName).newInstance();

                DCServerTypeDriver serverTypeDriver = (DCServerTypeDriver) dcDriver;
                JSONArray plugins = driver.getJSONArray("plugins");
                for (int i = 0; i < plugins.length(); i++) {
                    JSONObject object = plugins.getJSONObject(i);
                    DCPlugin plugin = DoubleChest.getPluginLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (plugin == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Plugin " + object.getString("_id"));
                        return jsonObject;
                        }
                    DCPlugin.PluginConfig pluginConfig = null;
                    if (object.has("_configId")) {
                        pluginConfig = plugin.getConfigs().get(new ObjectId(object.getString("_configId")));
                        if (pluginConfig == null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Unknown Plugin Config " + object.getString("_id") + " for plugin " + plugin.getName());
                            return jsonObject;
                        }
                    }
                    if (pluginConfig == null && plugin.getConfigs().size() > 0) {
                        resp.setStatus(400);
                        jsonObject.put("error", "There must be a config for " + plugin.getName());
                        return jsonObject;
                    }
                    serverTypeDriver.getPlugins().put(plugin, pluginConfig);
                }
                serverType.setDriver(dcDriver);

                JSONArray worlds = serverTypeJSON.getJSONArray("worlds");
                for (int i = 0; i < worlds.length(); i++) {
                    JSONObject object = worlds.getJSONObject(i);
                    DCWorld world = DoubleChest.getWorldLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (world == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown World "+object.getString("_id"));
                        return jsonObject;
                    }
                    serverType.getWorlds().add(world);
                    if (object.getBoolean("isDefault")) {
                        if (serverType.getDefaultWorld() != null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Server Type already has a default world "+serverType.getDefaultWorld().getName());
                            return jsonObject;
                        }
                        serverType.setDefaultWorld(world);
                    }
                }

                if (serverType.getDefaultWorld() == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "No default world");
                    return jsonObject;
                }
                DoubleChest.getServerTypeLoader().saveEntity(serverType);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Server Type Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject deleteJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        if (req.getRequestURI().endsWith("delete")) {
            String id = req.getParameter("id");
            try {
                DCServerType serverType = DoubleChest.getServerTypeLoader().loadEntity(new ObjectId(id));
                if (serverType == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Server Type "+id);
                    return jsonObject;
                }

                for (DCProxyType proxyType : DoubleChest.getProxyTypeLoader().getTypes()) {
                    for (DCServerType serverType1 : proxyType.getServerTypes().keySet()) {
                        if (serverType1.get_id().equals(serverType.get_id())) {
                            resp.setStatus(406);
                            jsonObject.put("error", "Cannot delete server type. Please remove from proxy " + proxyType.getName());
                            return jsonObject;
                        }
                    }
                }

                DoubleChest.getServerTypeLoader().removeEntity(serverType);
                return jsonObject;
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing DELETE request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Server Type Method Not allowed");
            return jsonObject;
        }
    }
}
