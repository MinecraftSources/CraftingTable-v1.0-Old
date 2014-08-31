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
        name = "ServerTypesServlet",
        urlPatterns = {"/mn2/servertype/list", "/mn2/servertype/edit", "/mn2/servertype/add"})
@Log4j2
public class ServerTypesServlet extends HttpServlet {

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
        req.setAttribute("navActive", "st");

        if (req.getRequestURI().endsWith("list")) {
            req.setAttribute("partial", "servertypes-view");
            req.setAttribute("servertypes", DatabaseResource.getServerTypeLoader().getTypes());
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("edit")) {
            req.setAttribute("partial", "servertype-view");
            req.setAttribute("javascript", "serverType");
            req.setAttribute("action", "Edit");
            requestDispatcher.forward(req, resp);
        } else if (req.getRequestURI().endsWith("add")) {
            req.setAttribute("partial", "servertype-view");
            req.setAttribute("javascript", "serverType");
            req.setAttribute("action", "Add");
            requestDispatcher.forward(req, resp);
        }
    }

}
