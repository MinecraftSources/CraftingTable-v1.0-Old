<div ng-app="formApp" ng-controller="formController">
    <h1 class="page-header">Manage Server</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form id="bungeeForm" ng-submit="stop()">

        <div class="form-group">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" ng-model="server._id" disabled>
        </div>

        <div class="form-group">
            <label for="type">Bungee Type</label>
            <input type="text" class="form-control" id="type" ng-model="server._servertype.name" disabled>
        </div>

        <div class="form-group">
            <label for="node">Node</label>
            <input type="text" class="form-control" id="node" ng-model="server._node.address" disabled>
        </div>

        <div class="form-group">
            <label for="port">Port</label>
            <input type="text" class="form-control" id="port" ng-model="server.port" disabled>
        </div>

        <div class="form-group">
            <label for="number">Number</label>
            <input type="text" class="form-control" id="number" ng-model="server.number" disabled>
        </div>

        <div class="col-xs-12">
            <button type="button" ng-click="cancel()" class="btn btn-primary">Cancel</button>
            <button type="submit" class="btn btn-danger">Stop</button>
        </div>
    </form>
</div>