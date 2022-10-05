package com.magadiflo.client.app.models.services;

import com.magadiflo.client.app.models.Producto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final WebClient client;

    public ProductoServiceImpl(WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<Producto> findAll() {
        return this.client.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Producto.class);
    }

    @Override
    public Mono<Producto> findById(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.client.get().uri("/{id}", params)
                .accept()
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return null;
    }

    @Override
    public Mono<Producto> update(Producto producto, String id) {
        return null;
    }

    @Override
    public Mono<Void> delete(String id) {
        return null;
    }
}
