package com.example.demo.service;

import com.example.demo.domain.Device;
import com.example.demo.domain.DeviceState;
import com.example.demo.dto.request.CreateDeviceRequest;
import com.example.demo.dto.request.UpdateDeviceRequest;
import com.example.demo.dto.response.DeviceResponse;
import com.example.demo.exception.DeviceConstraintViolationException;
import com.example.demo.exception.DeviceNotFoundException;
import com.example.demo.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    private DeviceServiceImpl deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceServiceImpl(deviceRepository, new DeviceMapper());
    }

    @Test
    void testCreateShouldPersistAndReturnResponse() {
        CreateDeviceRequest request = new CreateDeviceRequest("iPhone 15 Pro", "Apple", DeviceState.AVAILABLE);
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-09T10:15:30Z");
        Device savedDevice = Device.builder()
                .id(id)
                .name(request.name())
                .brand(request.brand())
                .state(request.state())
                .createdAt(createdAt)
                .build();

        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);

        DeviceResponse response = deviceService.create(request);

        assertEquals(id, response.id());
        assertEquals("iPhone 15 Pro", response.name());
        assertEquals("Apple", response.brand());
        assertEquals(DeviceState.AVAILABLE, response.state());
    }

    @Test
    void testUpdateShouldRejectChange() {
        UUID id = UUID.randomUUID();
        Device existing = Device.builder()
                .id(id)
                .name("Current")
                .brand("Apple")
                .state(DeviceState.IN_USE)
                .build();
        UpdateDeviceRequest request = new UpdateDeviceRequest("Updated", null, null);

        when(deviceRepository.findById(id)).thenReturn(Optional.of(existing));

        DeviceConstraintViolationException exception = assertThrows(
                DeviceConstraintViolationException.class,
                () -> deviceService.update(id, request)
        );

        assertTrue(exception.getMessage().contains("cannot be updated while device is IN_USE"));
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void testUpdateShouldAllowStateChange() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-09T10:15:30Z");
        Device existing = Device.builder()
                .id(id)
                .name("Current")
                .brand("Apple")
                .state(DeviceState.IN_USE)
                .createdAt(createdAt)
                .build();
        UpdateDeviceRequest request = new UpdateDeviceRequest(null, null, DeviceState.INACTIVE);

        when(deviceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(deviceRepository.save(existing)).thenReturn(existing);

        DeviceResponse response = deviceService.update(id, request);
        assertEquals(DeviceState.INACTIVE, response.state());
    }

    @Test
    void testFindByIdShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(deviceRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(DeviceNotFoundException.class, () -> deviceService.findById(id));
    }

    @Test
    void testFindByBrandShouldMapResults() {
        Device samsung = Device.builder()
                .id(UUID.randomUUID())
                .name("Galaxy S24 Ultra")
                .brand("Samsung")
                .state(DeviceState.AVAILABLE)
                .createdAt(Instant.parse("2026-04-09T10:15:30Z"))
                .build();

        when(deviceRepository.findAllByBrand("Samsung")).thenReturn(List.of(samsung));

        List<DeviceResponse> responses = deviceService.findByBrand("Samsung");

        assertEquals(1, responses.size());
        assertEquals("Samsung", responses.getFirst().brand());
    }

    @Test
    void testDeleteShouldReject() {
        UUID id = UUID.randomUUID();
        Device existing = Device.builder()
                .id(id)
                .name("Current")
                .brand("Apple")
                .state(DeviceState.IN_USE)
                .build();

        when(deviceRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(DeviceConstraintViolationException.class, () -> deviceService.delete(id));
        verify(deviceRepository, never()).delete(any(Device.class));
    }

    @Test
    void deleteShouldRemoveDeviceWhenAllowed() {
        UUID id = UUID.randomUUID();
        Device existing = Device.builder()
                .id(id)
                .name("Current")
                .brand("Apple")
                .state(DeviceState.AVAILABLE)
                .build();

        when(deviceRepository.findById(id)).thenReturn(Optional.of(existing));

        deviceService.delete(id);

        verify(deviceRepository).delete(existing);
    }
}
