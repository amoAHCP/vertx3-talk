package ch.trivadis.verticles.demo2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * Created by Andy Moncsek on 15.02.16.
 */
public class TCPVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("start TCPVerticle: "+this);
        NetServer server = vertx.createNetServer();
        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                // Just echo back the data
                socket.write(buffer);
            });
        });
        server.listen(config().getInteger("port",8080), "localhost");
    }



    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        Vertx.vertx().deployVerticle(TCPVerticle.class.getName(), options);
    }

}
