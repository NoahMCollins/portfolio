/*
Service to make the display of error and success messages easier.
 */
angular.module('usgs')
  .factory('notify', function() {

    // Different models that are allowed to have error/success messages.
    var models = [
      'account',
      'alarms',
      'subscriptions'
    ];

    // Initializes an object of messages for the models.
    var notify = {};
    angular.forEach(models, function(n) {
      notify[n] = {};
    });

    /*
     Displays a message to the user

     model - enum('account', 'alarms', 'subscriptions')
     msg - String
      */
    function success(model, msg) {
      if(!msg || typeof msg !== 'string' || models.indexOf(model) < 0) return;

      // Delete any errors
      delete notify[model].error;

      notify[model].success = {
        msg: msg
      };
    }

    /*
     Displays a error to the user

     model - enum('account', 'alarms', 'subscriptions')
     msg - object
        * status - type of error
          * 400 - client data/request error
          * 500 - server error
          * 200 - ok (not an error)
        * data - String (reason)
     */
    function error(model, msg) {
      if(!msg || typeof msg !== 'object' || models.indexOf(model) < 0)
        return;

      // Delete any success messages
      delete notify[model].success;

      var text = "";

      switch( msg.status ) {
        case(200) :
          return;
        case(400) :
          text = msg.data;
          break;
        case(500) :
          text = "Internal Server Error";
          break;
        default:
          text = "Unknown Server Error";
      }
      notify[model].error = {
        msg: text
      };
    }

    /*
    Clears out all messages
     */
    function clear(model) {
      if( models.indexOf(model) > -1 ) {
        notify[model] = {};
      }
    }

    return {
      notifications: notify,
      clear: clear,
      error: error,
      success: success
    };
  });
