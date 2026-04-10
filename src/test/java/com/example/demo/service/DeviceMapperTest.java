package com.example.demo.service;

import com.example.demo.domain.Device;
import com.example.demo.domain.DeviceState;
import com.example.demo.dto.request.CreateDeviceRequest;
import com.example.demo.dto.response.DeviceResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeviceMapperTest {

    private final DeviceMapper deviceMapper = new DeviceMapper();

    @Test
    void toEntityShouldMapCreateRequest() {
        CreateDeviceRequest request = new CreateDeviceRequest("iPhone 15 Pro", "Apple", DeviceState.AVAILABLE);

        Device device = deviceMapper.toEntity(request);

        assertNotNull(device);
        assertEquals("iPhone 15 Pro", device.getName());
        assertEquals("Apple", device.getBrand());
        assertEquals(DeviceState.AVAILABLE, device.getState());
        assertNull(device.getId());
    }

    @Test
    void toEntityShouldReturnNullForNullRequest() {
        assertNull(deviceMapper.toEntity(null));
    }

    @Test
    void toResponseShouldMapDevice() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-09T10:15:30Z");
        Device device = Device.builder()
                .id(id)
                .name("Pixel 8")
                .brand("Google")
                .state(DeviceState.INACTIVE)
                .createdAt(createdAt)
                .build();

        DeviceResponse response = deviceMapper.toResponse(device);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Pixel 8", response.name());
        assertEquals("Google", response.brand());
        assertEquals(DeviceState.INACTIVE, response.state());
        assertEquals(createdAt, response.createdAt());
    }

    @Test
    void toResponseShouldReturnNullForNullDevice() {
        assertNull(deviceMapper.toResponse(null));
    }
}
