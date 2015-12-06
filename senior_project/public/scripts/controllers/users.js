'use strict';

angular.module('usgs')
  .config(function($routeProvider){
    $routeProvider
      .when('/users', {
        templateUrl: 'assets/views/users.html',
        controller: 'UsersCtrl',
        controllerAs: 'ctrl'
      });
  })
  .controller('UsersCtrl', function (user) {
    var self = this;
    self.page = 'users';
    self.list = [];

    /*
    Loads the list of users from the user service.
     */
    user.ready.then(function() {
      self.list = user.list;
    });
  });