package com.example.demo.service;

import com.example.demo.domain.DeviceState;
import com.example.demo.dto.request.CreateDeviceRequest;
import com.example.demo.dto.request.UpdateDeviceRequest;
import com.example.demo.dto.response.DeviceResponse;

import java.util.List;
import java.util.UUID;

public interface DeviceService {

    DeviceResponse create(CreateDeviceRequest request);

    DeviceResponse update(UUID id, UpdateDeviceRequest request);

    DeviceResponse findById(UUID id);

    List<DeviceResponse> findAll();

    List<DeviceResponse> findByBrand(String brand);

    List<DeviceResponse> findByState(DeviceState state);

    void delete(UUID id);
}