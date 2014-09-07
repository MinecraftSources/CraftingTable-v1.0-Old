<div ng-app="formApp" ng-controller="formController">
    <h1 class="page-header">Manage Bungee</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form id="bungeeForm" ng-submit="stop()">

        <div class="form-group">
            <label for="id">ID</label>
            <input type="text" class="form-control" name="id" id="id" ng-model="bungee._id" disabled>
        </div>

        <div class="form-group">
            <label for="type">Bungee Type</label>
            <input type="text" class="form-control" name="type" id="type" ng-model="bungee._proxytype.name" disabled>
        </div>

        <div class="form-group">
            <label for="node">Node</label>
            <input type="text" class="form-control" name="node" id="node" ng-model="bungee._node.address" disabled>
        </div>

        <div class="col-xs-12">
            <button type="button" ng-click="cancel()" class="btn btn-primary">Cancel</button>
            <button type="submit" class="btn btn-danger">Stop</button>
        </div>
    </form>
</div>