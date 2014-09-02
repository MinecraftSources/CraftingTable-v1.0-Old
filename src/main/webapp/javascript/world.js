var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {
    $scope.edit = $location.path() == "/Silicon/world/edit";
    $scope.world = {};
    $scope.error = undefined;

    $scope.cancel = function() {
        $window.location.href = '/Silicon/world/list';
    };

    $scope.remove = function () {
        if ($scope.edit) {
            $http.delete('/Silicon/api/world/delete?id=' + $scope.world._id).success(function (data) {
                $window.location.href = '/Silicon/world/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    $scope.submit = function () {
        if ($scope.edit == true) {
            $http.put('/Silicon/api/world/save', $scope.world).success(function (data) {
                $window.location.href = '/Silicon/world/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        } else {
            $http.post('/Silicon/api/world/add', $scope.world).success(function (data) {
                $window.location.href = '/Silicon/world/list';
            }).error(function (error) {
                $scope.error = error.error;
            });
        }
    };

    if ($scope.edit) {
        $http.get('/Silicon/api/world/one?id='+$location.search().id).success(function (data) {
            $scope.world = data;
        }).error(function (error) {
            $scope.error = error.error;
        });
    }

}]);