package io.minestack.servlets.api;

import io.minestack.db.DoubleChest;
import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.driver.DCDriver;
import io.minestack.db.entity.proxy.DCProxyType;
import io.minestack.db.entity.proxy.DCProxyTypeDriver;
import io.minestack.db.entity.server.DCServerType;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@WebServlet(
        name = "APIProxyTypeServlet",
        urlPatterns = {"/api/bungeetype/all", "/api/bungeetype/one", "/api/bungeetype/save", "/api/bungeetype/add", "/api/bungeetype/delete"})
@Log4j2
public class ProxyTypeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");

            try {
                DCProxyType proxyType = DoubleChest.getProxyTypeLoader().loadEntity(new ObjectId(id));

                if (proxyType == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Proxy Type " + id);
                    return jsonObject;
                }

                jsonObject.put("_id", proxyType.get_id().toString());
                jsonObject.put("name", proxyType.getName());

                JSONArray serverTypes = new JSONArray();
                for (DCServerType serverType : proxyType.getServerTypes().keySet()) {
                    boolean allowRejoin = proxyType.getServerTypes().get(serverType);
                    JSONObject serverTypeJSON = new JSONObject();
                    serverTypeJSON.put("_id", serverType.get_id());
                    serverTypeJSON.put("allowRejoin", allowRejoin);
                    if (serverType == proxyType.getDefaultType()) {
                        serverTypeJSON.put("isDefault", true);
                    } else {
                        serverTypeJSON.put("isDefault", false);
                    }
                    serverTypes.put(serverTypeJSON);
                }
                jsonObject.put("serverTypes", serverTypes);

                JSONObject driver = new JSONObject();
                driver.put("driverName", proxyType.getDriver().getDriverName());

                DCProxyTypeDriver proxyTypeDriver = (DCProxyTypeDriver) proxyType.getDriver();
                JSONArray plugins = new JSONArray();
                for (DCPlugin plugin : proxyTypeDriver.getPlugins().keySet()) {
                    DCPlugin.PluginConfig pluginConfig = proxyTypeDriver.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                driver.put("plugins", plugins);

                jsonObject.put("driver", driver);
                return jsonObject;
            } catch (Exception ex) {
                resp.setStatus(400);
                jsonObject.put("error", "Invalid Bungee ID " + id);
                return jsonObject;
            }
        } else if (req.getRequestURI().endsWith("all")) {
            JSONArray proxyTypes = new JSONArray();

            for (DCProxyType proxyType : DoubleChest.getProxyTypeLoader().getTypes()) {
                JSONObject proxyTypeJSON = new JSONObject();

                proxyTypeJSON.put("_id", proxyType.get_id().toString());
                proxyTypeJSON.put("name", proxyType.getName());

                JSONArray serverTypes = new JSONArray();
                for (DCServerType serverType : proxyType.getServerTypes().keySet()) {
                    boolean allowRejoin = proxyType.getServerTypes().get(serverType);
                    JSONObject serverTypeJSON = new JSONObject();
                    serverTypeJSON.put("_id", serverType.get_id());
                    serverTypeJSON.put("allowRejoin", allowRejoin);
                    if (serverType == proxyType.getDefaultType()) {
                        serverTypeJSON.put("isDefault", true);
                    } else {
                        serverTypeJSON.put("isDefault", false);
                    }
                    serverTypes.put(serverTypeJSON);
                }
                proxyTypeJSON.put("serverTypes", serverTypes);

                JSONObject driver = new JSONObject();
                driver.put("driverName", proxyType.getDriver().getDriverName());

                DCProxyTypeDriver dcProxyTypeDriver = (DCProxyTypeDriver) proxyType.getDriver();
                JSONArray plugins = new JSONArray();
                for (DCPlugin plugin : dcProxyTypeDriver.getPlugins().keySet()) {
                    DCPlugin.PluginConfig pluginConfig = dcProxyTypeDriver.getPlugins().get(plugin);

                    JSONObject pluginJSON = new JSONObject();
                    pluginJSON.put("_id", plugin.get_id().toString());
                    if (pluginConfig != null) {
                        pluginJSON.put("_configId", pluginConfig.get_id().toString());
                    }
                    plugins.put(pluginJSON);
                }
                driver.put("plugins", plugins);

                proxyTypeJSON.put("driver", driver);

                proxyTypes.put(proxyTypeJSON);
            }

            jsonObject.put("proxyTypes", proxyTypes);
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
                JSONObject proxyTypeJSON = new JSONObject(json);

                DCProxyType proxyType = new DCProxyType();
                proxyType.setName(proxyTypeJSON.getString("name"));

                JSONArray serverTypes = proxyTypeJSON.getJSONArray("serverTypes");
                for (int i = 0; i < serverTypes.length(); i++) {
                    JSONObject object = serverTypes.getJSONObject(i);
                    DCServerType serverType = DoubleChest.getServerTypeLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (serverType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Server Type "+object.getString("_id"));
                        return jsonObject;
                    }
                    proxyType.getServerTypes().put(serverType, object.getBoolean("allowRejoin"));
                    if (object.getBoolean("isDefault")) {
                        proxyType.setDefaultType(serverType);
                    }
                }

                JSONObject driver = proxyTypeJSON.getJSONObject("driver");
                String driverName = driver.getString("driverName");

                Class driverClass = DCDriver.getDrivers().get(driverName);

                if (driverClass == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Unknown Driver " + driverName);
                    return jsonObject;
                }

                DCDriver dcDriver = DCDriver.getDrivers().get(driverName).newInstance();

                DCProxyTypeDriver proxyTypeDriver = (DCProxyTypeDriver) dcDriver;
                JSONArray plugins = driver.getJSONArray("plugins");
                for (int i = 0; i < plugins.length(); i++) {
                    JSONObject object = plugins.getJSONObject(i);
                    DCPlugin plugin = DoubleChest.getPluginLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (plugin == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Plugin Type " + object.getString("_id"));
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
                    proxyTypeDriver.getPlugins().put(plugin, pluginConfig);
                }

                proxyType.setDriver(dcDriver);

                if (proxyType.getDefaultType() == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "No default server type");
                    return jsonObject;
                }
                DoubleChest.getProxyTypeLoader().insertEntity(proxyType);
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
                JSONObject proxy = new JSONObject(json);

                DCProxyType proxyType = new DCProxyType();
                proxyType.set_id(new ObjectId(proxy.getString("_id")));

                if (DoubleChest.getProxyTypeLoader().loadEntity(proxyType.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown proxy type " + proxyType.get_id());
                    return jsonObject;
                }

                proxyType.setName(proxy.getString("name"));

                JSONArray serverTypes = proxy.getJSONArray("serverTypes");
                for (int i = 0; i < serverTypes.length(); i++) {
                    JSONObject object = serverTypes.getJSONObject(i);
                    DCServerType serverType = DoubleChest.getServerTypeLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (serverType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Server Type "+object.getString("_id"));
                        return jsonObject;
                    }
                    proxyType.getServerTypes().put(serverType, object.getBoolean("allowRejoin"));
                    if (object.getBoolean("isDefault")) {
                        if (proxyType.getDefaultType() != null) {
                            resp.setStatus(400);
                            jsonObject.put("error", "Proxy Type already has a default server type " + proxyType.getDefaultType().getName());
                            return jsonObject;
                        }
                        proxyType.setDefaultType(serverType);
                    }
                }

                JSONObject driver = proxy.getJSONObject("driver");
                String driverName = driver.getString("driverName");

                Class driverClass = DCDriver.getDrivers().get(driverName);

                if (driverClass == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Unknown Driver " + driverName);
                    return jsonObject;
                }

                DCDriver dcDriver = DCDriver.getDrivers().get(driverName).newInstance();

                DCProxyTypeDriver proxyTypeDriver = (DCProxyTypeDriver) dcDriver;
                JSONArray plugins = driver.getJSONArray("plugins");
                for (int i = 0; i < plugins.length(); i++) {
                    JSONObject object = plugins.getJSONObject(i);
                    DCPlugin plugin = DoubleChest.getPluginLoader().loadEntity(new ObjectId(object.getString("_id")));
                    if (plugin == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Plugin Type " + object.getString("_id"));
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
                    proxyTypeDriver.getPlugins().put(plugin, pluginConfig);
                }

                proxyType.setDriver(dcDriver);

                if (proxyType.getDefaultType() == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "No default server type");
                    return jsonObject;
                }
                DoubleChest.getProxyTypeLoader().saveEntity(proxyType);
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
                DCProxyType proxyType = DoubleChest.getProxyTypeLoader().loadEntity(new ObjectId(id));
                if (proxyType == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Proxy Type " + id);
                    return jsonObject;
                }

                for (DCNode node : DoubleChest.getNodeLoader().getNodes()) {
                    if (node.getProxyType() != null && node.getProxyType().get_id().equals(proxyType.get_id())) {
                        resp.setStatus(406);
                        jsonObject.put("error", "Cannot delete proxy type. Please remove from node " + node.getAddress());
                        return jsonObject;
                    }
                }

                DoubleChest.getProxyTypeLoader().removeEntity(proxyType);
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
