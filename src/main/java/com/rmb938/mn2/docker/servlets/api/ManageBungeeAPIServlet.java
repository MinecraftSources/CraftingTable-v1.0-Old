package com.rmb938.mn2.docker.servlets.api;

import com.mongodb.util.JSON;
import com.rmb938.mn2.docker.DatabaseResource;
import com.rmb938.mn2.docker.db.entity.MN2Bungee;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ManageBungeeServlet",
        urlPatterns = {"/mn2/api/bungee/manage"})
public class ManageBungeeAPIServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseResource.initDatabase();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");

        JSONObject object = new JSONObject();
        ObjectId objectId;
        try {
            objectId = new ObjectId(req.getParameter("bungee"));
        } catch (Exception ex) {
            sendError(500, "Unknown Bungee ID", resp);
            return;
        }
        MN2Bungee bungee = DatabaseResource.getBungeeLoader().loadEntity(objectId);
        if (bungee == null) {
            sendError(404, "Unknown Bungee "+objectId.toString(), resp);
            return;
        }
        object.put("_id", bungee.get_id().toString());
        if (bungee.getBungeeType() != null) {
            JSONObject type = new JSONObject();
            type.put("_id", bungee.getBungeeType().get_id());
            type.put("name", bungee.getBungeeType().getName());

            object.put("type", type);
        }
        if (bungee.getNode() != null) {
            JSONObject node = new JSONObject();
            node.put("_id", bungee.getNode().get_id().toString());
            node.put("address", bungee.getNode().getAddress());

            object.put("node", bungee.getNode());
        }
        object.put("containerId", bungee.getContainerId());

        resp.getWriter().println(object.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
    }

    private void sendError(int status, String error, HttpServletResponse resp) throws IOException {
        resp.setStatus(status);
        JSONObject object = new JSONObject();
        object.put("status", status);
        object.put("error", error);

        resp.getWriter().println(object.toString());
    }
}
