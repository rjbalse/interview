package com.datakaveri.interview;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class ReceiverVerticle extends AbstractVerticle {
	
	@Override
	public void start() throws Exception {
	   final EventBus eventBus = vertx.eventBus();
	   eventBus.consumer("messageAddress", receivedMessage -> {
	     System.out.println("Received message: " + receivedMessage.body());
	     receivedMessage.reply("Got your message");
	   });
	   System.out.println("Receiver ready!");
	}
	
}
