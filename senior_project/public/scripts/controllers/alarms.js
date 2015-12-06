'use strict';

angular.module('usgs')
  .config(function($routeProvider){
    $routeProvider
      .when('/alarms', {
        templateUrl: 'assets/views/alarms.html',
        controller: 'AlarmCtrl',
        controllerAs: 'ctrl'
      });
  })
  .controller('AlarmCtrl', function ( $location, alarms, notify ) {
    var self = this;

    self.page = 'alarms';

    self.notify = notify.notifications[self.page];

    /*
    Sets the selected alarm to the one passed in or a blank object
    if no alarm was passed in.  This function was created to assist
    in the creation and editing of alarms.
     */
    self.openModal = function(alarm) {
      alarm = alarm || {};
      self.selected = alarm;

      //Backup copy for resetting the alarm later. (Never Implemented)
      self.currentCopy = angular.copy(alarm);
    };

    /*
    Function that toggles the enabled flag and then saves the alarm.
     */
    self.enableAlarm = function(alarm) {
      alarm.enabled = alarm.enabled === '1' ? '0' : '1';
      self.saveAlarm(alarm);
    };


    self.resetAlarm = function(alarm) {
      alarm.triggered = '0';
      self.saveAlarm(alarm);
    };

    //Linking up to the alarms service
    self.alarms = alarms.list;
    self.removeAlarm = alarms.removeAlarm;
    self.saveAlarm = alarms.save;

    /*
    Clears out error/success messages and transitions to the list of
    criteria for the alarm identified by the passed in id.
     */
    self.listCriteria = function(id) {
      if( id ) {
        notify.clear(self.page);
        $location.url('/alarms/' + id + '/criteria');
      }
    }
  });
