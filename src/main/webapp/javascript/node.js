var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {
    $scope.edit = $location.path() == "/Silicon/node/edit";
    $scope.error = undefined;
    $scope.bungeetypes = [];
    $scope.bungeeType = null;
    $scope.node = {};
    $scope.node._bungeeType = "";

    $scope.cancel = function() {
        $window.location.href = '/Silicon/node/list';
    };

    $scope.nodeAction = function () {
        var timeMillis = Date.now();
        timeMillis = (timeMillis - 60000);
        if ($scope.node.lastUpdate >= timeMillis) {
            return "Stop Node";
        } else {
            return "Start Node";
        }
    };

    $scope.doNodeAction = function () {
        var status = "start";
        var timeMillis = Date.now();
        timeMillis = (timeMillis - 60000);
        if ($scope.node.lastUpdate >= timeMillis) {
            status = "stop";
        } else {
            status = "start";
        }
        $scope.node.status = status;
        $http.put('/Silicon/api/node/'+status, $scope.node).success(function (data) {
            $window.location.href = '/Silicon/node/list';
        }).error(function (error) {
            $scope.error = error.error;
        });
    };

    $scope.submit = function () {
        if ($scope.bungeeType != null) {
            $scope.node._bungeeType = $scope.bungeeType._id;
        } else {
            $scope.node._bungeeType = "";
        }
        if ($scope.edit == true) {
            $http.put('/Silicon/api/node/save', $scope.node).success(function (data) {
                $window.location.href = '/Silicon/node/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        } else {
            $http.post('/Silicon/api/node/add', $scope.node).success(function (data) {
                $window.location.href = '/Silicon/node/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.remove = function () {
        if ($scope.edit == true) {
            $http.delete('/Silicon/api/node/delete?id=' + $scope.node._id).success(function (data) {
                $window.location.href = '/Silicon/node/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    if ($scope.edit) {
        $http.get('/Silicon/api/bungeetype/all').success(function (data) {
            $scope.bungeetypes = data.bungeeTypes;
            $http.get('/Silicon/api/node/one?id='+$location.search().id).success(function(data) {
                $scope.node = data;

                for (var i = 0; i < $scope.bungeetypes.length; i++) {
                    var bungeeType = $scope.bungeetypes[i];
                    if(bungeeType._id == $scope.node._bungeeType) {
                        $scope.bungeeType = bungeeType;
                        break;
                    }
                }
            }).error(function(error) {
                $scope.error = error.error;
            });
        }).error(function (error) {
            $scope.error = error.error;
        });
    } else {
        $http.get('/Silicon/api/bungeetype/all').success(function (data) {
            console.log(data);
            $scope.bungeetypes = data.bungeeTypes;
        }).error(function (error) {
            $scope.error = error.error;
        });
    }

}]);