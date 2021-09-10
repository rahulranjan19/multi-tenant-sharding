package com.multitenant.web.delivery;

import com.multitenant.repository.DeliveryRepository;
import com.multitenant.repository.entity.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    public List<Delivery> getAllDeliveries(String tenantId) {
        return deliveryRepository.findAllByFacilityNum(tenantId);
    }

    public void updateDelivery(Integer id) {
        final Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        deliveryOptional.ifPresent(delivery -> delivery.setScheduledOn(new Date(Instant.now().toEpochMilli())));
    }

    public void addDelivery(Integer delNumber, String facility) {
        deliveryRepository.save(Delivery.builder().deliveryNumber(delNumber).facilityNum(facility).scheduledOn(new Date(Instant.now().toEpochMilli())).build());
    }
}
