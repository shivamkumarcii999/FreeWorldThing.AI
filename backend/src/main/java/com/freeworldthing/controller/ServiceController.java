package com.freeworldthing.controller;

import com.freeworldthing.model.ServiceListing;
import com.freeworldthing.repository.ServiceListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceListingRepository serviceRepository;

    @GetMapping
    public ResponseEntity<List<ServiceListing>> getAllServices(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            return ResponseEntity.ok(serviceRepository.findByCategoryIgnoreCase(category));
        }
        return ResponseEntity.ok(serviceRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceListing> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServiceListing> createService(@RequestBody ServiceListing serviceListing) {
        return ResponseEntity.ok(serviceRepository.save(serviceListing));
    }
}
