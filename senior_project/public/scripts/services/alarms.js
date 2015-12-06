angular.module('usgs')
  .factory('alarms', function( $http, $q, notify ) {

    var alarms = [];
    var model = "alarms";

    function addAlarm( alarm ) {
      //Adds the alarm that is passed in to the database
      alarm.triggered = 0;
      alarm.enabled = 0;
      return $http.post('alarms', alarm).then(
        function() {
          // Displays an error
          getAlarms();
        }, function(err) {
          notify.error(model, err);
          return $q.reject(err);
        }
      );
    }

    function removeAlarm( ind ) {
      //Delete alarm at the given index
      return $http.delete('alarms/' + alarms[ind].ID).then(
        function() {
          // Removes the alarm locally
          alarms.splice(ind, 1);
          notify.success(model, "Successfully Deleted Alarm");
        }, function(err) {
          notify.error(model, err);
          return $q.reject();
        }
      );
    }

    /*
    Returns the alarm from the local cache.
     */
    function getAlarm( id ) {
      var matches = alarms.filter(function(n) {
        return n.ID === id;
      });

      // returns the first match (Should only have one)
      if( matches.length ) return matches[0];
      else return null;
    }

    function getAlarms() {
      //Return a list of alarms
      alarms.length = 0;
      return $http.get('alarms').then(
        function(res) {
          // Adds the alarms locally
          angular.forEach(res.data, function(n) {
            alarms.push(n);
          });
        }, function(err) {
          notify.error(model, err.data)
        }
      );
    }

    /*
    Saves the alarm as passed in.  If the alarm doesn't have an ID,
    it means that it wasn't pulled from the database and therefore
    only exists locally.
     */
    function saveAlarm( alarm ) {
      var request;
      var op;

      // Requires an alarmName
      if( !alarm.alarmName ) return;

      if( alarm.ID ) { // Save
        request = $http.put('alarms/' + alarm.ID, alarm);
        op = "save";
      } else { // Create
        //Defaults triggered and enabled to 0
        alarm.triggered = alarm.triggered ? alarm.triggered : 0;
        alarm.enabled = alarm.enabled ? alarm.enabled : 0;
        op = "create";
        request = $http.post('alarms', alarm);
      }

      request.then(
        function() {
          if( !alarm.ID ) {
            // Refreshes alarms on creation
            getAlarms();
          }
          notify.success(model, "Successfully " + op + "d alarm")
        },
        function(err) {
          notify.error(model, err);
        }
      );
    }

    var ready = getAlarms();
    return {
      list: alarms,
      addAlarm: addAlarm,
      getAlarm: getAlarm,
      getAlarms: getAlarms,
      removeAlarm: removeAlarm,
      save: saveAlarm,
      ready: ready
    };

  });
