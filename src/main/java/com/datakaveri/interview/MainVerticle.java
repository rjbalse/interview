package com.datakaveri.interview;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.datakaveri.interview.model.Device;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

  public static final String COLLECTION = "devices";
  private MongoClient mongo;


  public static void main(String[] args) {
	    Vertx vertx = Vertx.vertx();
	    vertx.deployVerticle(new MainVerticle());
	  
	}
  
  @Override
  public void start(Future<Void> fut) {
	  // Create a Mongo client
    mongo = MongoClient.createShared(vertx, config());
    
    serverStartup(
            (nothing) -> startWebApp(
                (http) -> completeStartup(http, fut)
            ), fut);
 }

  @SuppressWarnings("deprecation")
private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
    // Create a router object.
    Router router = Router.router(vertx);

    router.get("/api/devices").handler(this::getDevices);
    router.route("/api/devices*").handler(BodyHandler.create());
    router.post("/api/devices").handler(event -> {
		try {
			addDevice(event);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	});
    router.get("/api/devices/:deviceId").handler(this::getDeviceById);
    router.put("/api/devices/:deviceId").handler(this::updateDeviceById);
    router.delete("/api/devices/:deviceId").handler(this::deleteDeviceById);
   // router.route("/*/swagger").handler(StaticHandler.create());
    router.route("/webroot/*").handler(StaticHandler.create("webroot"));
    // Create the HTTP server and pass the "accept" method to the request handler.
    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(
            // Retrieve the port from the configuration,
            // default to 8080.
            config().getInteger("http.port", 8080),
            next::handle
        );
  }

  @SuppressWarnings("deprecation")
  private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
    if (http.succeeded()) {
      fut.complete();
    } else {
      fut.fail(http.cause());
    }
  }

  @Override
  public void stop() throws Exception {
    mongo.close();
  }

  private void addDevice(RoutingContext routingContext) throws JsonParseException, JsonMappingException, IOException {
	System.out.println("Body" + routingContext.getBodyAsString()); 
	final Device device = new ObjectMapper().readValue(routingContext.getBodyAsString(), Device.class);

    System.out.println(device);
    
    mongo.insert(COLLECTION, device.toJson(), r ->
    routingContext.response()
        .setStatusCode(201)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(device)));
}

  private void getDeviceById(RoutingContext routingContext) {
    final String deviceId = routingContext.request().getParam("deviceId");
    if (deviceId == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.findOne(COLLECTION, new JsonObject().put("deviceId", deviceId), null, ar -> {
        if (ar.succeeded()) {
          if (ar.result() == null) {
            routingContext.response().setStatusCode(404).end();
            return;
          }
          Device device = new Device(ar.result());
          routingContext.response()
              .setStatusCode(200)
              .putHeader("content-type", "application/json; charset=utf-8")
              .end(Json.encodePrettily(device));
        } else {
          routingContext.response().setStatusCode(404).end();
        }
      });
    }
  }

  private void updateDeviceById(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("deviceId");
    JsonObject json = routingContext.getBodyAsJson();
    if (id == null || json == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.update(COLLECTION,
          new JsonObject().put("deviceId", id), // Select a unique document
          // The update syntax: {$set, the json object containing the fields to update}
          new JsonObject()
              .put("$set", json),
          v -> {
            if (v.failed()) {
              routingContext.response().setStatusCode(404).end();
            } else {
              routingContext.response()
                  .putHeader("content-type", "application/json; charset=utf-8")
                  .end("Record updated");
            }
          });
    }
  }

  private void deleteDeviceById(RoutingContext routingContext) {
    String deviceId = routingContext.request().getParam("deviceId");
    if (deviceId == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.removeOne(COLLECTION, new JsonObject().put("deviceId", deviceId),
          ar -> routingContext.response().setStatusCode(204).end("Record deleted"));
    }
  }

  private void getDevices(RoutingContext routingContext) {
    mongo.find(COLLECTION, new JsonObject(), results -> {
    	
      List<JsonObject> objects = results.result();
      List<Device> devices = objects.stream().map(Device::new).collect(Collectors.toList());     
      routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(devices));
    });
  }
  
  private void serverStartup(Handler<AsyncResult<Void>> next, Future<Void> fut) {
	  	next.handle(Future.<Void>succeededFuture());
  }
  
}