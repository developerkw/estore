package com.developerkw.estore.controller;

import com.developerkw.estore.discount.DiscountEnum;
import com.developerkw.estore.model.BasketItem;
import com.developerkw.estore.model.Product;
import com.developerkw.estore.model.ProductDto;
import com.developerkw.estore.model.PurchaseDto;
import com.developerkw.estore.model.ReceiptDto;
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

import java.math.BigDecimal;
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

    @GetMapping("/checkout")
    public ResponseEntity<ReceiptDto> checkout(Principal principal) {
        List<BasketItem> basketItems = basketRepository.findByUserName(principal.getName());
        if (basketItems.size() > 0) {
            return ResponseEntity.ok(generateReceiptDto(basketItems, principal.getName()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ReceiptDto generateReceiptDto(List<BasketItem> basketItems, String userName) {
        var purchaseDtoList = basketItems.stream()
            .map(this::purchaseDtoMapper)
            .toList();
        var receiptDto = new ReceiptDto();
        receiptDto.setUsername(userName);
        receiptDto.setPurchases(purchaseDtoList);
        BigDecimal totalPrice = purchaseDtoList.stream()
            .map(PurchaseDto::getDiscountedTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        receiptDto.setTotalPrice(totalPrice);
        return receiptDto;
    }

    private PurchaseDto purchaseDtoMapper(BasketItem basketItem) {
        var purchseDto = new PurchaseDto();
        purchseDto.setProduct(productDtoMapper(basketItem.getProduct()));
        purchseDto.setQuantity(basketItem.getQuantity());
        var price = basketItem.getProduct().getPrice();
        var total = new BigDecimal(purchseDto.getQuantity()).multiply(price);
        purchseDto.setOriginalTotalPrice(total);
        purchseDto.setDiscountedTotalPrice(total);
        applyDiscounts(purchseDto);
        return purchseDto;
    }

    private ProductDto productDtoMapper(Product product) {
        var productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setCategory(product.getCategory());
        productDto.setStock(product.getStock());
        productDto.setPrice(product.getPrice());
        productDto.setDiscounts(product.getDiscounts());
        return productDto;
    }

    private void applyDiscounts(PurchaseDto purchaseDto) {
        for (String discountName : purchaseDto.getProduct().getDiscounts()) {
            var discount = DiscountEnum.valueOf(discountName);
            discount.getDiscount().apply(purchaseDto);
        }
    }

}
