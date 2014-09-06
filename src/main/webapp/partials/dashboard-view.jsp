<%@ page import="io.minestack.db.entity.proxy.DCProxy" %>
<%@ page import="io.minestack.db.entity.server.DCServer" %>
<%@ page import="java.util.ArrayList" %>
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

            ArrayList<DCProxy> proxies = (ArrayList<DCProxy>) request.getAttribute("proxies");

            for (DCProxy proxy : proxies) {
                if (proxy.getLastUpdate() == 0) {
                    continue;
                }
        %>
        <tr>
        <%
                    if (proxy.getProxyType() != null) {
        %>
                        <td><%=proxy.getProxyType().getName() %></td>
        <%
                    } else {
        %>
                        <td>NULL</td>
        <%
                    }
                    if (proxy.getNode() != null) {
        %>
                        <td><%=proxy.getNode().getAddress() %></td>
        <%
                    } else {
        %>
                         <td>NULL</td>
        <%
                    }
        %>
        <td><a href="${pageContext.request.contextPath}/bungee/manage?id=<%=proxy.get_id().toString() %>">
            <button type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button>
        </a></td>
        </tr>
        <%
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

            ArrayList<DCServer> servers = (ArrayList<DCServer>) request.getAttribute("servers");

            for (DCServer server : servers) {
                if (server.getLastUpdate() == 0) {
                    continue;
                }
        %>
                <tr>
                    <td><%=server.getNumber() %></td>
            <%
                if (server.getServerType() != null) {
            %>
                    <td><%=server.getServerType().getName() %></td>
            <%
                } else {
            %>
                    <td>NULL</td>
            <%
                }
            %>
                    <td><%=server.getPlayers().size() %></td>
            <%
                        if (server.getNode() != null) {
            %>
                            <td><%=server.getNode().getAddress() %>:<%=server.getPort() %></td>
            <%
                        } else {
            %>
                            <td>NULL:<%=server.getPort() %></td>
            <%
                        }
            %>
                    <td><a href="${pageContext.request.contextPath}/server/manage?id=<%=server.get_id().toString() %>">
                        <button type="button" class="btn btn-default btn-xs">
                            <span class="glyphicon glyphicon glyphicon-pencil"></span></button>
                    </a></td>
                </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>