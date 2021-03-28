package com.datakaveri.interview;

import com.datakaveri.interview.ReceiverVerticle;
import com.datakaveri.interview.SenderVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class MessengerLauncher extends AbstractVerticle {
	
	  public static void main(String[] args) {
		    Vertx vertx = Vertx.vertx();
		    vertx.deployVerticle(new MessengerLauncher());		  
		}
	
	@Override
	public void start(Future<Void> future){
	 CompositeFuture.all(deployHelper(ReceiverVerticle.class.getName()),
	 deployHelper(SenderVerticle.class.getName())).setHandler(result -> { 
	   if(result.succeeded()){
	      future.complete();
	   } else {
	      future.fail(result.cause());
	   }
	  });
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
