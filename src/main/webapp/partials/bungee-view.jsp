<h1 class="page-header">Manage Bungee</h1>

<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
<div class="alert alert-danger"><%=error %></div>
<%
    }
%>

<form enctype="application/x-www-form-urlencoded" method="POST">

    <div class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" name="id" id="id" value="${id}" disabled>
    </div>

    <div class="form-group">
        <label for="type">Bungee Type</label>
        <input type="text" class="form-control" name="type" id="type" value="${type}" disabled>
    </div>

    <div class="form-group">
        <label for="node">Node</label>
        <input type="text" class="form-control" name="node" id="node" value="${node}" disabled>
    </div>

    <div class="col-xs-12">
        <a href="${pageContext.request.contextPath}/mn2"><button type="button" class="btn btn-primary">Cancel</button></a>
        <button type="submit" class="btn btn-danger">Stop</button>
    </div>
</form>