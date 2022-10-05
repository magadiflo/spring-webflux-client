package com.magadiflo.client.app.models.services;

import com.magadiflo.client.app.models.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<Producto> findAll();

    Mono<Producto> findById(String id);

    Mono<Producto> save(Producto producto);

    Mono<Producto> update(Producto producto, String id);

    Mono<Void> delete(String id);

}
