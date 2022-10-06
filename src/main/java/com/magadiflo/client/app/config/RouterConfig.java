package com.magadiflo.client.app.config;

import com.magadiflo.client.app.handler.ProductoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> rutas(ProductoHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/client"), handler::listar)
                .andRoute(RequestPredicates.GET("/api/client/{id}"), handler::ver);
    }

}
