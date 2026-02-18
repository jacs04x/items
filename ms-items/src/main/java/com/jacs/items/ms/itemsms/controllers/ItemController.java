package com.jacs.items.ms.itemsms.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jacs.items.ms.itemsms.models.Item;
import com.jacs.items.ms.itemsms.models.Product;
import com.jacs.items.ms.itemsms.services.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RefreshScope
@RestController
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final CircuitBreakerFactory cb;

    @Value("${configuracion.texto}")
    private String text;

    @Autowired
    private Environment env;

    public ItemController(ItemService itemService, CircuitBreakerFactory cb) {
        this.itemService = itemService;
        this.cb = cb;
    }

    @GetMapping("/configs")
    public ResponseEntity<?> getMethodName(@Value("${server.port}") String port) {
        Map<String, String> response = new HashMap<>();
        response.put("texto", text + " Puerto: " + port);
        logger.info(text);
        if(env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
            response.put("nombre.autor", env.getProperty("configuracion.autor.nombre"));
            response.put("nombre.autor.email", env.getProperty("configuracion.autor.email"));
        }
        return ResponseEntity.ok(response);
    }
    

    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
    
    Optional <Item> item = cb.create("items").run(() -> itemService.findById(id),  throwable -> {
        logger.error(throwable.getMessage());
        Product product = new Product();
        product.setId(1L);
        product.setName("Pantalla prueba");
        product.setCreatedAt(LocalDateTime.now());
        product.setPrice(200.0);
        Item itemFallback = new Item(product, 5);
        return Optional.of(itemFallback);
    });

        if (item.isEmpty()) {
            return ResponseEntity.status(204).body(Collections.singletonMap("message", "No existe el producto"));
        }
        return ResponseEntity.ok(item.get());
    }
    

    @CircuitBreaker(name = "items" , fallbackMethod = "getFallBackMethod")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getItemById2(@PathVariable Long id) {
    
        Optional <Item> item =  itemService.findById(id);

        if (item.isEmpty()) {
            return ResponseEntity.status(204).body(Collections.singletonMap("message", "No existe el producto"));
        }
        return ResponseEntity.ok(item.get());
    }


    public ResponseEntity<?> getFallBackMethod(Throwable throwable) {
        logger.error(throwable.getMessage());
        Product product = new Product();
        product.setId(1L);
        product.setName("Pantalla prueba");
        product.setCreatedAt(LocalDateTime.now());
        product.setPrice(200.0);
        Item itemFallback = new Item(product, 5);
        return ResponseEntity.ok(itemFallback);
    }

    @CircuitBreaker(name = "items" ,fallbackMethod = "getFallBackMethod2")
    @TimeLimiter(name = "items")
    @GetMapping("/details3/{id}")
    public CompletableFuture<?> getItemById3(@PathVariable Long id) {
    
       return CompletableFuture.supplyAsync(() -> {
        Optional <Item> item =  itemService.findById(id);

        if (item.isEmpty()) {
            return ResponseEntity.status(204).body(Collections.singletonMap("message", "No existe el producto"));
        }
        return ResponseEntity.ok(item.get());
       });  
    }

    public CompletableFuture<?> getFallBackMethod2(Throwable throwable) {

            return CompletableFuture.supplyAsync(() -> {
        logger.error(throwable.getMessage());
        Product product = new Product();
        product.setId(1L);
        product.setName("Pantalla prueba");
        product.setCreatedAt(LocalDateTime.now());
        product.setPrice(200.0);
        Item itemFallback = new Item(product, 5);
        return ResponseEntity.ok(itemFallback);

                });

    }

}
