var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {
    $scope.edit = $location.path() == "/CraftingTable/bungeetype/edit";
    $scope.bungeeType = {};
    $scope.bungeeType.serverTypes = [];
    $scope.bungeeType.driver = {};
    $scope.bungeeType.driver.driverName = "bungeeType";
    $scope.bungeeType.driver.plugins = [];
    $scope.serverType = {};
    $scope.plugin = {};
    $scope.pluginConfig = {};
    $scope.plugins = [];
    $scope.serverTypes = {};
    $scope.error = undefined;

    $scope.cancel = function() {
        $window.location.href = '/CraftingTable/bungeetype/list';
    };

    $scope.remove = function () {
        if ($scope.edit == true) {
            $http.delete('/CraftingTable/api/bungeetype/delete?id=' + $scope.bungeeType._id).success(function (data) {
                $window.location.href = '/CraftingTable/bungeetype/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.submit = function () {
        if ($scope.edit == true) {
            $http.put('/CraftingTable/api/bungeetype/save', $scope.bungeeType).success(function (data) {
                $window.location.href = '/CraftingTable/bungeetype/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        } else {
            $http.post('/CraftingTable/api/bungeetype/add', $scope.bungeeType).success(function (data) {
                $window.location.href = '/CraftingTable/bungeetype/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.hasManyDefaultServerTypes = function () {
        var servertypes = [];
        for (var i = 0; i < $scope.bungeeType.serverTypes.length; i++) {
            var servertype = $scope.bungeeType.serverTypes[i];
            if (servertype.isDefault == true) {
                servertypes.push(servertype);
            }
        }
        if (servertypes.length > 1) {
            return true;
        }
        return false;
    };

    $scope.needsServerType = function () {
        return $scope.bungeeType.serverTypes.length == 0;
    };

    $scope.needsDefaultServerType = function () {
        for (var i = 0; i < $scope.bungeeType.serverTypes.length; i++) {
            var servertype = $scope.bungeeType.serverTypes[i];
            if (servertype.isDefault) {
                return false;
            }
        }
        return true;
    };

    $scope.addServerType = function () {
        if ($scope.serverType == undefined) {
            return;
        }
        for (var i = 0; i < $scope.bungeeType.serverTypes.length; i++) {
            var servertype = $scope.bungeeType.serverTypes[i];
            if (servertype._id == $scope.serverType._id) {
                return;
            }
        }
        var servertype = {
            _id: $scope.serverType._id,
            name: $scope.serverType.name,
            allowRejoin: false,
            isDefault: false
        };
        $scope.bungeeType.serverTypes.push(servertype);
        $scope.serverType = {}
    };

    $scope.removeServerType = function (servertype) {
        for (var i = 0; i < $scope.bungeeType.serverTypes.length; i++) {
            var servertypeOld = $scope.bungeeType.serverTypes[i];
            if (servertypeOld._id == servertype._id) {
                $scope.bungeeType.serverTypes.splice(i, 1);
                break;
            }
        }
    };

    $scope.addPlugin = function () {
        if ($scope.plugin == undefined) {
            return;
        }
        if (Object.keys($scope.pluginConfig).length == 0) {
            if ($scope.plugin.configs.length != 0) {
                return;
            }
        }
        for (var i = 0; i < $scope.bungeeType.driver.plugins.length; i++) {
            var plugin = $scope.bungeeType.driver.plugins[i];
            if (plugin._id == $scope.plugin._id) {
                return;
            }
        }
        var plugin = {
            _id: $scope.plugin._id,
            name: $scope.plugin.name
        };
        if (Object.keys($scope.pluginConfig).length > 0) {
            plugin._configId = $scope.pluginConfig._id;
            plugin.config = $scope.pluginConfig.name;
        }
        $scope.bungeeType.driver.plugins.push(plugin);
        $scope.plugin = {};
        $scope.pluginConfig = {};
    };
    $scope.removePlugin = function (plugin) {

        for (var i = 0; i < $scope.bungeeType.driver.plugins.length; i++) {
            var pluginOld = $scope.bungeeType.driver.plugins[i];
            if (pluginOld._id == plugin._id) {
                $scope.bungeeType.driver.plugins.splice(i, 1);
                break;
            }
        }
    };

    if ($scope.edit == true) {
        $http.get('/CraftingTable/api/plugin/bungee').success(function (data) {
            $scope.plugins = data.plugins;
            $http.get('/CraftingTable/api/servertype/all').success(function (data) {
                $scope.serverTypes = data.serverTypes;
                $http.get('/CraftingTable/api/bungeetype/one?id=' + $location.search().id).success(function (data) {
                    $scope.bungeeType = data;
                    for (var i = 0; i < $scope.bungeeType.driver.plugins.length; i++) {
                        var typePlugin = $scope.bungeeType.driver.plugins[i];
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

                    for (var i = 0; i < $scope.bungeeType.serverTypes.length; i++) {
                        var typeServerType = $scope.bungeeType.serverTypes[i];
                        typeServerType.name = undefined;
                        for (var j = 0; j < $scope.serverTypes.length; j++) {
                            var serverType = $scope.serverTypes[j];
                            if (serverType._id == typeServerType._id) {
                                typeServerType.name = serverType.name;
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
        $http.get('/CraftingTable/api/plugin/bungee').success(function (data) {
            $scope.plugins = data.plugins;
        }).error(function (error) {
            $scope.error = error.error;
        });

        $http.get('/CraftingTable/api/servertype/all').success(function (data) {
            $scope.serverTypes = data.serverTypes;
        }).error(function (error) {
            $scope.error = error.error;
        });
    }

}]);