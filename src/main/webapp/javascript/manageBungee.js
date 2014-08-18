var formApp = angular.module('formApp', []);

formApp.config(function($locationProvider) {
    $locationProvider.html5Mode(true);
});

formApp.controller('formController', ['$scope', '$http', '$location'], function($scope, $http, $location) {

    $scope.bungee = {};
    $scope.bungee.type = {};
    $scope.bungee.node = {};
    $scope.error = undefined;

    $http.get('/mn2/api/bungee/manage?bungee='+$location.search().bungee).success(function(data) {
        $scope.bungee = data;
    }).error(function(error) {
        $scope.error = error.error;
    });

});