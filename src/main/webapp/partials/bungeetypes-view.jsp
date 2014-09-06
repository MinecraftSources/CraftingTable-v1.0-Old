<%@ page import="java.util.ArrayList" %>
<%@ page import="io.minestack.db.entity.proxy.DCProxyType" %>
<h1 class="page-header">Proxy Types</h1>

<div>
    <a href="${pageContext.request.contextPath}/bungeetype/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add Bungee Type</button></a>
    <div class="table-responsive">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>ID</th>
            <th>Edit</th>
        </tr>
        </thead>
        <tbody>
        <%
            ArrayList<DCProxyType> proxytypes = (ArrayList<DCProxyType>) request.getAttribute("proxytypes");
            for (DCProxyType proxyType : proxytypes) {
        %>
            <tr>
                <td><%=proxyType.getName() %></td>
                <td><%=proxyType.get_id().toString() %></td>
                <td><a href="${pageContext.request.contextPath}/bungeetype/edit?id=<%=proxyType.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                    <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>
    </div>
</div>