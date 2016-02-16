package ch.trivadis.demo2;

import ch.trivadis.verticles.demo2.HTTPVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.impl.ws.WebSocketFrameImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.fakecluster.FakeClusterManager;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Andy Moncsek on 15.02.16.
 */

public class HTTPVerticleTest extends VertxTestBase {
    private Vertx vertx;
    private static AtomicInteger port = new AtomicInteger(8080);





    @Before
    public void startVerticles() throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        options.setConfig(new JsonObject().put("port", port.incrementAndGet()));
        CountDownLatch latch = new CountDownLatch(1);
        getVertx().deployVerticle(HTTPVerticle.class.getName(), options, asyncResult -> {
            // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
            System.out.println("start service: " + asyncResult.succeeded());
            assertTrue(asyncResult.succeeded());
            assertNotNull("deploymentID should not be null", asyncResult.result());
            latch.countDown();

        });

        latch.await(20000, TimeUnit.MILLISECONDS);
        System.out.println("start on port:" + port.get());
    }


    /**
     * Let's ensure that our application behaves correctly.
     *
     */
    @Test
    public void testHTTPConnection() {
        // This test is asynchronous, so get an async handler to inform the test when we are done.
        final HttpClient httpClient =  getVertx().createHttpClient();
        System.out.println("connect on port:" + port.get());
        httpClient.getNow(port.get(), "localhost", "/hello", response -> {
            response.handler(body -> {
                assertTrue(body.toString().contains("world"));
                httpClient.close();
                testComplete();
            });
        });

    }

    /**
     * Let's ensure WebSockets behaves correctly.
     *
     */
    @Test
    public void testWebSocketConnection() {
        // This test is asynchronous, so get an async handler to inform the test when we are done.

        final HttpClient httpClient = getVertx().createHttpClient();
        System.out.println("connect on port:" + port.get());
        httpClient.websocket(port.get(), "localhost", "/helloWS", ws -> {
            ws.handler((data) -> {
                assertNotNull(data.getBytes());
                String response = new String(data.getBytes());
                System.out.println("response: " + response);
                assertEquals("hello world", response);
                ws.close();
                httpClient.close();
                testComplete();
            });

            ws.writeFrame(new WebSocketFrameImpl("hello"));
        });


        await(10000, TimeUnit.MILLISECONDS);

    }


    protected int getNumNodes() {
        return 1;
    }

    protected Vertx getVertx() {
        return vertices[0];
    }

    @Override
    protected ClusterManager getClusterManager() {
        return new FakeClusterManager();
    }


    private HttpClient client;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        startNodes(getNumNodes());

    }


}
