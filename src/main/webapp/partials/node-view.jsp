<div ng-app="formApp" ng-controller="formController">
    <%
        String action = (String) request.getAttribute("action");
    %>
    <h1 class="page-header"><%=action %> Node</h1>

    <div ng-show="error != undefined" id="error-group" class="alert alert-danger">{{ error }}</div>

    <form name="nodeForm," ng-submit="submit()" novalidate>
        <div class="form-group">
            <label for="inputHost">Private IP Address</label>
            <input type="text" class="form-control" ng-disabled="edit" name="host" ng-pattern="/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/" ng-model = "node.host" id="inputHost" placeholder="127.0.0.1" value="{{ node.host }}" required>
            <span ng-show="nodeForm.host.$error.required" class="help-block">A node IP is required.</span>
            <span ng-show="nodeForm.host.$error.pattern" class="help-block">You must enter a valid IP address</span>
        </div>

        <div class="form-group">
            <label for="inputRam">Usable RAM (MB)</label>
            <input type="number" class="form-control" name="ram" min="2048" ng-model = "node.ram" id="inputRAM" placeholder="2048" value="{{ node.ram }}" required>
            <span ng-show="nodeForm.ram.$error.required" class="help-block">Usable RAM is required</span>
            <span ng-show="nodeForm.ram.$error.min" class="help-block">A minimum of 2048MB of usable ram is required.</span>
        </div>

        <div class="form-group">
            <label for="bungeetype">Bungee Type</label>
            <select ng-model="bungeeType" name="bungeetype" id="bungeetype" ng-options="bungeetype.name for bungeetype in bungeetypes">
                <option value=""></option>
            </select>
        </div>

        <div class="form-group">
            <button ng-hide="!edit" ng-click="doNodeAction()" type="button" class="btn btn-warning">{{ nodeAction() }}</button>
        </div>

        <div class="col-xs-12">
            <button type="submit" class="btn btn-warning" ng-disabled="nodeForm.$invalid">Save</button>
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