package com.jacs.items.ms.itemsms.services;

import java.util.List;
import java.util.Optional;

import com.jacs.items.ms.itemsms.models.Item;
import com.jacs.items.ms.itemsms.models.Product;

public interface ItemService {

    List<Item> findAll();

    Optional<Item> findById(Long id);

    Product save (Product product);

    Product update (Long id, Product product);

    void delete (Long id);

}
