angular.module('usgs')
  .factory('user', function($q, $http, notify) {
    var list = [];
    var ready = $q.defer();
    var userHash = {};
    var currentUser;
    var model = 'account';

    /*
    Gets the currently logged in user
     */
    function getUser() {
      return $http.get('users/current').then(
        function(res) {
          return currentUser = res.data[0];
        }, function(err) {
          notify.error(model, err);
          return $q.reject(err);
        }
      );
    }

    /*
    Gets a list of users
     */
    function getUsers() {
      var request = $q.defer();
      $http.get('users/list').then(
        function(res) {
          // Clears array while keeping reference
          list.length = 0;
          userHash = {};
          // Loads array and saves values to a hash
          angular.forEach(res.data, function(n) {
            list.push(n);
            userHash[n.ID] = n;
          });
        }, function(err) {
          list.length = 0;
          notify.error(model, err);
          return $q.reject();
        }
      ).finally(function() {
        ready.resolve();
        request.resolve();
      });

      return request.promise;
    }

    /*
    Saves a user's information
     */
    function putUser( user ) {
      return $http.put('users/' + user.ID, user).then(
        function() {
          notify.success(model, "Successfully saved user information");
        },
        function(err) {
          notify.error(model, err);

          // Reverts the user information
          user.email = userHash[user.ID].email;
          user.password = userHash[user.ID].password;
          return $q.reject(err);
        });
    }

    //Initialization
    getUsers();

    return {
      getUser: getUser,
      getUsers: getUsers,
      putUser: putUser,
      list: list,
      ready: ready.promise
    };
  });
