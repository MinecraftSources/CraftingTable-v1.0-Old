<%@ page import="java.util.ArrayList" %>
<%@ page import="com.rmb938.mn2.docker.db.entity.MN2World" %>
<h1 class="page-header">Worlds</h1>

<div>
    <a href="${pageContext.request.contextPath}/mn2/world/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add World</button></a>
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
            ArrayList<MN2World> worlds = (ArrayList<MN2World>) request.getAttribute("worlds");
            for (MN2World world : worlds) {
        %>
        <tr>
            <td><%=world.getName() %></td>
            <td><%=world.get_id().toString() %></td>
            <td><a href="${pageContext.request.contextPath}/mn2/world/edit?id=<%=world.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>