package com.jacs.items.ms.itemsms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.jacs.items.ms.itemsms.models.Item;
import com.jacs.items.ms.itemsms.models.Product;

@Primary
@Service
public class ItemServiceWebClient implements ItemService {
    
    private final WebClient.Builder client;

    public ItemServiceWebClient(Builder client) {
        this.client = client;
    }

    @Override
    public List<Item> findAll() {

        return this.client.build()
                .get()
                .uri("http://ms-products")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Product.class)
                .map(product -> new Item(product, 1))
                .collectList()
                .block(); // Blocking for simplicity, consider using reactive patterns in production
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = this.client.build()
                .get()
                .uri("http://ms-products/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                .map(product -> new Item(product, 1))
                .block(); // Blocking for simplicity, consider using reactive patterns in production

        return Optional.ofNullable(item);
    }

}
