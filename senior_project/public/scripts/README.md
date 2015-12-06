## Application README

***

### Description

***

This Application is written using angularJS 1.2.0.  It is paired with Java Play server-side code.  The server code
resides in the app folder and contains the controllers for the routes and views for the index, login, and registration
pages.  While the code in the app folder is intended to interact with the DB, this part of the project is designed to
only deal with user interaction with data and depend on the server handling the requests made upon it correctly.  Much
of the logic here is very self-explanatory, but I have provided documentation where possible.

***

### Controllers

***

Controllers interact with the views and form a link between them and the services.  The logic in these files should be
minimal, calling the services whenever they need any heavy lifting done.  They rely heavily upon the fact that JS arrays
are saved by reference.  Therefore, it is important in the services not to break this referencing.  e.g.

```javascript

array = [];  // Assigns reference from [] to array
array.length = 0;  // Clears out array without breaking the reference

```

Controllers also contain the logic for routing which can be found at the top of each of the controllers.  These do not
have to be in the same file as the controllers which they reference, but I have found they are easier to manage and
correct if they are local.

***

### Services

***

Services provide an interaction between the controllers and the server code.  They are also a key part in communication
between controllers and form a type of dependency tree.  One of their jobs is to make requests via HTTP and handle the
responses that come back.  This keeps the logic in the services, allows for code modularization, and makes the code very
clean.  Any services that are required as dependencies for a controller or another service can be found in the factory
declaration:

```javascript

angular.module('usgs')
    .factory('alarms', function( /* dependencies here */ );

```