package com.rmb938.mn2.docker.servlets;

import com.rmb938.mn2.docker.DatabaseResource;
import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "PluginsServlet",
        urlPatterns = {"/mn2/plugin/list", "/mn2/plugin/edit", "/mn2/plugin/add"})
@Log4j2
public class PluginsServlet extends HttpServlet {

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

        if (req.getRequestURI().endsWith("list")) {
            req.setAttribute("partial", "plugins-view");
            req.setAttribute("plugins", DatabaseResource.getPluginLoader().loadPlugins());
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("edit")) {
            req.setAttribute("partial", "plugin-view");
            req.setAttribute("javascript", "plugin");
            req.setAttribute("action", "Edit");
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("add")) {
            req.setAttribute("partial", "plugin-view");
            req.setAttribute("javascript", "plugin");
            req.setAttribute("action", "Add");
            requestDispatcher.forward(req, resp);
        }
    }

}
