<%@ page import="io.minestack.db.entity.MN2Plugin" %>
<%@ page import="java.util.ArrayList" %>
<h1 class="page-header">Plugins</h1>

<div>
    <a href="${pageContext.request.contextPath}/plugin/add"><button type="button" class="btn btn-primary btn-lg">
        <span class="glyphicon glyphicon glyphicon-plus"></span>Add Plugin</button></a>
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
            ArrayList<MN2Plugin> plugins = (ArrayList<MN2Plugin>) request.getAttribute("plugins");
            for (MN2Plugin plugin : plugins) {
        %>
        <tr>
            <td><%=plugin.getName() %></td>
            <td><%=plugin.get_id().toString() %></td>
            <td><a href="${pageContext.request.contextPath}/plugin/edit?id=<%=plugin.get_id().toString() %>"><button type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></a></td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
    </div>
</div>