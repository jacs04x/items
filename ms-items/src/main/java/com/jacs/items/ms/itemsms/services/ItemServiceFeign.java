package com.jacs.items.ms.itemsms.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jacs.items.ms.itemsms.client.ProductFeignClient;
import com.jacs.items.ms.itemsms.models.Item;
import com.jacs.items.ms.itemsms.models.Product;

import feign.FeignException.FeignClientException;

@Service
public class ItemServiceFeign implements ItemService {

    private final ProductFeignClient productFeignClient;

    public ItemServiceFeign(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    @Override
    public List<Item> findAll() {
        List<Product> products = productFeignClient.findAll();
        return products.stream()
                .map(product -> new Item(product, 1)) // Assuming quantity is 1 for simplicity
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        try{
            Product product = productFeignClient.findById(id);
            if (product == null) {
                return Optional.empty();
            }
            return Optional.of(product).map(p -> new Item(p, 1));
        }catch(FeignClientException e){
            return Optional.empty();
        }
    }


}
