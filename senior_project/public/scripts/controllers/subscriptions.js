'use strict';

angular.module('usgs')
  .config(function($routeProvider){
    $routeProvider
      .when('/subscriptions', {
        templateUrl: 'assets/views/subscriptions.html',
        controller: 'SubscriptionsCtrl',
        controllerAs: 'ctrl'
      });
  })
  .controller('SubscriptionsCtrl', function ( alarms, subscriptions, notify ) {
    var self = this;
    self.page = 'subscriptions';
    self.notify = notify.notifications[self.page];

    //These lists are populated from the alarms and subscriptions services
    self.alarms = alarms.list;
    self.subscriptions = subscriptions.list;

    /*
    Subscribes to the current alarm if it is defined.  It then sets the
    current alarm to null so that the user can subscribe to a different
    alarm.
     */
    self.subscribe = function() {
      if( self.currentAlarm )
        subscriptions.addSubscription( self.currentAlarm );

      self.currentAlarm  = null;
    };

    /*
    Removes the subscription passed in from the database and the list of
    subscriptions.
     */
    self.unsubscribe = function( subscr ) {
      subscriptions.removeSubscription(subscr);
    };

    /*
    Determines if an alarm is subscribed to.
     */
    self.subscribed = function( alarm ) {
      /*
       Iterate through the subscriptions to determine if the alarm is in
       the list of subscriptions.
        */
      for (var i = 0; i < self.subscriptions.length; i++) {
        if( alarm.alarmName === self.subscriptions[i].alarmName)
          return false;
      }

      return true;
    }
  });
