package ch.trivadis.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by Andy Moncsek on 15.02.16.
 */
public class BasicVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        startFuture.complete();
    }

}
