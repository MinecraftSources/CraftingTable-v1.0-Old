var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {

    $scope.bungee = {};
    $scope.bungee._bungeetype = {};
    $scope.bungee._node = {};
    $scope.error = undefined;

    $scope.stop = function() {
        $http.put('/MN2DockerTomcat/mn2/api/bungee?id='+$location.search().id, $scope.bungee).success(function(data) {
            $window.location.href = '/MN2DockerTomcat/mn2';
        }).error(function(error) {
            $scope.error = error.error;
        });
    };

    $scope.cancel = function() {
        $window.location.href = '/MN2DockerTomcat/mn2';
    };

    $http.get('/MN2DockerTomcat/mn2/api/bungee?id='+$location.search().id).success(function(data) {
        $scope.bungee = data;
    }).error(function(error) {
        $scope.error = error.error;
    });

}]);