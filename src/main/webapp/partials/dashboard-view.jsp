<h1 class="page-header">Dashboard</h1>

<div class="row placeholders">
    <div class="col-xs-6 col-sm-3">
        <h4>Online Players</h4>
        <span class="text-muted">{{ players.length }} / {{ maxPlayers }}</span>
    </div>
    <div class="col-xs-6 col-sm-3">
        <h4>Online Nodes</h4>
        <span class="text-muted">{{ onlineNodes.length }} / {{ nodes.length }}</span>
    </div>
    <div class="col-xs-6 col-sm-3">
        <h4>Memory Usage</h4>
        <span class="text-muted">{{ usedMemory }} MB / {{ maxMemory }} MB</span>
    </div>
    <div class="col-xs-6 col-sm-3">
        <h4>Label</h4>
        <span class="text-muted">Something else</span>
    </div>
</div>
<h2 class="sub-header">Running Bungees</h2>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Bungee Type</th>
            <th>Node</th>
            <th>Manage</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="bungee in bungees">
            <td>{{ bungee.type }}</td>
            <td>{{ bungee.node }}</td>
            <td><button ng-click="editBungee(bungee._id)" type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></td>
        </tr>
        </tbody>
    </table>
</div>

<h2 class="sub-header">Running Servers</h2>

<div class="table-responsive">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Number</th>
            <th>Server Type</th>
            <th>Players</th>
            <th>Node:Port</th>
            <th>Manage</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="server in servers">
            <td>{{ server.number }}</td>
            <td>{{ server.type }}</td>
            <td>{{ server.players.length }}</td>
            <td>{{ server.node }}:{{ server.port }}</td>
            <td><button ng-click="editServer(server._id)" type="button" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon glyphicon-pencil"></span></button></td>
        </tr>
        </tbody>
    </table>
</div>