package io.minestack.servlets.api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.minestack.db.DoubleChest;
import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.proxy.DCProxyType;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@WebServlet(
        name = "APINodeServlet",
        urlPatterns = {"/api/node/all", "/api/node/one", "/api/node/save", "/api/node/add", "/api/node/delete", "/api/node/stop", "/api/node/start"})
@Log4j2
public class NodeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);
        if (req.getRequestURI().endsWith("one")) {
            String id = req.getParameter("id");

            try {
                DCNode node = DoubleChest.getNodeLoader().loadEntity(new ObjectId(id));

                if (node == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown Node "+id);
                    return jsonObject;
                }

                jsonObject.put("_id", node.get_id());
                jsonObject.put("host", node.getAddress());
                jsonObject.put("ram", node.getRam());
                if (node.getProxyType() != null) {
                    jsonObject.put("_proxyType", node.getProxyType().get_id().toString());
                } else {
                    jsonObject.put("_proxyType", "");
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

                DCNode node = new DCNode();
                node.setAddress(nodeJSON.getString("host"));
                node.setRam(nodeJSON.getInt("ram"));
                node.setLastUpdate(0L);

                DBObject dbObject = DoubleChest.getNodeLoader().getDb().findOne(DoubleChest.getNodeLoader().getCollection(), new BasicDBObject("host", node.getAddress()));
                if (dbObject != null) {
                    resp.setStatus(400);
                    jsonObject.put("error", "A node already exists with that IP Address.");
                    return jsonObject;
                }

                if (nodeJSON.getString("_proxyType").length() > 0) {
                    DCProxyType proxyType = DoubleChest.getProxyTypeLoader().loadEntity(new ObjectId(nodeJSON.getString("_proxyType")));
                    if (proxyType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Proxy Type " + nodeJSON.getString("_proxyType"));
                        return jsonObject;
                    }
                    node.setProxyType(proxyType);
                } else {
                    node.setProxyType(null);
                }

                DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
                config.withVersion("1.13");
                config.withUri("http://" + node.getAddress() + ":4243");
                DockerClient dockerClient = new DockerClientImpl(config.build());

                try {
                    dockerClient.createContainerCmd("minestack/nodecontroller")
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

                DoubleChest.getNodeLoader().insertEntity(node);
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

                DCNode node = new DCNode();
                node.set_id(new ObjectId(nodeJSON.getString("_id")));

                if (DoubleChest.getNodeLoader().loadEntity(node.get_id()) == null) {
                    resp.setStatus(404);
                    jsonObject.put("error", "Unknown node " + node.get_id());
                    return jsonObject;
                }

                node.setAddress(nodeJSON.getString("host"));
                node.setRam(nodeJSON.getInt("ram"));
                node.setLastUpdate(0L);

                if (nodeJSON.getString("_proxyType").length() > 0) {
                    DCProxyType proxyType = DoubleChest.getProxyTypeLoader().loadEntity(new ObjectId(nodeJSON.getString("_proxyType")));
                    if (proxyType == null) {
                        resp.setStatus(400);
                        jsonObject.put("error", "Unknown Proxy Type " + nodeJSON.getString("_proxyType"));
                        return jsonObject;
                    }
                    node.setProxyType(proxyType);
                } else {
                    node.setProxyType(null);
                }

                DoubleChest.getNodeLoader().saveEntity(node);
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

                DCNode node = DoubleChest.getNodeLoader().loadEntity(new ObjectId(nodeJSON.getString("_id")));

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

                DoubleChest.getNodeLoader().getDb().updateDocument(DoubleChest.getNodeLoader().getCollection(), new BasicDBObject("_id", node.get_id()), new BasicDBObject("$set", new BasicDBObject("lastUpdate", (long) 0)));
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

                DCNode node = DoubleChest.getNodeLoader().loadEntity(new ObjectId(nodeJSON.getString("_id")));

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
                DCNode node = DoubleChest.getNodeLoader().loadEntity(new ObjectId(id));
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

                DoubleChest.getNodeLoader().removeEntity(node);
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
