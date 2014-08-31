package com.rmb938.mn2.docker.servlets.api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.DatabaseResource;
import com.rmb938.mn2.docker.db.entity.MN2BungeeType;
import com.rmb938.mn2.docker.db.entity.MN2Node;
import com.rmb938.mn2.docker.db.entity.MN2Plugin;
import com.rmb938.mn2.docker.db.entity.MN2ServerType;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@WebServlet(
        name = "APINodeServlet",
        urlPatterns = {"/mn2/api/node/all", "/mn2/api/node/one", "/mn2/api/node/save", "/mn2/api/node/add", "/mn2/api/node/delete", "/mn2/api/node/stop", "/mn2/api/node/start"})
@Log4j2
public class NodeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);
        if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");

            try {
                MN2Node node = DatabaseResource.getNodeLoader().loadEntity(new ObjectId(id));

                if (node == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Node "+id);
                    return jsonObject;
                }

                jsonObject.put("_id", node.get_id());
                jsonObject.put("host", node.getAddress());
                jsonObject.put("ram", node.getRam());
                if (node.getBungeeType() != null) {
                    jsonObject.put("_bungeeType", node.getBungeeType().get_id().toString());
                } else {
                    jsonObject.put("_bungeeType", "");
                }
                jsonObject.put("lastUpdate", node.getLastUpdate());

                return jsonObject;
            } catch (Exception e) {
                resp.setStatus(400);
                jsonObject.put("error", "Invalid Node ID " + id);
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Node Method Not allowed");
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
                JSONObject nodeJSON = new JSONObject(json);

                MN2Node node = new MN2Node();
                node.setAddress(nodeJSON.getString("host"));
                node.setRam(nodeJSON.getInt("ram"));
                node.setLastUpdate(0L);

                DBObject dbObject = DatabaseResource.getNodeLoader().getDb().findOne(DatabaseResource.getNodeLoader().getCollection(), new BasicDBObject("host", node.getAddress()));
                if (dbObject != null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "A node already exists with that IP Address.");
                    return jsonObject;
                }

                if (nodeJSON.getString("_bungeeType").length() > 0) {
                    MN2BungeeType bungeeType = DatabaseResource.getBungeeTypeLoader().loadEntity(new ObjectId(nodeJSON.getString("_bungeeType")));
                    if (bungeeType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Bungee Type " + nodeJSON.getString("_bungeeType"));
                        return jsonObject;
                    }
                } else {
                    node.setBungeeType(null);
                }

                DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
                config.withVersion("1.13");
                config.withUri("http://" + node.getAddress() + ":4243");
                DockerClient dockerClient = new DockerClientImpl(config.build());

                try {
                    dockerClient.createContainerCmd("mnsquared/nodecontroller")
                        .withEnv("MONGO_HOSTS=" + System.getenv("MONGO_HOSTS"),
                            "RABBITMQ_HOSTS=" + System.getenv("RABBITMQ_HOSTS"),
                            "RABBITMQ_USERNAME=" + System.getenv("RABBITMQ_USERNAME"),
                            "RABBITMQ_PASSWORD=" + System.getenv("RABBITMQ_PASSWORD"),
                            "MY_NODE_IP=" +node.getAddress())
                        .withName("controller")
                        .exec();
                } catch (Exception ex) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Docker Error "+ex.getMessage());
                    return jsonObject;
                }

                DatabaseResource.getNodeLoader().insertEntity(node);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing Post request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Node Method Not allowed");
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
                JSONObject nodeJSON = new JSONObject(json);

                MN2Node node = new MN2Node();
                node.set_id(new ObjectId(nodeJSON.getString("_id")));

                if (DatabaseResource.getNodeLoader().loadEntity(node.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown node " + node.get_id());
                    return jsonObject;
                }

                node.setAddress(nodeJSON.getString("host"));
                node.setRam(nodeJSON.getInt("ram"));
                node.setLastUpdate(0L);

                if (nodeJSON.getString("_bungeeType").length() > 0) {
                    MN2BungeeType bungeeType = DatabaseResource.getBungeeTypeLoader().loadEntity(new ObjectId(nodeJSON.getString("_bungeeType")));
                    if (bungeeType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Bungee Type " + nodeJSON.getString("_bungeeType"));
                        return jsonObject;
                    }
                    node.setBungeeType(bungeeType);
                } else {
                    node.setBungeeType(null);
                }

                DatabaseResource.getNodeLoader().saveEntity(node);
                return jsonObject;
            } catch (Exception e) {
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else if (req.getRequestURI().endsWith("stop")) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                String json = reader.readLine();
                if (json == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "You must send something");
                    return jsonObject;
                }
                JSONObject nodeJSON = new JSONObject(json);

                MN2Node node = DatabaseResource.getNodeLoader().loadEntity(new ObjectId(nodeJSON.getString("_id")));

                if (node == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown node " + nodeJSON.getString("_id"));
                    return jsonObject;
                }

                DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
                config.withVersion("1.13");
                config.withUri("http://" + node.getAddress() + ":4243");
                DockerClient dockerClient = new DockerClientImpl(config.build());
                try {
                    for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
                        String name = container.getNames()[0];
                        if (name.equals("/controller")) {
                            try {
                                dockerClient.killContainerCmd(container.getId()).exec();
                            } catch (Exception ignored) {
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Docker Error "+ex.getMessage());
                    return jsonObject;
                }

                return jsonObject;
            } catch (Exception e) {
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else if (req.getRequestURI().endsWith("start")) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                String json = reader.readLine();
                if (json == null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "You must send something");
                    return jsonObject;
                }
                JSONObject nodeJSON = new JSONObject(json);

                MN2Node node = DatabaseResource.getNodeLoader().loadEntity(new ObjectId(nodeJSON.getString("_id")));

                if (node == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown node " + nodeJSON.getString("_id"));
                    return jsonObject;
                }

                DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
                config.withVersion("1.13");
                config.withUri("http://" + node.getAddress() + ":4243");
                DockerClient dockerClient = new DockerClientImpl(config.build());
                try {
                    for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
                        String name = container.getNames()[0];
                        if (name.equals("/controller")) {
                            try {
                                dockerClient.startContainerCmd(container.getId()).exec();
                            } catch (Exception ignored) {
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Docker Error "+ex.getMessage());
                    return jsonObject;
                }

                return jsonObject;
            } catch (Exception e) {
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing PUT request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Node Method Not allowed");
            return jsonObject;
        }
    }

    @Override
    public JSONObject deleteJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        if (req.getRequestURI().endsWith("delete")) {
            String id = req.getParameter("id");
            try {
                MN2Node node = DatabaseResource.getNodeLoader().loadEntity(new ObjectId(id));
                if (node == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Node "+id);
                    return jsonObject;
                }

                DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
                config.withVersion("1.13");
                config.withUri("http://" + node.getAddress() + ":4243");
                DockerClient dockerClient = new DockerClientImpl(config.build());
                try {
                    for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
                        String name = container.getNames()[0];
                        if (name.equals("/controller")) {
                            try {
                                dockerClient.killContainerCmd(container.getId()).exec();
                            } catch (Exception ignored) {
                            }
                            dockerClient.removeContainerCmd(container.getId()).exec();
                            break;
                        }
                    }
                } catch (Exception ex) {
                    resp.setStatus(400);
                    jsonObject.put("error", "Docker Error "+ex.getMessage());
                    return jsonObject;
                }

                DatabaseResource.getNodeLoader().removeEntity(node);
                return jsonObject;
            } catch (Exception ex) {
                ex.printStackTrace();
                resp.setStatus(400);
                jsonObject.put("error", "Error parsing DELETE request");
                return jsonObject;
            }
        } else {
            resp.setStatus(405);
            jsonObject.put("error", "Node Method Not allowed");
            return jsonObject;
        }
    }
}
