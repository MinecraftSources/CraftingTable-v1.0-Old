<div ng-app="formApp" ng-controller="formController">

    <%
        String action = (String) request.getAttribute("action");
    %>
    <h1 class="page-header"><%=action %> World</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form name="worldForm" ng-submit="submit()" novalidate>
        <div class="form-group">
            <label for="inputName">Name</label>
            <input type="text" class="form-control" name="name" ng-pattern="/^[a-zA-Z0-9]+$/" ng-model = "world.name" id="inputName" maxlength="50" placeholder="Name" value="{{ world.name }}" required>
            <span ng-show="worldForm.name.$error.required" class="help-block">A world name is required.</span>
            <span ng-show="worldForm.name.$error.pattern" class="help-block">Name can only container letters and numbers.</span>
        </div>
        <div class="form-group">
            <label for="inputFolder">Folder</label>
            <input type="text" class="form-control" name="folder" ng-pattern="/^[a-zA-Z0-9\/]+$/" ng-model = "world.folder" id="inputFolder" placeholder="Folder" value="{{ world.folder }}" required>
            <span ng-show="worldForm.folder.$error.required" class="help-block">A world folder is required.</span>
            <span ng-show="worldForm.folder.$error.pattern" class="help-block">Folder can only container letters, numbers and forward slashes.</span>
        </div>
        <div class="form-group">
            <label for="environment">Environment</label>
            <select class="form-control" ng-model="world.environment" name="environment" id="environment" required>
                <option>NORMAL</option>
                <option>NETHER</option>
                <option>THE_END</option>
            </select>
            <span ng-show="worldForm.environment.$error.required" class="help-block">A world environment is required.</span>
        </div>
        <div class="form-group">
            <label for="inputGenerator">Generator</label>
            <input type="text" class="form-control" name="generator" ng-pattern="/^[a-zA-Z0-9]+$/" ng-model = "world.generator" id="inputGenerator" maxlength="50" placeholder="Generator" value="{{ world.generator }}">
            <span ng-show="worldForm.name.$error.pattern" class="help-block">Generator can only container letters and numbers.</span>
        </div>

        <div class="col-xs-12">
            <button type="submit" class="btn btn-warning" ng-disabled="worldForm.$invalid">Save</button>
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