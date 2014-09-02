package io.minestack.servlets;

import io.minestack.DatabaseResource;
import io.minestack.db.Uranium;
import io.minestack.db.entity.UBungee;
import io.minestack.db.entity.UNode;
import io.minestack.db.entity.UServer;
import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(
        name = "DashboardServlet",
        urlPatterns = {"/index"})
@Log4j2
public class DashboardServlet extends HttpServlet {

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
        req.setAttribute("navActive", "home");
        req.setAttribute("partial", "dashboard-view");

        int onlinePlayers = 0;
        int maxPlayers = 0;

        int maxMemory = 0;
        int usedMemory = 0;

        ArrayList<UNode> nodes = Uranium.getNodeLoader().getNodes();
        for (UNode node : nodes) {
            maxMemory += node.getRam();
        }

        req.setAttribute("maxMemory", maxMemory);

        int totalNodes = nodes.size();
        int onlineNodes = Uranium.getNodeLoader().getOnlineNodes().size();

        req.setAttribute("onlineNodes", onlineNodes);
        req.setAttribute("totalNodes", totalNodes);

        ArrayList<UServer> servers = Uranium.getServerLoader().getServers();

        for (UServer server : servers) {
            onlinePlayers += server.getPlayers().size();
            if (server.getServerType() != null) {
                maxPlayers += server.getServerType().getPlayers();
                usedMemory += server.getServerType().getMemory();
            }
        }

        ArrayList<UBungee> bungees = Uranium.getBungeeLoader().getBungees();

        req.setAttribute("bungees", bungees);
        req.setAttribute("servers", servers);
        req.setAttribute("onlinePlayers", onlinePlayers);
        req.setAttribute("maxPlayers", maxPlayers);
        req.setAttribute("usedMemory", usedMemory);

        requestDispatcher.forward(req, resp);
    }
}
