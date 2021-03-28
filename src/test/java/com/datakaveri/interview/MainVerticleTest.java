package com.datakaveri.interview;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.datakaveri.interview.model.Device;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * This is our JUnit test for our Main Verticle
 */
@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;
  private Integer port;
  private static MongodProcess MONGO;
  private static int MONGO_PORT = 27017;

  @BeforeClass
  public static void initialize() throws IOException {
	  MongodStarter starter = MongodStarter.getDefaultInstance();

	    String bindIp = "localhost";
	    int port = 8080;

	    IMongodConfig mongodConfig = new MongodConfigBuilder()
	            .version(Version.Main.PRODUCTION)
	            .net(new Net(bindIp, port, Network.localhostIsIPv6()))
	            .build();

	    MongodExecutable mongodExecutable = null;

	    mongodExecutable = starter.prepare(mongodConfig);

    MONGO = mongodExecutable.start();
  }

  @AfterClass
  public static void shutdown() {
    //MONGO.stop();
  }

  /**
   * Before executing our test, let's deploy our verticle.
   * <p/>
   * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
   * completed its start sequence (thanks to `context.asyncAssertSuccess`).
   *
   * @param context the test context.
   */
  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();

    // Let's configure the verticle to listen on the 'test' port (randomly picked).
    // We create deployment options and set the _configuration_ json object:
    ServerSocket socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    DeploymentOptions options = new DeploymentOptions()
        .setConfig(new JsonObject()
            .put("http.port", port)
            .put("db_name", "DEFAULT_DB")
            .put("connection_string", "mongodb://localhost:" + MONGO_PORT)
        );

    // We pass the options as the second parameter of the deployVerticle method.
    vertx.deployVerticle(MainVerticle.class.getName(), options, context.asyncAssertSuccess());
  }

  /**
   * This method, called after our test, just cleanup everything by closing the vert.x instance
   *
   * @param context the test context
   */
  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }
 
  @Test
  public void checkThatWeCanAdd(TestContext context) {
    Async async = context.async();
    final String json = Json.encodePrettily(new Device("James", "Island", "", "", null, ""));
    vertx.createHttpClient().post(port, "localhost", "/api/devices")
        .putHeader("content-type", "application/json")
        .putHeader("content-length", Integer.toString(json.length()))
        .handler(response -> {
          context.assertEquals(response.statusCode(), 201);
          context.assertTrue(response.headers().get("content-type").contains("application/json"));
          response.bodyHandler(body -> {
            final Device device = Json.decodeValue(body.toString(), Device.class);
            context.assertEquals(device.getDeviceId(), "James");
            context.assertEquals(device.getDomain(), "Island");
            context.assertNotNull(device.getDeviceId());
            async.complete();
          });
        })
        .write(json)
        .end();
  }
  
  @Test
  public void checkThatWeCanRetrieveIndividualProduct(TestContext context) {   
    HttpClientRequest request =
    		vertx.createHttpClient().get(port, "localhost",
    	          "/api/devices")
    			.handler(response -> {
    		          context.assertEquals(response.statusCode(), 201);
    		          response.bodyHandler(
    	                  body -> {
    	                    System.out.println("Got a createResponse: " + body.toString());
    	                    final Device device = Json.decodeValue(body.toString(), Device.class);
    	                    context.assertEquals(device.getDeviceId(), "James");
    	                    context.assertEquals(device.getDomain(), "Island");
    	                    retrieveProduct(device, context);
    	                  });
    	          });
    	  request.end();
  }

  public void retrieveProduct(Device device, TestContext context) {
	
	  HttpClientRequest request =
	    		vertx.createHttpClient().get(port, "localhost",
	    	          "/api/devices"+device.getDeviceId())
	    			.handler(response -> {
	    		          context.assertEquals(response.statusCode(), 200);
	    		          response.bodyHandler(
	    	                  body -> {
	    	                    System.out.println("Got a createResponse: " + body.toString());
	    	                    final Device rdevice = Json.decodeValue(body.toString(), Device.class);
	    	                    context.assertEquals(rdevice.getDeviceId(), "James");
	    	                    context.assertEquals(rdevice.getDomain(), "Island");
	    	                    deleteProduct(device, context);
	    	                  });
	    	          });
	    	  request.end();
	
}
 
  public void deleteProduct(Device device, TestContext context) {
	 HttpClientRequest request =
	    		vertx.createHttpClient().delete(port, "localhost",
	    	          "/api/devices"+device.getDeviceId())
	    			.handler(response -> {
	    		          context.assertEquals(response.statusCode(), 404);
	    		          response.bodyHandler(
	    	                  body -> {
	    	                    System.out.println("Got a createResponse: " + body.toString());
	    	                    context.assertEquals(device.getDomain(), "");
	    	                    retrieveDeletedProduct(device, context);
	    	                  });
	    	          });
	    	  request.end();
	
	
}
  
  public void retrieveDeletedProduct(Device device, TestContext context) {
	 HttpClientRequest request =
	    		vertx.createHttpClient().get(port, "localhost",
	    	          "/api/devices"+device.getDeviceId())
	    			.handler(response -> {
	    		          context.assertEquals(response.statusCode(), 204);
	    		          response.bodyHandler(
	    	                  body -> {
	    	                    System.out.println("Got a createResponse: " + body.toString());
	    	                    context.assertEquals(device.getDomain(), "");
	    	                   
	    	                  });
	    	          });
	    	  request.end();
	
}
}