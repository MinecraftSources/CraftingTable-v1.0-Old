<%@ page import="com.rmb938.mn2.docker.db.entity.MN2ServerType" %>
<%@ page import="java.util.ArrayList" %>
<h1 class="page-header">Server Types</h1>

<div>
    <a href="${pageContext.request.contextPath}/mn2/servertype/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add Server Type</button></a>
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
            ArrayList<MN2ServerType> servertypes = (ArrayList<MN2ServerType>) request.getAttribute("servertypes");
            for (MN2ServerType serverType : servertypes) {
        %>
        <tr>
            <td><%=serverType.getName() %></td>
            <td><%=serverType.get_id().toString() %></td>
            <td><a href="${pageContext.request.contextPath}/mn2/servertype/edit?id=<%=serverType.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>