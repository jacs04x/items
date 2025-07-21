package com.jacs.items.ms.itemsms.services;

import java.util.List;
import java.util.Optional;

import com.jacs.items.ms.itemsms.models.Item;

public interface ItemService {

    List<Item> findAll();

    Optional<Item> findById(Long id);

}
