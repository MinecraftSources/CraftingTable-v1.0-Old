<%@ page import="com.rmb938.mn2.docker.db.entity.MN2BungeeType" %>
<%@ page import="java.util.ArrayList" %>
<h1 class="page-header">Bungee Types</h1>

<div>
    <a href="${pageContext.request.contextPath}/mn2/bungeetype/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add Bungee Type</button></a>
    <table class="table-responsive table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>ID</th>
            <th>Edit</th>
        </tr>
        </thead>
        <tbody>
        <%
            ArrayList<MN2BungeeType> bungeetypes = (ArrayList<MN2BungeeType>) request.getAttribute("bungeetypes");
            for (MN2BungeeType bungeeType : bungeetypes) {
        %>
            <tr>
                <td><%=bungeeType.getName() %></td>
                <td><%=bungeeType.get_id().toString() %></td>
                <td><a href="${pageContext.request.contextPath}/mn2/bungeetype/edit?id=<%=bungeeType.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                    <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>