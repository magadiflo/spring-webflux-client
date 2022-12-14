package com.magadiflo.client.app.handler;

import com.magadiflo.client.app.models.Producto;
import com.magadiflo.client.app.models.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        return this.errorHandler(
                this.service.findById(id)
                        .flatMap(producto -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(producto)
                                .switchIfEmpty(ServerResponse.notFound().build())
                        )
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
        ).onErrorResume(throwable -> {
            WebClientResponseException errorResponse = (WebClientResponseException) throwable;
            if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(errorResponse.getResponseBodyAsString());
            }
            return Mono.error(errorResponse);
        });
    }

    public Mono<ServerResponse> editar(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> producto = request.bodyToMono(Producto.class);

        return this.errorHandler(
                producto
                        .flatMap(p -> this.service.update(p, id))
                        .flatMap(p -> ServerResponse
                                .created(URI.create("/api/client/".concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(p)
                        )
        );
    }

    public Mono<ServerResponse> eliminar(ServerRequest request) {
        String id = request.pathVariable("id");
        return this.errorHandler(
                this.service.delete(id).then(ServerResponse.noContent().build())
        );
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        String id = request.pathVariable("id");
        return this.errorHandler(
                request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                        .cast(FilePart.class)
                        .flatMap(filePart -> this.service.upload(filePart, id))
                        .flatMap(producto -> ServerResponse
                                .created(URI.create("/api/client/".concat(producto.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(producto)
                        )
        );
    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> responseMono) {
        return responseMono.onErrorResume(throwable -> {
            WebClientResponseException errorResponse = (WebClientResponseException) throwable;
            if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "No existe el producto ".concat(errorResponse.getMessage()));
                body.put("timestamp", new Date());
                body.put("status", errorResponse.getStatusCode().value());
                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body); //Si con el not_found queremos enviar alg??n json de respuesta
            }
            return Mono.error(errorResponse);
        });
    }

}
