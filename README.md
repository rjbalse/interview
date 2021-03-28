# interview
Iot Device Datstore CRUD using vertx

A simple REST API backend using vert.x framework for IoT devices datastore
which will store all the details related to IoT devices. Application also provides APIs to 
add, update, delete and get device details.

## Compatibility
- Java 8+
- Vert.x 3.x.x

 Vert.x version     | Library version | Rx version 
 ------------------ | ----------------|--------------
 3.0.0 - 3.3.3      | 2.0.x           | 1.x
 3.0.0 - 3.3.3      | 2.1.0           | 1.x
 3.0.0 - 3.3.3      | 2.2.1           | 1.x
 3.4.x              | 2.3.1           | 1.x
 3.5.x              | 3.0.0           | 1.x, 2.x
 
## Dependencies

### Dependency
### Maven
```xml
<dependency>
  <groupId>io.vertx</groupId>
  <artifactId>vertx-core</artifactId>
  <version>3.8.1</version>
</dependency>
<dependency>
  <groupId>io.vertx</groupId>
  <artifactId>vertx-web</artifactId>
  <version>3.8.1</version>
</dependency> 	


## Building

You build the project using:

mvn clean package


## Testing

The application is tested using [vertx-unit](http://vertx.io/docs/vertx-unit/java/).

## Packaging

The application is packaged as a _fat jar_, using the 
[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/).

## Running

Once packaged, just launch the jar_ as follows:

java -jar target/interview-1.0-SNAPSHOT.jar


Then, open a browser to http://localhost:8080.

## Features

```device details json : 
{
"deviceId":"123-asdasd-123",
"Domain":"smart-transport",
"state":"MH",
"city":"Pune",
"location":{
"Type":"point",
"Coordinates":[34.56,76.34]
},
"deviceType":"smart-camera"
}.
```
### POST
Invoking POST request on a devices (document) to add content of the device.
> POST /api/devices

Invoking POST request on a collection returns a device details.

#### Parameters

| Parameter | Description  |
|:--------- | :----------- |
| json | device details json |


### GET
Invoking GET request on a devices (document) to get all details of the devices.
> GET /api/devices

Invoking GET request on a collection returns all device's details.


### GET
Invoking GET request on a devices (document) to get device details by id.
> GET  /api/devices/{deviceId}

Invoking GET request on a collection returns a device details

#### Parameters

| Path Parameter | Description  |
|:--------- | :----------- |
| deviceId | defines the device id of the deive |


### PUT
Invoking PUT request on a a devices (document) to update device details by id.
> PUT  /api/devices/{deviceId}

Invoking PUT request on a collection returns a record update message

| Path Parameter | Description  |
|:--------- | :----------- |
| deviceId | defines the device id of the device |

### DELETE
Invoking DELETE  request on a a devices (document) to delete device details.
> DELETE /api/devices/{deviceId}

Invoking DELETE request on a collection returns a record delete message

| Path Parameter | Description  |
|:--------- | :----------- |
| deviceId | defines the device id of the device |


### Multiple Verticles and Communication in Vert.x

Verticles are communicated with each other by sending messages on the event bus. For this example, we have developed two verticles. One is the sender and the other is the receiver.

To start the application run MessengerLauncher.java as Java Application

Browser: http://localhost:8081/send/:message

Check the console to witness the communication between verticles by point to point messaging

### Added Open API specs i,e swagger-ui
Path : src/resources/webroot/index.html -> Open in Browser :(Refer Open API specs screenshots)
### JUnit Test Cases
Run MainVerticleTest.java as JUnit Test.
Refer Postman collection file i,e Interview Device Collection.postman_collection.json -> Import this JSON in Postman to test all the APIs.




