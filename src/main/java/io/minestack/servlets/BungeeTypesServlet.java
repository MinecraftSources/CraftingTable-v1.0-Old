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
        name = "BungeeTypesServlet",
        urlPatterns = {"/bungeetype/list", "/bungeetype/edit", "/bungeetype/add"})
@Log4j2
public class BungeeTypesServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            DatabaseResource.initDatabase();
        } catch (Exception e) {
            new ServerTypesServlet();
        }
        log.info("Init "+this.getServletName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index/index.jsp");
        req.setAttribute("navActive", "bt");

        if (req.getRequestURI().endsWith("list")) {
            req.setAttribute("partial", "bungeetypes-view");
            req.setAttribute("bungeetypes", Uranium.getBungeeTypeLoader().getTypes());
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("edit")) {
            req.setAttribute("partial", "bungeetype-view");
            req.setAttribute("javascript", "bungeeType");
            req.setAttribute("action", "Edit");
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("add")) {
            req.setAttribute("partial", "bungeetype-view");
            req.setAttribute("javascript", "bungeeType");
            req.setAttribute("action", "Add");
            requestDispatcher.forward(req, resp);
        }
    }

}
