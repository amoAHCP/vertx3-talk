package ch.trivadis.verticles.demo1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by Andy Moncsek on 15.02.16.
 */
public class Pong extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final EventBus eventBus = vertx.eventBus();
        eventBus.consumer("pong.addr",handler -> {
            System.out.println("Pong: "+handler.body().toString());
            handler.reply("pong");
        });
        startFuture.complete();
    }

}