package ch.trivadis.verticles.demo2;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;

/**
 * Created by Andy Moncsek on 15.02.16.
 */
public class HTTPVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("start HTTPVerticle: "+this);
        final HttpServer httpServer = vertx.createHttpServer();

        // add httpRequest
        httpServer.requestHandler(request -> {
            System.out.println(request.path());
            if (request.path().equals("/hello")) {
                request.response().end("world");
            }
        });

        // add websocketHandler
        httpServer.websocketHandler(socket -> {
            if (socket.path().equals("/helloWS")) {
                // register message handler
                socket.handler(messageHandler -> {
                    String message = new String(messageHandler.getBytes());
                    socket.write(Buffer.buffer().appendString(message + " world"));
                });
            }
        });

        httpServer.listen(config().getInteger("port",8080), "localhost", res -> updateStartFutureStatus(startFuture, res));
    }

    private void updateStartFutureStatus(Future<Void> startFuture, AsyncResult<HttpServer> res) {
        if (res.succeeded()) {
            System.out.println("Server is now listening!");
            startFuture.complete();
        } else {
            System.out.println("Failed to bind! on port: "+config().getInteger("port",8080)+"  " + res.cause());
            startFuture.failed();
        }
    }

    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        Vertx.vertx().deployVerticle(HTTPVerticle.class.getName(), options);
    }

}
