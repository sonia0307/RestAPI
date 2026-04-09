package com.example.demo.repository;

import com.example.demo.domain.DeviceState;
import com.example.demo.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    List<Device> findAllByBrand(String brand);

    List<Device> findAllByState(DeviceState state);
}

