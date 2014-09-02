var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {
    $scope.edit = $location.path() == "/Silicon/servertype/edit";
    $scope.serverType = {};
    $scope.serverType.worlds = [];
    $scope.serverType.plugins = [];
    $scope.serverType.disabled = false;
    $scope.plugins = [];
    $scope.worlds = [];
    $scope.world = {};
    $scope.plugin = {};
    $scope.config = {};
    $scope.error = undefined;

    $scope.cancel = function() {
        $window.location.href = '/Silicon/servertype/list';
    };

    $scope.needsWorld = function () {
        if ($scope.serverType.worlds.length > 0) {
            return false;
        }
        return true;
    };

    $scope.needsDefaultWorld = function () {
        for (var i = 0; i < $scope.serverType.worlds.length; i++) {
            var world = $scope.serverType.worlds[i];
            if (world.isDefault == true) {
                return false;
            }
        }
        return true;
    };

    $scope.hasManyDefaultWorlds = function () {
        var worlds = [];
        for (var i = 0; i < $scope.serverType.worlds.length; i++) {
            var world = $scope.serverType.worlds[i];
            if (world.isDefault == true) {
                worlds.push(world);
            }
        }
        if (worlds.length > 1) {
            return true;
        }
        return false;
    };

    $scope.remove = function () {
        if ($scope.edit == true) {
            $http.delete('/Silicon/api/servertype/delete?id=' + $scope.serverType._id).success(function (data) {
                console.log(data);
                $window.location.href = '/Silicon/servertype/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.submit = function () {
        if ($scope.edit == true) {
            $http.put('/Silicon/api/servertype/save', $scope.serverType).success(function (data) {
                $window.location.href = '/Silicon/servertype/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        } else {
            $http.post('/Silicon/api/servertype/add', $scope.serverType).success(function (data) {
                $window.location.href = '/Silicon/servertype/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.addWorld = function () {
        if ($scope.world == undefined) {
            return;
        }
        for (var i = 0; i < $scope.serverType.worlds.length; i++) {
            var world = $scope.serverType.worlds[i];
            if (world._id == $scope.world._id) {
                return;
            }
        }
        var world = {
            _id: $scope.world._id,
            name: $scope.world.name
        };
        $scope.serverType.worlds.push(world);
        $scope.world = {};
    };
    $scope.removeWorld = function (world) {

        for (var i = 0; i < $scope.serverType.worlds.length; i++) {
            var worldOld = $scope.serverType.worlds[i];
            if (worldOld._id == world._id) {
                $scope.serverType.worlds.splice(i, 1);
                break;
            }
        }
    };

    $scope.addPlugin = function () {
        if ($scope.plugin == undefined) {
            return;
        }
        if (Object.keys($scope.config).length == 0) {
            if ($scope.plugin.configs.length != 0) {
                return;
            }
        }
        for (var i = 0; i < $scope.serverType.plugins.length; i++) {
            var plugin = $scope.serverType.plugins[i];
            if (plugin._id == $scope.plugin._id) {
                return;
            }
        }
        var plugin = {
            _id: $scope.plugin._id,
            name: $scope.plugin.name
        };
        if (Object.keys($scope.config).length > 0) {
            plugin._configId = $scope.config._id;
            plugin.config = $scope.config.name;
        }
        $scope.serverType.plugins.push(plugin);
        $scope.plugin = {};
        $scope.config = {};
    };
    $scope.removePlugin = function (plugin) {

        for (var i = 0; i < $scope.serverType.plugins.length; i++) {
            var pluginOld = $scope.serverType.plugins[i];
            if (pluginOld._id == plugin._id) {
                $scope.serverType.plugins.splice(i, 1);
                break;
            }
        }
    };

    if ($scope.edit == true) {


        $http.get('/Silicon/api/plugin/bukkit').success(function (data) {
            $scope.plugins = data.plugins;
            $http.get('/Silicon/api/world/all').success(function (data) {
                $scope.worlds = data.worlds;
                $http.get('/Silicon/api/servertype/one?id=' + $location.search().id).success(function (data) {
                    $scope.serverType = data;
                    for (var i = 0; i < $scope.serverType.plugins.length; i++) {
                        var typePlugin = $scope.serverType.plugins[i];
                        typePlugin.name = undefined;
                        typePlugin.config = undefined;
                        for (var j = 0; j < $scope.plugins.length; j++) {
                            var plugin = $scope.plugins[j];
                            if (plugin._id == typePlugin._id) {
                                typePlugin.name = plugin.name;
                                for (var k = 0; k < plugin.configs.length; k++) {
                                    var config = plugin.configs[k];
                                    if (config._id == typePlugin._configId) {
                                        typePlugin.config = config.name;
                                    }
                                }
                            }
                        }
                    }

                    for (var i = 0; i < $scope.serverType.worlds.length; i++) {
                        var typeWorld = $scope.serverType.worlds[i];
                        typeWorld.name = undefined;
                        for (var j = 0; j < $scope.worlds.length; j++) {
                            var world = $scope.worlds[j];
                            if (world._id == typeWorld._id) {
                                typeWorld.name = world.name;
                            }
                        }
                    }

                }).error(function (error) {
                    $scope.error = error.error;
                });
            }).error(function (error) {
                $scope.error = error.error;
            });
        }).error(function(error) {
            $scope.error = error.error;
        });
    } else {
        $http.get('/Silicon/api/plugin/bukkit').success(function (data) {
            $scope.plugins = data.plugins;
        }).error(function (error) {
            $scope.error = error.error;
        });

        $http.get('/Silicon/api/world/all').success(function (data) {
            $scope.worlds = data.worlds;
        }).error(function (error) {
            $scope.error = error.error;
        });

    }

}]);