'use strict';

angular.module('usgs')
  .config(function($routeProvider){
    $routeProvider
      .when('/home', {
        templateUrl: 'assets/views/home.html',
        controller: 'MainCtrl',
        controllerAs: 'ctrl'
      });
  })
  .controller('MainCtrl', function (user, notify) {
    /*
     Assigning this to self allows for references to this from
     inside promises where the context of this switches.
      */
    var self = this;

    // Used in the header page
    self.page = 'account';
    // Used in the notify bar
    self.notify = notify.notifications[self.page];

    self.user = null;

    /*
    Function for saving the current user model.  If the user model
    is not defined, then it should do nothing.
     */
    self.putUser = function() {
      if( !self.user ) return;
      // NOTE: finally only works on certain promises.  Use with care.
      user.putUser(self.user).finally(
        function() {
          // Reset password
          self.user.password = "";
        }
      );
    };

    /*
    Gets the current user and loads their information into the scope
    of this controller.
     */
    user.getUser().then(function(res) {
      self.user = res;
    }, function(err) {
      // Displays an error if unsuccessful.
      notify.error(self.page, err.data);
    });
  });
