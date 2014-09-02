<%@ page import="io.minestack.db.entity.MN2Node" %>
<%@ page import="java.util.ArrayList" %>
<h1 class="page-header">Nodes</h1>

<div>
    <a href="${pageContext.request.contextPath}/node/add">
        <button type="button" class="btn btn-primary btn-lg">
            <span class="glyphicon glyphicon glyphicon-plus"></span>Add Node
        </button>
    </a>

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
                ArrayList<MN2Node> nodes = (ArrayList<MN2Node>) request.getAttribute("nodes");
                for (MN2Node node : nodes) {
            %>
            <tr>
                <td><%=node.getAddress() %>
                </td>
                <td><%=node.get_id().toString() %>
                </td>
                <td><a href="${pageContext.request.contextPath}/node/edit?id=<%=node.get_id().toString() %>">
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
</div>