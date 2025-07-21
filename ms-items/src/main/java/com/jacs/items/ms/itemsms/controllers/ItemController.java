package com.jacs.items.ms.itemsms.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jacs.items.ms.itemsms.models.Item;
import com.jacs.items.ms.itemsms.services.ItemService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class ItemController {

    private final ItemService itemService;
    
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
    Optional <Item> item = itemService.findById(id);
        if (item.isEmpty()) {
            return ResponseEntity.status(204).body(Collections.singletonMap("message", "No existe el producto"));
        }
        return ResponseEntity.ok(item.get());
    }
    

}
