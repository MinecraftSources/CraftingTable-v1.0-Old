<div ng-app="formApp" ng-controller="formController">
    <h1 class="page-header">Manage Bungee</h1>

    <div id="error-group" class="alert alert-danger"></div>

    <form id="bungeeForm" method="POST">

        <div class="form-group">
            <label for="id">ID</label>
            <input type="text" class="form-control" name="id" id="id" ng-model="bungee._id" disabled>
        </div>

        <div class="form-group">
            <label for="type">Bungee Type</label>
            <input type="text" class="form-control" name="type" id="type" ng-model="bungee.type.name" disabled>
        </div>

        <div class="form-group">
            <label for="node">Node</label>
            <input type="text" class="form-control" name="node" id="node" ng-model="bungee.node.address" disabled>
        </div>

        <div class="col-xs-12">
            <a href="${pageContext.request.contextPath}/mn2"><button type="button" class="btn btn-primary">Cancel</button></a>
            <button type="submit" class="btn btn-danger">Stop</button>
        </div>
    </form>
</div>