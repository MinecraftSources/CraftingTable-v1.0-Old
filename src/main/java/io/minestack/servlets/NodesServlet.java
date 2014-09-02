package io.minestack.servlets;

import io.minestack.DatabaseResource;
import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "NodesServlet",
        urlPatterns = {"/node/list", "/node/edit", "/node/add"})
@Log4j2
public class NodesServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseResource.initDatabase();
        log.info("Init "+this.getServletName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index/index.jsp");
        req.setAttribute("navActive", "nodes");

        if (req.getRequestURI().endsWith("list")) {
            req.setAttribute("partial", "nodes-view");
            req.setAttribute("nodes", DatabaseResource.getNodeLoader().getNodes());
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("edit")) {
            req.setAttribute("partial", "node-view");
            req.setAttribute("javascript", "node");
            req.setAttribute("action", "Edit");
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("add")) {
            req.setAttribute("partial", "node-view");
            req.setAttribute("javascript", "node");
            req.setAttribute("action", "Add");
            requestDispatcher.forward(req, resp);
        }
    }

}
