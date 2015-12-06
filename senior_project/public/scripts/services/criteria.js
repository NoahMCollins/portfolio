angular.module('usgs')
  .factory('criteria', function($http, $q, notify) {

    //Current parent alarm ID
    var currentAlarm = 0;
    //Hash of criteria for easy lookup
    var criteriaHash = {};
    //Current list of criteria
    var list = [];
    var model = 'alarms';

    /**
     * @name getCriteria
     * @param ID {Integer} ID of the criteria
     * @param alarmID {Integer} ID of the parent alarm
     * @description
     * Gets the specified criteria object
     */
    function getCriteria(ID, alarmID) {
      var dfd = $q.defer();

      if( currentAlarm && criteriaHash[ID] ) {
        dfd.resolve(criteriaHash[ID]);
      } else {
        getAlarmCriteria(alarmID).then(
          function() {
            dfd.resolve(criteriaHash[ID]);
          }, function() {
            dfd.reject("Could not find requested criteria.");
          }
        );
      }

      return dfd.promise;
    }

    /**
     * @name getAlarmCriteria
     * @param alarmID {Integer} ID of the alarm
     * @description
     * Gets all criteria for the given alarmID.
     * Sets criteriaHash and currentAlarm.
     */
    function getAlarmCriteria(alarmID) {
      if( !alarmID ) return $q.reject();
      criteriaHash = {};
      list.length = 0;
      return $http.get('alarms/' + alarmID + '/criteria')
        .then(
          function(res) {
            // Updates hash for all criteria.
            angular.forEach(res.data, function(n) {
              if(n.ID) {
                criteriaHash[n.ID] = n;
                list.push(n);
              }
            });
            currentAlarm = alarmID;
          },
          function(err) {
            notify.error(model, err);
            return $q.reject(err);
          });
    }

    /**
     * @name saveCriteria
     * @param criteria {Object} criteria being saved
     * @description
     * Saves changes to an already existing criteria object to the database
     */
    function saveCriteria(criteria) {
      if( !criteria.ID || !currentAlarm ) return $q.reject();


      if( !criteria.triggerTime || typeof criteria.triggerTime !== 'number' || criteria.triggerTime < 0 ) {
        notify.error(model, {
          status: 400,
          data: "Trigger time field is required."
        });

        return $q.reject();
      }

      return $http.put(
        'alarms/' + currentAlarm + '/criteria/' + criteria.ID,
        criteria
      ).then(
        function() {
          // Update hash for possible revert later.
          criteriaHash[criteria.ID] = criteria;
          notify.success(model, "Successfully saved criteria")
        },
        function(err) {
          // Revert if applicable
          if( criteriaHash[criteria.ID] )
            criteria = criteriaHash[criteria.ID];

          notify.error(model, err);
          return $q.reject(err);
        }
      );
    }

    /**
     * @name createCriteria
     * @param criteria {Object} fields to be saved as a criteria object
     * @description
     * Creates a new criteria object in the database.
     */
    function createCriteria(criteria) {
      if( criteria.ID || !currentAlarm ) return $q.reject();

      if( !criteria.triggerTime || typeof criteria.triggerTime !== 'number' || criteria.triggerTime < 0 ) {
        notify.error(model, {
          status: 400,
          data: "Trigger time field is required."
        });

        return $q.reject();
      }

      return $http.post(
        'alarms/' + currentAlarm + '/criteria',
        criteria
      ).then(
        function() {
          // Reload alarm criteria list. (I need the ID from the DB for this criteria)
          getAlarmCriteria(currentAlarm);
          notify.success(model, "Successfully created criteria");
        },
        function(err) {
          notify.error(model, err);
          return $q.reject();
        }
      );
    }

    /**
     * @name deleteCriteria
     * @param ID {Integer} Id of the criteria to be deleted
     * @description
     * Deletes the criteria of the specified ID from the database and updates
     * the data structures locally.
     */
    function deleteCriteria(ID) {
      if( !ID || !currentAlarm ) return $q.reject();

      return $http.delete( 'alarms/' + currentAlarm + '/criteria/' + ID )
        .then(
          function() {
            for( var i = 0; i < list.length; i++ ) {
              // Removes the criteria from the local list.
              if( list[i].ID === ID ) {
                list.splice(i, 1);
                break;
              }
            }
            notify.success(model, "Successfully deleted criteria");
          }, function(err) {
            notify.error(model, err);
            return $q.reject(err);
          }
        )
    }

    return {
      list: list,
      getCriteria: getCriteria,
      getAlarmCriteria: getAlarmCriteria,
      saveCriteria: saveCriteria,
      createCriteria: createCriteria,
      deleteCriteria: deleteCriteria
    };
  });