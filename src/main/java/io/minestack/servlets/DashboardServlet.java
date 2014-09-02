package io.minestack.servlets;

import io.minestack.DatabaseResource;
import io.minestack.db.entity.MN2Bungee;
import io.minestack.db.entity.MN2Node;
import io.minestack.db.entity.MN2Server;
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
        DatabaseResource.initDatabase();
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

        ArrayList<MN2Node> nodes = DatabaseResource.getNodeLoader().getNodes();
        for (MN2Node node : nodes) {
            maxMemory += node.getRam();
        }

        req.setAttribute("maxMemory", maxMemory);

        int totalNodes = nodes.size();
        int onlineNodes = DatabaseResource.getNodeLoader().getOnlineNodes().size();

        req.setAttribute("onlineNodes", onlineNodes);
        req.setAttribute("totalNodes", totalNodes);

        ArrayList<MN2Server> servers = DatabaseResource.getServerLoader().getServers();

        for (MN2Server server : servers) {
            onlinePlayers += server.getPlayers().size();
            if (server.getServerType() != null) {
                maxPlayers += server.getServerType().getPlayers();
                usedMemory += server.getServerType().getMemory();
            }
        }

        ArrayList<MN2Bungee> bungees = DatabaseResource.getBungeeLoader().getBungees();

        req.setAttribute("bungees", bungees);
        req.setAttribute("servers", servers);
        req.setAttribute("onlinePlayers", onlinePlayers);
        req.setAttribute("maxPlayers", maxPlayers);
        req.setAttribute("usedMemory", usedMemory);

        requestDispatcher.forward(req, resp);
    }
}
