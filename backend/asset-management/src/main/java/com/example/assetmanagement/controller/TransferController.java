package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.Transfer;
import com.example.assetmanagement.service.TransferService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "http://localhost:5175")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) {
        this.service = service;
    }

    @GetMapping
    public List<Transfer> getAll() {
        return service.getAllTransfers();
    }

    @PostMapping
    public Transfer create(@RequestBody Transfer transfer) {
        return service.createTransfer(transfer);
    }
}
