package io.minestack.servlets;

import io.minestack.DatabaseResource;
import io.minestack.db.Uranium;
import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "WorldsServlet",
        urlPatterns = {"/world/list", "/world/edit", "/world/add"})
@Log4j2
public class WorldsServlet extends HttpServlet {

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
        req.setAttribute("navActive", "worlds");

        if (req.getRequestURI().endsWith("list")) {
            req.setAttribute("partial", "worlds-view");
            req.setAttribute("worlds", Uranium.getWorldLoader().getWorlds());
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("edit")) {
            req.setAttribute("partial", "world-view");
            req.setAttribute("javascript", "world");
            req.setAttribute("action", "Edit");
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("add")) {
            req.setAttribute("partial", "world-view");
            req.setAttribute("javascript", "world");
            req.setAttribute("action", "Add");
            requestDispatcher.forward(req, resp);
        }
    }

}
