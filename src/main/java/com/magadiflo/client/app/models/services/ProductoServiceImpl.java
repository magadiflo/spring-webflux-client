package com.magadiflo.client.app.models.services;

import com.magadiflo.client.app.models.Producto;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
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
        return this.client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(producto))
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Producto> update(Producto producto, String id) {
        return this.client.put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(producto))
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.client.delete()
                .uri("/{id}", Collections.singletonMap("id", id))
                .exchangeToMono(clientResponse -> Mono.empty());
    }

    @Override
    public Mono<Producto> upload(FilePart file, String id) {
        MultipartBodyBuilder parts = new MultipartBodyBuilder();
        parts.asyncPart("file", file.content(), DataBuffer.class).headers(h -> {
           h.setContentDispositionFormData("file", file.filename());
        });
        return this.client.post()
                .uri("/upload/{id}", Collections.singletonMap("id", id))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(parts.build())
                .retrieve()
                .bodyToMono(Producto.class);
    }
}
