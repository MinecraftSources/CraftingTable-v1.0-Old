package io.minestack.servlets.api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.NotFoundException;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import io.minestack.DatabaseResource;
import io.minestack.db.entity.MN2Server;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "APIServerServlet",
        urlPatterns = {"/api/server"})
@Log4j2
public class ServerServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        String id = req.getParameter("id");

        try {
            MN2Server server = DatabaseResource.getServerLoader().loadEntity(new ObjectId(id));

            if (server == null) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Server " + id + ".");
                return jsonObject;
            }

            jsonObject.put("_id", server.get_id().toString());

            if (server.getServerType() != null) {
                JSONObject servertype = new JSONObject();
                servertype.put("_id", server.getServerType().get_id().toString());
                servertype.put("name", server.getServerType().getName());
                jsonObject.put("_servertype", servertype);
            }

            if (server.getNode() != null) {
                JSONObject node = new JSONObject();
                node.put("_id", server.getNode().get_id());
                node.put("address", server.getNode().getAddress());
                jsonObject.put("_node", node);
            }

            jsonObject.put("port", server.getPort());
            jsonObject.put("number", server.getNumber());

            return jsonObject;
        } catch (Exception ex) {
            resp.setStatus(400);
            jsonObject.put("error", "Invalid Server ID " + id);
            return jsonObject;
        }
    }

    @Override
    public JSONObject putJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        String id = req.getParameter("id");

        try {
            MN2Server server = DatabaseResource.getServerLoader().loadEntity(new ObjectId(id));

            if (server == null) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Server " + id + ".");
                return jsonObject;
            }

            if (server.getNode() == null) {
                resp.setStatus(500);
                jsonObject.put("error", "Server has a null node. Cannot stop.");
                return jsonObject;
            }

            DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
            config.withVersion("1.14");
            config.withUri("http://" + server.getNode().getAddress() + ":4243");
            DockerClient dockerClient = new DockerClientImpl(config.build());
            try {
                dockerClient.inspectContainerCmd(server.getContainerId()).exec();
                try {
                    dockerClient.killContainerCmd(server.getContainerId())/*.withSignal("SIGINT")*/.exec();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                dockerClient.removeContainerCmd(server.getContainerId()).exec();
            } catch (Exception ex) {
                if (!(ex instanceof NotFoundException)) {
                    resp.setStatus(500);
                    jsonObject.put("error", ex.getMessage());
                    return jsonObject;
                }
            }
            server.setLastUpdate(0);
            DatabaseResource.getServerLoader().saveEntity(server);
            return jsonObject;
        } catch (Exception ex) {
            resp.setStatus(400);
            jsonObject.put("error", "Invalid Server ID " + id + ". Cannot stop.");
            return jsonObject;
        }
    }

}
