package com.datakaveri.interview;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SenderVerticle extends AbstractVerticle {
	
	private static final Integer HTTP_PORT = 8081;
	private static final String PATH_PARAM = "message";
    
	@Override
    public void start(Future<Void> future) throws Exception {

        final Router router = Router.router(vertx);
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>Hello from non-clustered messenger example!</h1>");
        });
        router.post("/send/:message").handler(this::sendMessage);

        vertx.createHttpServer().requestHandler(router::accept)
                .listen(config().getInteger("http.server.port", HTTP_PORT), result -> {
                    if (result.succeeded()) {
                       System.out.println("HTTP server running on port " + HTTP_PORT);
                        future.complete();
                    } else {
                    	System.out.println("Could not start a HTTP server"+ result.cause());
                        future.fail(result.cause());
                    }
                });
        
    }
	
	private void sendMessage(RoutingContext routingContext){
		   final EventBus eventBus = vertx.eventBus();
		   final String message = routingContext.request().getParam(PATH_PARAM);
		   eventBus.send("messageAddress", message, reply -> {
		      if (reply.succeeded()) {
		          System.out.println("Received reply: " + reply.result().body());
		      } else {
		    	  System.out.println("No reply");
		      }
		   });
		   routingContext.response().end(message);
		}
	
	
	private Future<Void> deployHelper(String name){
		   final Future<Void> future = Future.future();
		   vertx.deployVerticle(name, res -> {
		      if(res.failed()){
		    	  System.out.println("Failed to deploy verticle " + name);
		         future.fail(res.cause());
		      } else {
		    	  System.out.println("Deployed verticle " + name);
		         future.complete();
		      }
		   });
		   
		   return future;
		}
	
	

}
