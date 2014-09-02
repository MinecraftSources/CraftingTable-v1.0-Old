var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location', '$window', function($scope, $http, $location, $window) {

    $scope.server = {};
    $scope.server._servertype = {};
    $scope.server._node = {};
    $scope.error = undefined;

    $scope.stop = function() {
        console.log('stopping');
        $http.put('/Sillicon/api/server?id='+$location.search().id, $scope.bungee).success(function(data) {
            $window.location.href = '/Sillicon/index';
        }).error(function(error) {
            $scope.error = error.error;
        });
    };

    $scope.cancel = function() {
        $window.location.href = '/Sillicon/index';
    };

    $http.get('/Sillicon/api/server?id='+$location.search().id).success(function(data) {
        $scope.server = data;
    }).error(function(error) {
        $scope.error = error.error;
    });

}]);