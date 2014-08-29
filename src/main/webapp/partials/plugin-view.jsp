<div ng-app="formApp" ng-controller="formController">
    <%
        String action = (String) request.getAttribute("action");
    %>
    <h1 class="page-header"><%=action %> Plugin</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form name="pluginForm" ng-submit="submit()" novalidate>
        <div class="form-group">
            <label for="inputName">Name</label>
            <input type="text" class="form-control" name="name" ng-pattern="/^[a-zA-Z0-9]+$/" ng-model="plugin.name"
                   id="inputName" maxlength="50" placeholder="Name" value="{{ plugin.name }}" required>
            <span ng-show="pluginForm.name.$error.required" class="help-block">A plugin name is required.</span>
            <span ng-show="pluginForm.name.$error.pattern" class="help-block">Name can only container letters and numbers.</span>
        </div>
        <div class="form-group">
            <label>Plugin Type</label>
            <br>
            <input ng-disabled="edit" type="radio" name="type" ng-model="plugin.type" value="BUKKIT" required> Bukkit
            <br>
            <input ng-disabled="edit" type="radio" name="type" ng-model="plugin.type" value="BUNGEE" required> Bungee
            <span ng-show="pluginForm.type.$error.required" class="help-block">A plugin type is required.</span>
        </div>
        <div class="form-group">
            <label for="inputBaseFolder">Base Folder</label>
            <input class="form-control" type="text" ng-pattern="/^[a-zA-Z0-9\/]+$/" ng-model="plugin.baseFolder" name="basefolder" id="inputBaseFolder"
                   placeholder="pluginFolder">
            <span ng-show="pluginForm.folder.$error.required"
                  class="help-block">A plugin base folder is required.</span>
        </div>
        <div class="form-group">
            <label for="inputFolder">Config Folder Name</label>
            <input type="text" class="form-control" name="folder" ng-pattern="/^[a-zA-Z0-9]+$/"
                   ng-model="plugin.configFolder" id="inputFolder" placeholder="Config Folder Name"
                   value="{{ plugin.folder }}" required>
            <span ng-show="pluginForm.folder.$error.required"
                  class="help-block">A plugin folder name is required.</span>
            <span ng-show="pluginForm.git.$error.pattern" class="help-block">A valid folder name is required.</span>
        </div>

        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Config Name</th>
                    <th>Config Folder Location</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="config in plugin.configs">
                    <td>{{ config.name }}</td>
                    <td>{{ config.location }}</td>
                    <td>
                        <button ng-click="removeConfig(config)" type="button" class="btn btn-danger btn-xms">
                            <span class="glyphicon glyphicon glyphicon-minus"></span>
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="form-group">
                <input type="text" ng-pattern="/^[a-zA-Z0-9]+$/" ng-model="config.name" name="cName" id="cName"
                       placeholder="confg 1">
                <input type="text" ng-pattern="/^[a-zA-Z0-9\/]+$/" ng-model="config.location" name="cName" id="cfLocation"
                       placeholder="confgFolder">
                <button ng-click="addConfig()" type="button" class="btn btn-success btn-xms">
                    <span class="glyphicon glyphicon glyphicon-plus"></span>
                </button>
            </div>

        </div>
        <div class="col-xs-12">
            <button type="submit" class="btn btn-warning" ng-disabled="pluginForm.$invalid">Save</button>
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