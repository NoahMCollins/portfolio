'use strict';

/*
Initializing the usgs module here.  It is dependent
on the ngRoute module which is loaded in the index.html
via a cdn.
 */
angular.module('usgs', [
  'ngRoute'
])
  /*
  The following is to provide a route to anything
  that doesn't match any of the other routes.  The
  /users route is in controllers/users.js
   */
  .config(function ($routeProvider) {
    $routeProvider
      .otherwise({
        redirectTo: '/users'
      });
  });
