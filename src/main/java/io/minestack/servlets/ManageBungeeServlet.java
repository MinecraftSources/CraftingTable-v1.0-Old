package io.minestack.servlets;

import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ManageBungeeServlet",
        urlPatterns = {"/bungee/manage"})
@Log4j2
public class ManageBungeeServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        log.info("Init "+this.getServletName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index/index.jsp");
        req.setAttribute("navActive", "home");
        req.setAttribute("partial", "bungee-view");
        req.setAttribute("javascript", "bungee");

        requestDispatcher.forward(req, resp);
    }

}
