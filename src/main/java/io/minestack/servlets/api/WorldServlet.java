package io.minestack.servlets.api;

import io.minestack.db.DoubleChest;
import io.minestack.db.entity.DCServerType;
import io.minestack.db.entity.DCWorld;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@WebServlet(
        name = "APIWorldServlet",
        urlPatterns = {"/api/world/all", "/api/world/one", "/api/world/save", "/api/world/add", "/api/world/delete"})
public class WorldServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        if (req.getRequestURI().endsWith("all")) {
            JSONArray worldsJSON = new JSONArray();
            for (DCWorld world : DoubleChest.getWorldLoader().getWorlds()) {
                JSONObject worldJSON = new JSONObject();

                worldJSON.put("_id", world.get_id());
                worldJSON.put("name", world.getName());
                worldJSON.put("environment", world.getEnvironment().name());
                worldJSON.put("folder", world.getFolder());
                worldJSON.put("generator", world.getGenerator());

                worldsJSON.put(worldJSON);
            }
            jsonObject.put("worlds", worldsJSON);
            return jsonObject;
        } else if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");

            try {
                DCWorld world = DoubleChest.getWorldLoader().loadEntity(new ObjectId(id));

                jsonObject.put("_id", world.get_id());
                jsonObject.put("name", world.getName());
                jsonObject.put("environment", world.getEnvironment().name());
                jsonObject.put("folder", world.getFolder());
                jsonObject.put("generator", world.getGenerator());

                return jsonObject;
            } catch (Exception ex) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown World "+id);
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "World Method Not allowed");
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
                JSONObject worldJSON = new JSONObject(json);

                DCWorld world = new DCWorld();
                world.set_id(new ObjectId(worldJSON.getString("_id")));

                if (DoubleChest.getWorldLoader().loadEntity(world.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown world "+world.get_id());
                    return jsonObject;
                }

                world.setName(worldJSON.getString("name"));
                world.setFolder(worldJSON.getString("folder"));
                world.setEnvironment(DCWorld.Environment.valueOf(worldJSON.getString("environment")));
                if (worldJSON.has("generator") && worldJSON.getString("generator").length() > 0) {
                    world.setGenerator(worldJSON.getString("generator"));
                }

                DoubleChest.getWorldLoader().saveEntity(world);

                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "World Method Not allowed");
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
                JSONObject worldJSON = new JSONObject(json);

                DCWorld world = new DCWorld();
                world.setName(worldJSON.getString("name"));
                world.setFolder(worldJSON.getString("folder"));
                world.setEnvironment(DCWorld.Environment.valueOf(worldJSON.getString("environment")));
                if (worldJSON.has("generator") && worldJSON.getString("generator").length() > 0) {
                        world.setGenerator(worldJSON.getString("generator"));
                }

                DoubleChest.getWorldLoader().insertEntity(world);

                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "World Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject deleteJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        if (req.getRequestURI().endsWith("delete")) {
            String id = req.getParameter("id");
            try {
                DCWorld world = DoubleChest.getWorldLoader().loadEntity(new ObjectId(id));
                if (world == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown World Type "+id);
                    return jsonObject;
                }

                for (DCServerType serverType : DoubleChest.getServerTypeLoader().getTypes()) {
                    for (DCWorld world1 : serverType.getWorlds()) {
                        if (world1.get_id().equals(world.get_id())) {
                            resp.setStatus(406);
                            jsonObject.put("error", "Cannot delete world. Please remove from server type "+serverType.getName());
                            return jsonObject;
                        }
                    }
                }

                DoubleChest.getWorldLoader().removeEntity(world);
                return jsonObject;
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing DELETE request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "World Method Not allowed");
            return jsonObject;
        }
    }
}
