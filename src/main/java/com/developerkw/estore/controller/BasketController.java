package com.developerkw.estore.controller;

import com.developerkw.estore.model.BasketItem;
import com.developerkw.estore.repository.BasketRepository;
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
import java.util.List;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketRepository basketRepository;

    public BasketController(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @PostMapping()
    public ResponseEntity<BasketItem> addToBasket(@RequestBody BasketItem basketItem, Principal principal) {
        //TODO may return URL instead with the matching openapi doc
        var basketItemWithOwner = new BasketItem();
        basketItemWithOwner.setUserName(principal.getName());
        basketItemWithOwner.setProduct(basketItem.getProduct());
        basketItemWithOwner.setQuantity(basketItem.getQuantity());
        var basketItemSaved = basketRepository.save(basketItem);
        return new ResponseEntity<>(basketItemSaved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteBasketItem(@PathVariable Long id, Principal principal) {
        if (basketRepository.existsByIdAndUserName(id, principal.getName())) {
            basketRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<BasketItem>> getBasket(Principal principal) {
        List<BasketItem> basketItems = basketRepository.findByUserName(principal.getName());
        if (basketItems.size() > 0) {
            return ResponseEntity.ok(basketItems);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
