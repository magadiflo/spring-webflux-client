package com.magadiflo.client.app.handler;

import com.magadiflo.client.app.models.Producto;
import com.magadiflo.client.app.models.services.ProductoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Component
public class ProductoHandler {

    private final ProductoService service;

    public ProductoHandler(ProductoService service) {
        this.service = service;
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.service.findAll(), Producto.class);
    }

    public Mono<ServerResponse> ver(ServerRequest request) {
        String id = request.pathVariable("id");
        return this.service.findById(id)
                .flatMap(producto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(producto)
                        .switchIfEmpty(ServerResponse.notFound().build())
                );
    }

    public Mono<ServerResponse> crear(ServerRequest request) {
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        return producto.flatMap(p -> {
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }
            return this.service.save(p);
        }).flatMap(p -> ServerResponse
                .created(URI.create("/api/client/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)
        );
    }

    public Mono<ServerResponse> editar(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> producto = request.bodyToMono(Producto.class);

        return producto.flatMap(p -> ServerResponse
                .created(URI.create("/api/client/".concat(id)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.service.update(p, id), Producto.class)
        );
    }

    public Mono<ServerResponse> eliminar(ServerRequest request) {
        String id = request.pathVariable("id");
        return this.service.delete(id).then(ServerResponse.noContent().build());
    }

}
