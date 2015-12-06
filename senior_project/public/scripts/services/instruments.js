angular.module('usgs')
  .factory('instruments', function($http) {

    var instruments = [];
    var fields = {};

    /**
     * @description gets all of the instruments from the instruments field
     */
    function getInstruments() {
      return $http.get('instruments')
        .then(
          function(res) {
            instruments.length = 0;
            angular.forEach(res.data, function(n) {
              instruments.push(n);
            });
          },
          function(err) {
            console.error(err);
          });
    }

    /**
     * @description Loads the instrument field names based on the
     * instrument name
     */
    function getFields( instrumentName ) {
      if( !instrumentName ) return [];

      if( fields[instrumentName] ) return fields[instrumentName];

      fields[instrumentName] = [];

      $http.get('instruments/' + instrumentName + '/fields')
        .then(
          function(res) {
            angular.forEach(res.data, function(n) {
              fields[instrumentName].push(n);
            });
          },
          function(err) {
            console.error(err);
          });

      return fields[instrumentName];
    }

    // Initializes the instrument list
    getInstruments();

    return {
      list: instruments,
      load: getFields
    };
  });