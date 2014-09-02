var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {
    $scope.edit = $location.path() == "/CraftingTable/plugin/edit";
    $scope.plugin = {};
    $scope.plugin.configs = [];
    $scope.config = {};
    $scope.error = undefined;

    $scope.cancel = function() {
        $window.location.href = '/CraftingTable/plugin/list';
    };

    $scope.remove = function () {
        if ($scope.edit) {
            $http.delete('/CraftingTable/api/plugin/delete?id=' + $scope.plugin._id).success(function (data) {
                console.log(data);
                $window.location.href = '/CraftingTable/plugin/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.submit = function () {
        if ($scope.edit == true) {
            $http.put('/CraftingTable/api/plugin/save', $scope.plugin).success(function (data) {
                $window.location.href = '/CraftingTable/plugin/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        } else {
            $http.post('/CraftingTable/api/plugin/add', $scope.plugin).success(function (data) {
                $window.location.href = '/CraftingTable/plugin/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.addConfig = function () {
        if ($scope.config.name == undefined) {
            return;
        }
        if ($scope.config.location == undefined) {
            return;
        }
        for (var i = 0; i < $scope.plugin.configs.length; i++) {
            var config = $scope.plugin.configs[i];
            if (config.name == $scope.config.name) {
                return;
            }
        }
        var configInfo = {
            name: $scope.config.name,
            location: $scope.config.location
        };
        $scope.plugin.configs.push(configInfo);
        $scope.config = {};
    };
    $scope.removeConfig = function (config) {
        for (var i = 0; i < $scope.plugin.configs.length; i++) {
            var configOld = $scope.plugin.configs[i];
            if (configOld.name == config.name && configOld.location == config.location) {
                $scope.plugin.configs.splice(i, 1);
                break;
            }
        }
    };

    if ($scope.edit) {
        $http.get('/CraftingTable/api/plugin/one?id='+$location.search().id).success(function (data) {
            console.log(data);
            $scope.plugin = data;
        }).error(function (error) {
            $scope.error = error.error;
        });
    }
}]);