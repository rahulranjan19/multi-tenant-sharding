package com.multitenant.web.delivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class DeliveryApi {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/deliveries")
    public ResponseEntity<?> getDeliveries(@RequestHeader String tenantId) {
        return ResponseEntity.ok(deliveryService.getAllDeliveries(tenantId));
    }
    @PostMapping("/deliveries/{deliveryId}")
    public ResponseEntity<?> saveDeliveries(@PathVariable Integer deliveryId, @RequestHeader String tenantId) {
        deliveryService.addDelivery(deliveryId, tenantId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/deliveries/{deliveryId}")
    public ResponseEntity<?> updateDeliveries(@PathVariable Integer deliveryId) {
        deliveryService.updateDelivery(deliveryId);
        return ResponseEntity.ok().build();
    }
}
