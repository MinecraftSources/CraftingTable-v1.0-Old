package com.rmb938.mn2.docker.servlets;

import com.github.dockerjava.client.DockerClient;
import com.github.dockerjava.client.NotFoundException;
import com.rmb938.mn2.docker.DatabaseResource;
import com.rmb938.mn2.docker.db.entity.MN2Bungee;
import org.bson.types.ObjectId;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ManageBungeeServlet",
        urlPatterns = {"/mn2/bungee/manage"})
public class ManageBungeeServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseResource.initDatabase();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index/index.jsp");
        req.setAttribute("partial", "bungee-view");
        req.setAttribute("javascript", "manageBungee");

        String error = null;

        req.setAttribute("id", "");
        req.setAttribute("type", "");
        req.setAttribute("node", "");


        ObjectId objectId = new ObjectId(req.getParameter("bungee"));
        MN2Bungee bungee = DatabaseResource.getBungeeLoader().loadEntity(objectId);
        if (bungee != null) {
            req.setAttribute("id", bungee.get_id().toString());
            if (bungee.getBungeeType() != null) {
                req.setAttribute("type", bungee.getBungeeType().getName());
            }
            if (bungee.getNode() != null) {
                req.setAttribute("node", bungee.getNode().getAddress());
            }
        } else {
            error = "Bungee " + objectId.toString() + " not found";
        }

        if (error != null) {
            resp.setStatus(404);
        }

        req.setAttribute("error", error);

        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String error = null;

        String id = req.getParameter("bungee");

        if (id.isEmpty() == false) {
            ObjectId objectId = new ObjectId(id);
            MN2Bungee bungee = DatabaseResource.getBungeeLoader().loadEntity(objectId);
            if (bungee != null) {
                if (bungee.getNode() != null) {
                    DockerClient dockerClient = new DockerClient("http://" + bungee.getNode().getAddress() + ":4243");
                    try {
                        dockerClient.inspectContainerCmd(bungee.getContainerId()).exec();
                        try {
                            dockerClient.killContainerCmd(bungee.getContainerId()).exec();

                            bungee.setLastUpdate(0);
                            DatabaseResource.getBungeeLoader().saveEntity(bungee);
                        } catch (Exception ex) {
                            error = "Error killing bungee container.";
                        }
                    } catch (Exception ex) {
                        if (ex instanceof NotFoundException) {
                            error = "Cannot find bungee's docker container";
                        } else {
                            error = ex.getMessage();
                        }
                    }
                } else {
                    error = "Bungee has a null node. Cannot stop.";
                }
            } else {
                error = "Bungee " + objectId.toString() + " not found";
            }
        } else {
            error = "Bungee not found";
        }


    }
}
