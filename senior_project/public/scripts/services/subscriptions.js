angular.module('usgs')
  .factory('subscriptions', function($http, $q, notify) {
    var subscriptions = [];
    var model = 'subscriptions';

    /*
    Loads all of the subscriptions from the database for the current user.
     */
    function loadSubscriptions() {
      // Reset array keeping reference
      subscriptions.length = 0;
      $http.get('subscriptions').then(
        function(res) {
          // Load the array
          angular.forEach(res.data, function(n) {
            subscriptions.push(n);
          });
        }, function(err) {
          notify.error(model, err);
          return $q.reject(err);
        }
      )
    }

    /*
    Adds a subscription to the database
     */
    function addSubscription( subscr ) {
      $http.post('subscriptions', subscr)
        .then(
          function() {
            // Refreshes subscription list
            loadSubscriptions();
            notify.success(model, "Successfully added subscription");
          },
          function(err) {
            notify.error(model, err);
            return $q.reject(err);
          });
    }

    /*
    Removes a subscription from the database
     */
    function removeSubscription( subscr ) {
      $http.delete('subscriptions/' + subscr.ID)
        .then(
          function() {
            // Delete it from the list locally
            if( subscriptions.indexOf(subscr) > -1 )
              subscriptions.splice(subscriptions.indexOf(subscr), 1);
            notify.success(model, "Successfully deleted subscription")
          },
          function(err) {
            notify.error(model, err);
            return $q.reject();
          });
    }

    loadSubscriptions();
    return {
      list: subscriptions,
      addSubscription: addSubscription,
      removeSubscription: removeSubscription
    };

  });
