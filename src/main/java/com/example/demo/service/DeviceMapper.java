package com.example.demo.service;

import com.example.demo.domain.Device;
import com.example.demo.dto.request.CreateDeviceRequest;
import com.example.demo.dto.response.DeviceResponse;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {

    public Device toEntity(CreateDeviceRequest request) {
        if (request == null) {
            return null;
        }

        return Device.builder()
                .name(request.name())
                .brand(request.brand())
                .state(request.state())
                .build();
    }

    public DeviceResponse toResponse(Device device) {
        if (device == null) {
            return null;
        }

        return new DeviceResponse(
                device.getId(),
                device.getName(),
                device.getBrand(),
                device.getState(),
                device.getCreatedAt()
        );
    }
}
