### REST Example v1.0 [![](https://travis-ci.com/taorusb/RESTExample.svg?branch=main)](https://travis-ci.com/taorusb/RESTExample.svg?branch=main)

The application implements the REST architecture and applies the basic HTTP methods to it.

#### Used technologies:

##### JDK version 11
##### Flyway
##### Servlet 
##### Gson
##### Hibernate

##### Database used:
MySQL

##### You can try the app on:

https://rest-example888.herokuapp.com/api/v1/users

##### To launch the application on local machine, type in the command prompt:

mvn flyway:migrate

mvn package

##### Used design patterns:

Facade, Singleton

##### Object data is stored in tables:

users, files, events

##### The application only works with the JSON format, which should look like this:
{ "id": 0, "username": "bonboo@test.com", "status": "ACTIVE" }

{ "id": 0, "path": "/users", "userId": 1 }

{ "id": 0, "uploadDate": "13/05/2021", "fileId": 1, "userId": 1 }

##### When adding a new entity, the ID field must be zero (any other values will be overwritten by the new ID);
##### POST/PUT methods are processed by URLs (/user /file /event), DELETE (/user/{id} /file/{id} /event{id}), GET (/users)
