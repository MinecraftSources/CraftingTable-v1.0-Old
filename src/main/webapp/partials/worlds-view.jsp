<%@ page import="io.minestack.db.entity.MN2World" %>
<%@ page import="java.util.ArrayList" %>
<h1 class="page-header">Worlds</h1>

<div>
    <a href="${pageContext.request.contextPath}/world/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add World</button></a>
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
            ArrayList<MN2World> worlds = (ArrayList<MN2World>) request.getAttribute("worlds");
            for (MN2World world : worlds) {
        %>
        <tr>
            <td><%=world.getName() %></td>
            <td><%=world.get_id().toString() %></td>
            <td><a href="${pageContext.request.contextPath}/world/edit?id=<%=world.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
    </div>
</div>