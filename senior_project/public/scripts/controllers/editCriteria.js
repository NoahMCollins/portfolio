/*
Controller for adding or editing criteria items.
 */
angular.module('usgs')
  .config(function($routeProvider){
    $routeProvider
      .when('/alarms/:alarmId/criteria/add', {
        templateUrl: 'assets/views/edit_criteria.html',
        controller: 'EditCriteriaCtrl',
        controllerAs: 'ctrl'
      })
      .when('/alarms/:alarmId/criteria/:criteriaId', {
        templateUrl: 'assets/views/edit_criteria.html',
        controller: 'EditCriteriaCtrl',
        controllerAs: 'ctrl'
      });
  })
  .controller('EditCriteriaCtrl', function($routeParams, $location, criteria, alarms, instruments, notify) {
    var self = this;

    self.added = false;
    self.page = 'alarms';
    self.notify = notify.notifications[self.page];

    // Flag for determining if the criteria is already in the DB.
    var adding = !$routeParams.criteriaId;
    self.instruments = instruments.list;
    self.fields = [];
    self.editing = false;
    self.criteria = {};

    if( adding ) {
      // Creating a new Criteria
      self.criteria = {
        alarmId: $routeParams.alarmId
      };

    } else {
      // Loading in an existing criteria.
      criteria.getCriteria($routeParams.criteriaId, $routeParams.alarmId).then(
        function(res) {
          self.criteria = res;
          // For reverting if necessary
          self.criteriaHash = angular.copy(res);
          self.fields = instruments.load(self.criteria.instrumentName);
        });
    }

    /*
    Saves changes or adds a new criteria item
     */
    self.saveCriteria = function() {
      var request;

      // Returns if there is no criteria or no instrument selected
      if( !self.criteria || !self.criteria.instrumentName ) return;

      // If the alarm trigger is based on no-input, then the value is unnecessary.
      if( self.criteria.alarmTrigger === 'NONE' )
        self.criteria.value = null;

      if( adding ) {
        // Creates a criteria item.
        request = criteria.createCriteria(self.criteria);
        self.added = true;
      } else {
        // Saves a criteria item.
        request = criteria.saveCriteria(self.criteria);
      }

      // Handles the response from the request.
      request.then(
        function() {
          // Updates the hash controlling reverting information on success.
          self.criteriaHash = angular.copy(self.criteria);
        }, function() {
          // Reverts to old data on failure.
          self.criteria = angular.copy(self.criteriaHash);
          self.added = false;
        }
      );
    };

    /*
    Loads in the fields for the selected instrument.
     */
    self.loadFields = function() {
      self.criteria.fieldName = "";
      if( self.criteria.instrumentName )
        self.fields = instruments.load(self.criteria.instrumentName);
      else
        self.fields = [];
    };

    /*
    Deletes a criteria item and redirects on success.
     */
    self.deleteCriteria = function( criteriaId ) {
      criteria.deleteCriteria( criteriaId)
        .then(self.goBack);
    };

    /*
    Redirects back to criteria list.
     */
    self.goBack = function() {
      notify.clear(self.page);
      $location.url('/alarms/' + $routeParams.alarmId + '/criteria');
    };
  });
