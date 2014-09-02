package io.minestack.servlets.api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.NotFoundException;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import io.minestack.db.DoubleChest;
import io.minestack.db.entity.DCBungee;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "APIBungeeServlet",
        urlPatterns = {"/api/bungee"})
@Log4j2
public class BungeeServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        String id = req.getParameter("id");

        try {
            DCBungee bungee = DoubleChest.getBungeeLoader().loadEntity(new ObjectId(id));

            if (bungee == null) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Bungee " + id + ".");
                return jsonObject;
            }

            jsonObject.put("_id", bungee.get_id().toString());

            if (bungee.getBungeeType() != null) {
                JSONObject bungeetype = new JSONObject();
                bungeetype.put("_id", bungee.getBungeeType().get_id().toString());
                bungeetype.put("name", bungee.getBungeeType().getName());
                jsonObject.put("_bungeetype", bungeetype);
            }

            if (bungee.getNode() != null) {
                JSONObject node = new JSONObject();
                node.put("_id", bungee.getNode().get_id());
                node.put("address", bungee.getNode().getAddress());
                jsonObject.put("_node", node);
            }
            return jsonObject;
        } catch (Exception ex) {
            resp.setStatus(400);
            jsonObject.put("error", "Invalid Bungee ID " + id);
            return jsonObject;
        }
    }

    @Override
    public JSONObject putJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        String id = req.getParameter("id");

        try {
            DCBungee bungee = DoubleChest.getBungeeLoader().loadEntity(new ObjectId(id));
            if (bungee == null) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Bungee " + id + ". Cannot stop.");
                return jsonObject;
            }

            if (bungee.getNode() == null) {
                resp.setStatus(500);
                jsonObject.put("error", "Bungee has a null node. Cannot stop.");
                return jsonObject;
            }
            DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
            config.withVersion("1.13");
            config.withUri("http://" + bungee.getNode().getAddress() + ":4243");
            DockerClient dockerClient = new DockerClientImpl(config.build());
            try {
                dockerClient.inspectContainerCmd(bungee.getContainerId()).exec();
                try {
                    dockerClient.killContainerCmd(bungee.getContainerId()).exec();
                } catch (Exception ignored) {
                }
            } catch (Exception ex) {
                if (!(ex instanceof NotFoundException)) {
                    resp.setStatus(500);
                    jsonObject.put("error", ex.getMessage());
                    return jsonObject;
                }
            }
            bungee.setLastUpdate(0);
            DoubleChest.getBungeeLoader().saveEntity(bungee);
            return jsonObject;
        } catch (Exception ex) {
            resp.setStatus(400);
            jsonObject.put("error", "Invalid Bungee ID " + id + ". Cannot stop.");
            return jsonObject;
        }

    }
}
