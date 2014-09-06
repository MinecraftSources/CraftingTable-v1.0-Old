package io.minestack.servlets.api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.NotFoundException;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import io.minestack.db.DoubleChest;
import io.minestack.db.entity.proxy.DCProxy;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "APIProxyServlet",
        urlPatterns = {"/api/bungee"})
@Log4j2
public class ProxyServlet extends APIServlet {

    @Override
    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.getJSON(req, resp);

        String id = req.getParameter("id");

        try {
            DCProxy proxy = DoubleChest.getProxyLoader().loadEntity(new ObjectId(id));

            if (proxy == null) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Proxy " + id + ".");
                return jsonObject;
            }

            jsonObject.put("_id", proxy.get_id().toString());

            if (proxy.getProxyType() != null) {
                JSONObject proxytype = new JSONObject();
                proxytype.put("_id", proxy.getProxyType().get_id().toString());
                proxytype.put("name", proxy.getProxyType().getName());
                jsonObject.put("_proxytype", proxytype);
            }

            if (proxy.getNode() != null) {
                JSONObject node = new JSONObject();
                node.put("_id", proxy.getNode().get_id());
                node.put("address", proxy.getNode().getAddress());
                jsonObject.put("_node", node);
            }
            return jsonObject;
        } catch (Exception ex) {
            resp.setStatus(400);
            jsonObject.put("error", "Invalid Proxy ID " + id);
            return jsonObject;
        }
    }

    @Override
    public JSONObject putJSON(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jsonObject = super.putJSON(req, resp);

        String id = req.getParameter("id");

        try {
            DCProxy proxy = DoubleChest.getProxyLoader().loadEntity(new ObjectId(id));
            if (proxy == null) {
                resp.setStatus(404);
                jsonObject.put("error", "Unknown Proxy " + id + ". Cannot stop.");
                return jsonObject;
            }

            if (proxy.getNode() == null) {
                resp.setStatus(500);
                jsonObject.put("error", "Proxy has a null node. Cannot stop.");
                return jsonObject;
            }
            DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
            config.withVersion("1.13");
            config.withUri("http://" + proxy.getNode().getAddress() + ":4243");
            DockerClient dockerClient = new DockerClientImpl(config.build());
            try {
                dockerClient.inspectContainerCmd(proxy.getContainerId()).exec();
                try {
                    dockerClient.killContainerCmd(proxy.getContainerId()).exec();
                } catch (Exception ignored) {
                }
            } catch (Exception ex) {
                if (!(ex instanceof NotFoundException)) {
                    resp.setStatus(500);
                    jsonObject.put("error", ex.getMessage());
                    return jsonObject;
                }
            }
            proxy.setLastUpdate(0);
            DoubleChest.getProxyLoader().saveEntity(proxy);
            return jsonObject;
        } catch (Exception ex) {
            resp.setStatus(400);
            jsonObject.put("error", "Invalid Proxy ID " + id + ". Cannot stop.");
            return jsonObject;
        }

    }
}
