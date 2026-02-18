package com.jacs.items.ms.itemsms.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Product.class)
                .map(product -> new Item(product, 1))
                .collectList()
                .block(); // Blocking for simplicity, consider using reactive patterns in production
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = null;
        //try{
        item = this.client.build()
                .get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                .map(product -> new Item(product, 1))
                .block(); // Blocking for simplicity, consider using reactive patterns in production
                return Optional.of(item);
                
        //}catch(WebClientResponseException e){
        //    return Optional.empty();
        //}


    }

    @Override
    public Product save(Product product) {
            return this.client.build()
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(product)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block(); // Blocking for simplicity, consider using reactive patterns in production
    }

    @Override
    public Product update(Long id, Product product) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.client.build()
                .put()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class).block(); // Blocking for simplicity, consider using reactive patterns in production
    }

    @Override
    public void delete(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        this.client.build()
                .delete()
                .uri("/{id}", params)
                .retrieve()
                .bodyToMono(Void.class)
                .block(); // Blocking for simplicity, consider using reactive patterns in production
    }

}
