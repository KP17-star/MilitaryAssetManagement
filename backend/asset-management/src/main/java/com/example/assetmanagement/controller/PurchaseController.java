package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.Purchase;
import com.example.assetmanagement.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:5175")
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Purchase> getAll() {
        return service.getAllPurchases();
    }

    @PostMapping
    public Purchase create(@RequestBody Purchase purchase) {
        return service.createPurchase(purchase);
    }
}
