package com.multitenant.repository;

import com.multitenant.repository.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    @Query("select del from Delivery del where del.facilityNum = :tenantId")
    List<Delivery> findAllByFacilityNum(String tenantId);
}
