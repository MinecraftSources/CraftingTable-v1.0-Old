<%@ page import="io.minestack.db.entity.DCServerType" %>
<%@ page import="java.util.ArrayList" %>
<h1 class="page-header">Server Types</h1>

<div>
    <a href="${pageContext.request.contextPath}/servertype/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add Server Type</button></a>
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
            ArrayList<DCServerType> servertypes = (ArrayList<DCServerType>) request.getAttribute("servertypes");
            for (DCServerType serverType : servertypes) {
        %>
        <tr>
            <td><%=serverType.getName() %></td>
            <td><%=serverType.get_id().toString() %></td>
            <td><a href="${pageContext.request.contextPath}/servertype/edit?id=<%=serverType.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
    </div>
</div>