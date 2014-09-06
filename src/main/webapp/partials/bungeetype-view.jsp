<div ng-app="formApp" ng-controller="formController">
    <%
        String action = (String) request.getAttribute("action");
    %>
    <h1 class="page-header"><%=action %> Bungee</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form id="bungeeTypeForm" ng-submit="submit()" novalidate>

        <div class="form-group">
            <label for="inputName">Name</label>
            <input type="text" class="form-control" name="name" ng-pattern="/^[a-zA-Z0-9]+$/" ng-model = "bungeeType.name" id="inputName" maxlength="50" placeholder="Name" value="{{ bungeeType.name }}" required>
            <span ng-show="bungeeTypeForm.name.$error.required" class="help-block">A name is required.</span>
            <span ng-show="bungeeTypeForm.name.$error.pattern" class="help-block">Name can only contain letters and numbers.</span>
        </div>

        <div class="col-xs-12 table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Server Type</th>
                    <th>Allow Rejoin</th>
                    <th>Default</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="serverType in bungeeType.serverTypes">
                    <td>{{ serverType.name }}</td>
                    <td><input type="checkbox" ng-model="serverType.allowRejoin"></td>
                    <td><input type="checkbox" ng-model="serverType.isDefault"></td>
                    <td><button ng-click="removeServerType(serverType)" type="button" class="btn btn-danger btn-xms">
                        <span class="glyphicon glyphicon glyphicon-minus"></span>
                    </button></td>
                </tr>
                </tbody>
            </table>

            <div class="form-group">
                <div class="col-xs-6">
                    <label for="servertype">Server Type</label>
                    <select ng-model="serverType" name="serverType" id="serverType" ng-options="serverType.name for serverType in serverTypes">
                    </select>
                </div>
                <div class="col-xs-6">
                    <button ng-click="addServerType()" type="button" class="btn btn-success btn-xms">
                        <span class="glyphicon glyphicon glyphicon-plus"></span>
                    </button>
                </div>
                <span ng-show="needsServerType()" class="help-block">At least one server type is require.</span>
                <span ng-show="needsDefaultServerType()" class="help-block">No default server type selected</span>
                <span ng-show="hasManyDefaultServerTypes()" class="help-block">Only one server type can be selected as the default</span>
            </div>
        </div>

        <div class="col-xs-12 table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Plugin Name</th>
                    <th>Plugin Config</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="plugin in bungeeType.driver.plugins">
                    <td>{{ plugin.name }}</td>
                    <td>{{ plugin.config }}</td>
                    <td><button ng-click="removePlugin(plugin)" type="button" class="btn btn-danger btn-xms">
                        <span class="glyphicon glyphicon glyphicon-minus"></span>
                    </button></td>
                </tr>
                </tbody>
            </table>

            <div class="form-group">
                <div class="col-xs-4">
                    <label for="plugin">Plugin</label>
                    <select ng-model="plugin" name="plugin" id="plugin" ng-options="plugin.name for plugin in plugins">
                    </select>
                </div>
                <div class="col-xs-4">
                    <label for="pluginConfig">Config</label>
                    <select ng-model="pluginConfig" name="pluginConfig" id="pluginConfig" ng-options="pluginConfig.name for pluginConfig in plugin.configs">
                    </select>
                </div>
                <div class="col-xs-4">
                    <button ng-click="addPlugin()" type="button" class="btn btn-success btn-xms">
                        <span class="glyphicon glyphicon glyphicon-plus"></span>
                    </button>
                </div>
            </div>
        </div>

        <!--
        <div class="col-xs-12 table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Manual Server IP</th>
                    <th>Manual Server Port</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="manualServer in bungeeType.manualServers">
                    <td>{{ manualServer.ip }}</td>
                    <td>{{ manualServer.port }}</td>
                    <td><button ng-click="removeManualServer(manualServer)" type="button" class="btn btn-danger btn-xms">
                        <span class="glyphicon glyphicon glyphicon-minus"></span>
                    </button></td>
                </tr>
                </tbody>
            </table>

            <div class="form-group">
                <input type="text" ng-pattern="/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/" ng-model="manualServer.ip" name="msIP" id="msIP">

                <input type="number" ng-model="manualServer.port" name="msPort" id="msPort">
                <button ng-click="addManualServer()" type="button" class="btn btn-success btn-xms">
                    <span class="glyphicon glyphicon glyphicon-plus"></span>
                </button>
            </div>
        </div>
        -->

        <div class="col-xs-12">
            <button type="submit" class="btn btn-warning" ng-disabled="bungeeTypeForm.$invalid || needsServerType() || needsDefaultServerType() || hasManyDefaultServerTypes()">Save</button>
            <button ng-click="cancel()" type="button" class="btn btn-primary">Cancel</button>
            <%
                if (action.equalsIgnoreCase("edit")) {
            %>
                 <button ng-click="remove()" type="button" class="btn btn-danger">Delete</button>
            <%
                }
            %>
        </div>
    </form>
</div>