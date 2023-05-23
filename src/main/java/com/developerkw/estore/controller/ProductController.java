package com.developerkw.estore.controller;

import com.developerkw.estore.model.Product;
import com.developerkw.estore.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping()
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        //TODO may return URL instead with the matching openapi doc
        Product productSaved = productRepository.save(product);
        return new ResponseEntity<>(productSaved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Product> findById(@PathVariable Long requestedId) {
        Optional<Product> product = productRepository.findById(requestedId);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public List<Product> findAll(Principal principal) {
        Iterable<Product> products = productRepository.findAll();

        var list = new ArrayList<Product>();
        if (products.iterator().hasNext()) {
            products.forEach(list::add);
            return list;
        } else {
            return null;
        }
    }
}
