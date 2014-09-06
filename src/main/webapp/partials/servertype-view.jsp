<div ng-app="formApp" ng-controller="formController">
    <%
        String action = (String) request.getAttribute("action");
    %>
    <h1 class="page-header"><%=action %> Server Type</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form name="serverTypeForm" ng-submit="submit()" novalidate>
        <div class="form-group">
            <label for="inputName">Name</label>
            <input type="text" class="form-control" name="name" ng-pattern="/^[a-zA-Z0-9]+$/" ng-model = "serverType.name" id="inputName" maxlength="50" placeholder="Name" value="{{ serverType.name }}" required>
            <span ng-show="serverTypeForm.name.$error.required" class="help-block">A server type name is required.</span>
            <span ng-show="serverTypeForm.name.$error.pattern" class="help-block">Name can only contain letters and numbers.</span>
        </div>
        <div class="form-group">
            <label for="inputPlayers">Players</label>
            <input type="number" class="form-control" name="players" min="1" ng-model = "serverType.players" id="inputPlayers" placeholder="Players" value="{{ serverType.players }}" required>
            <span ng-show="serverTypeForm.players.$error.required" class="help-block">Number of players is required.</span>
            <span ng-show="serverTypeForm.players.$error.min" class="help-block">A minimum of 1 player is required.</span>
        </div>
        <div class="form-group">
            <label for="inputMemory">Memory (MB)</label>
            <input type="number" class="form-control" name="memory" min="512" ng-model = "serverType.memory" id="inputMemory" placeholder="Memory" value="{{ serverType.memory }}" required>
            <span ng-show="serverTypeForm.memory.$error.required" class="help-block">Memory amount is required.</span>
            <span ng-show="serverTypeForm.memory.$error.min" class="help-block">A minimum of 512MB of memory is required.</span>
        </div>
        <div class="form-group">
            <label for="inputNumber">Amount</label>
            <input type="number" class="form-control" name="number" min="1" ng-model = "serverType.amount" id="inputNumber" placeholder="Amount of Servers" value="{{ serverType.amount }}" required>
            <span ng-show="serverTypeForm.number.$error.required" class="help-block">Amount of servers is required.</span>
            <span ng-show="serverTypeForm.number.$error.min" class="help-block">A amount of 1 server is required.</span>
        </div>
        <div class="form-group">
            <label for="inputDisabled">Disable Server Type</label>
            <input name="disabled" id="inputDisabled" type="checkbox" ng-model="serverType.disabled">
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
                <tr ng-repeat="plugin in serverType.driver.plugins">
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
                    <select ng-model="config" name="pluginConfig" id="pluginConfig" ng-options="config.name for config in plugin.configs">
                    </select>
                </div>
                <div class="col-xs-4">
                    <button ng-click="addPlugin()" type="button" class="btn btn-success btn-xms">
                        <span class="glyphicon glyphicon glyphicon-plus"></span>
                    </button>
                </div>
            </div>
        </div>
        <div class="col-xs-12 table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>World Name</th>
                    <th>Default</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="world in serverType.worlds">
                    <td>{{ world.name }}</td>
                    <td><input type="checkbox" ng-model="world.isDefault"></td>
                    <td><button ng-click="removeWorld(world)" type="button" class="btn btn-danger btn-xms">
                        <span class="glyphicon glyphicon glyphicon-minus"></span>
                    </button></td>
                </tr>
                </tbody>
            </table>

            <div class="form-group">
                <div class="col-xs-6">
                    <label for="world">World</label>
                    <select ng-model="world" name="world" id="world" ng-options="world.name for world in worlds">
                    </select>
                </div>
                <div class="col-xs-6">
                    <button ng-click="addWorld()" type="button" class="btn btn-success btn-xms">
                        <span class="glyphicon glyphicon glyphicon-plus"></span>
                    </button>
                </div>
            </div>
            <span ng-show="needsWorld()" class="help-block">At least one world is required</span>
            <span ng-show="needsDefaultWorld()" class="help-block">No default world selected</span>
            <span ng-show="hasManyDefaultWorlds()" class="help-block">Only one world can be selected as the default</span>
        </div>
        <div class="col-xs-12">
            <button type="submit" class="btn btn-warning" ng-disabled="serverTypeForm.$invalid || needsWorld() || needsDefaultWorld() || hasManyDefaultWorlds()">Save</button>
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