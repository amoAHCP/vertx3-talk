package ch.trivadis.verticles.demo1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by Andy Moncsek on 15.02.16.
 */
public class Ping extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final EventBus eventBus = vertx.eventBus();
        System.out.println("start Ping");

        vertx.deployVerticle(Pong.class.getName(), completionHandler -> {
            if (completionHandler.succeeded()) {
                sendMessage(eventBus);
                startFuture.complete();
            } else {
                vertx.close();
            }
        });

    }

    private void sendMessage(EventBus eventBus) {
        eventBus.send("pong.addr", "ping", handler -> {
            if (handler.succeeded()) {
                System.out.println("Ping: "+handler.result().body().toString());
                vertx.close();
            }
        });
    }


    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        Vertx.vertx().deployVerticle(Ping.class.getName(), options);
    }

}