<%@ page import="com.rmb938.mn2.docker.db.entity.MN2Server" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.rmb938.mn2.docker.db.entity.MN2Bungee" %>
<h1 class="page-header">Dashboard</h1>

<div class="row stats">
    <div class="col-xs-6 col-sm-3">
        <h4>Online Players</h4>
        <span class="text-muted">${onlinePlayers} / ${maxPlayers}</span>
    </div>
    <div class="col-xs-6 col-sm-3">
        <h4>Online Nodes</h4>
        <span class="text-muted">${onlineNodes} / ${totalNodes}</span>
    </div>
    <div class="col-xs-6 col-sm-3">
        <h4>Memory Usage</h4>
        <span class="text-muted">${usedMemory} MB / ${maxMemory} MB</span>
    </div>
    <div class="col-xs-6 col-sm-3">
        <h4>Label</h4>
        <span class="text-muted">Something else</span>
    </div>
</div>
<h2 class="sub-header">Running Bungees</h2>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Bungee Type</th>
            <th>Node</th>
            <th>Manage</th>
        </tr>
        </thead>
        <tbody>

        <%

            ArrayList<MN2Bungee> bungees = (ArrayList<MN2Bungee>) request.getAttribute("bungees");

            for (MN2Bungee bungee : bungees) {
                if (bungee.getLastUpdate() >= System.currentTimeMillis()-60000) {
        %>
        <%
                    if (bungee.getBungeeType() != null) {
        %>
                        <td><%=bungee.getBungeeType().getName() %></td>
        <%
                    }
                    if (bungee.getNode() != null) {
        %>
                        <td><%=bungee.getNode().getAddress() %></td>
        <%
                    }
        %>
                     <td><a href="${pageContext.request.contextPath}/mn2/bungee/manage?bungee=<%=bungee.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                            <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
        <%
                }
            }
        %>
        </tbody>
    </table>
</div>

<h2 class="sub-header">Running Servers</h2>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Number</th>
            <th>Server Type</th>
            <th>Players</th>
            <th>Node:Port</th>
            <th>Manage</th>
        </tr>
        </thead>
        <tbody>

        <%

            ArrayList<MN2Server> servers = (ArrayList<MN2Server>) request.getAttribute("servers");

            for (MN2Server server : servers) {
                if (server.getLastUpdate() >= System.currentTimeMillis()-60000) {
        %>
        <tr>
                    <td><%=server.getNumber() %></td>
                    <td><%=server.getServerType().getName() %></td>
                    <td><%=server.getPlayers().size() %></td>
            <%
                        if (server.getNode() != null) {
            %>
                            <td><%=server.getNode().getAddress() %>:<%=server.getPort() %></td>
            <%
                        } else {
            %>
                            <td>:<%=server.getPort() %></td>
            <%
                        }
            %>
                    <td><button type="button" class="btn btn-default btn-xs">
                        <span class="glyphicon glyphicon glyphicon-pencil"></span></button></td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
</div>