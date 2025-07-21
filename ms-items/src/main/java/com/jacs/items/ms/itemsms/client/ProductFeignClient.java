package com.jacs.items.ms.itemsms.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jacs.items.ms.itemsms.models.Product;

@FeignClient(name = "ms-products", url = "${product.service.url}")
public interface ProductFeignClient {

    @GetMapping
    public List<Product> findAll();

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id);

}
