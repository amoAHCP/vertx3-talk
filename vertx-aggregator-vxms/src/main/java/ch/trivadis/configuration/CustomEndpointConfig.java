package ch.trivadis.configuration;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.jacpfx.vertx.rest.configuration.EndpointConfiguration;

/**
 * Created by Andy Moncsek on 18.02.16.
 */
public class CustomEndpointConfig implements EndpointConfiguration {
    public CorsHandler corsHandler() {
        return CorsHandler.create("*").
                allowedMethod(io.vertx.core.http.HttpMethod.GET).
                allowedMethod(io.vertx.core.http.HttpMethod.POST).
                allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS).
                allowedMethod(io.vertx.core.http.HttpMethod.PUT).
                allowedMethod(io.vertx.core.http.HttpMethod.DELETE).
                allowedHeader("Content-Type").
                allowedHeader("X-Requested-With");
    }

   // public StaticHandler staticHandler() {
    //    return StaticHandler.create();
   // }


    public void customRouteConfiguration(Vertx vertx, Router router) {
        MetricsService service = MetricsService.create(vertx);

        BridgeOptions options = new BridgeOptions().
                addOutboundPermitted(
                        new PermittedOptions().
                                setAddress("metrics")
                );

        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options));
        router.route("/metrics/*").handler(StaticHandler.create());

        // Send a metrics events every second
        vertx.setPeriodic(1000, t -> {
            JsonObject metrics = service.getMetricsSnapshot(vertx.eventBus());
            vertx.eventBus().publish("metrics", metrics);
        });
    }
}
