angular.module('usgs')
  .config(function($routeProvider){
    $routeProvider
      .when('/alarms/:alarmId/criteria', {
        templateUrl: 'assets/views/alarm_criteria.html',
        controller: 'CriteriaCtrl',
        controllerAs: 'ctrl'
      });
  })
  .controller('CriteriaCtrl', function($routeParams, $location, alarms, criteria, notify) {
    var self = this;

    self.page = 'alarms';
    self.notify = notify.notifications[self.page];
    self.list = [];
    self.editing = false;

    /*
    Sends the user to a page to add an individual criteria to the parent alarm.
     */
    self.addCriteria = function() {
      notify.clear(self.page);
      $location.url('/alarms/' + $routeParams.alarmId + '/criteria/add');
    };

    /*
     Sends the user to a page to edit the alarm identified by criteriaId and belonging
     to the parent alarm.
     */
    self.editCriteria = function( criteriaId ) {
      notify.clear(self.page);
      $location.url('/alarms/' + $routeParams.alarmId + '/criteria/' + criteriaId);
    };

    /*
    Deletes a criteria item.
     */
    self.deleteCriteria = function( criteriaId ) {
      criteria.deleteCriteria( criteriaId );
    };

    /*
    Initializes the criteria list from the criteria service.
     */
    criteria.getAlarmCriteria($routeParams.alarmId)
      .then(function() {
        self.list = criteria.list;
      });

    /*
    Loads in the parent alarm from the alarms service.
     */
    alarms.ready.then(
      function() {
        self.alarm = alarms.getAlarm($routeParams.alarmId);
      }
    );

    /*
    Goes back to the list of alarms
     */
    self.goBack = function() {
      notify.clear(self.page);
      $location.url('/alarms');
    }

  });
