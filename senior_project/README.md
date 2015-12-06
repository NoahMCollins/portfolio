###Note:
The config has been removed from this repository in order to protect database passwords and API keys.  I have tried to limit the files in this collection to be only files which I had the primary role in developing.  The Project Setup no longer works because the framework files have been removed from this project for the sake of making this project less cluttered.

# USGS Project

***

### Project Description

The USGS has many instruments in the field monitoring critical data, especially seismic activity.  These instruments are
operating off of batteries which periodically need to be replaced in order to keep all of the systems effective.  The
scope of this project is to create a system that monitors the readings of these instruments and report any user-defined
abnormalities.  Events currently supported are loss of data and outlier readings.  If these events occur, a message will
be sent to the subscribed user, notifying them of the event.  They can then take the appropriate actions and then reset
the alarm.

We broke this project scope into two separate programs.  One program handles the monitoring of the data against stored
criteria and the distribution of alert messages.  This program allows users to set the criteria to be monitored and
subscribe to the created alarms.  Together, they make up an effective and easy to use alarm management system.


### Authors

Noah Collins
Christopher Stickney
Jennifer Clark
Jefferson Lusk
Arkadiy Shapovalov

### Project Setup Overview

Since this is a standard git project, it can be pulled in similarly to any other git project:

    git clone git@69.166.62.18:software-engineering/usgs_play.git

This project uses activator as the project manager and there are many different commands that can be run through:

   ./activator <cmd>

To build into a war file:

	./activator war

To deploy on the Tomcat server:

Drag and drop the <filename>.war and the <filename>-assets.jar file 
to the webapps folder and run startup.bat or startup.sh.  This will 
run the app on Tomcat under <server>/<filename>

***

### Route API

***

#### Application Routes

#### GET     /

Goes to the index page.  If the user is logged in, it will take you to the main application.

#### GET     /register

Goes to the registration page.

#### POST    /register

Registers a user.

Parameters:
  * email - user email to be registered
  * password - user password
  * confirm - confirmation of user password
  * first_name - user first name
  * last_name - user last name

#### GET     /login

Goes to the login page.

#### POST   /login

Logs a user in.

Parameters:
  * email - user email
  * password - user password

#### GET    /logout

Logs a user out.

***

#### User Routes

#### GET    /users/current

Gets the current user information.

#### GET    /users/list

Gets a list of all registered users.

#### PUT    /users/:id

Updates the user identified by _id_.

***

#### Subscription Routes

#### GET    /subscriptions

Gets all the subscriptions for the logged in user.

#### POST   /subscriptions

Creates a new subscription.

#### DELETE /subscriptions/:id

Deletes the subscription identified by _id_.

***

#### Alarm Routes

#### GET    /alarms

Gets a list of all alarms in the system.

#### POST   /alarms

Creates a new alarm.

#### PUT    /alarms/:id

Updates the alarm identified by _id_.

#### DELETE /alarms/:id

Deletes the alarm identified by _id_.

***

#### Criteria Routes

#### GET    /alarms/:id/criteria

Gets a list of the criteria for the alarm identified by _id_.

#### POST   /alarms/:alarmId/criteria

Creates a criteria object for the alarm identified by _alarmId_.

#### PUT    /alarms/:alarmId/criteria/:id

Updates the criteria identified by _id_ belonging to the alarm _alarmId_.

#### DELETE /alarms/:alarmId/criteria/:id

Deletes the criteria identified by _id_ belonging to the alarm _alarmId_.

***

#### Instrument Routes

#### GET    /instruments

Gets a list of all instruments in the system.

#### GET    /instruments/:name/fields

Gets a list of field names for the instrument identified by the _name_ field.
