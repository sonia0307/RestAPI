package com.example.demo.service;

import com.example.demo.domain.Device;
import com.example.demo.domain.DeviceState;
import com.example.demo.dto.request.CreateDeviceRequest;
import com.example.demo.dto.request.UpdateDeviceRequest;
import com.example.demo.dto.response.DeviceResponse;
import com.example.demo.exception.DeviceConstraintViolationException;
import com.example.demo.exception.DeviceNotFoundException;
import com.example.demo.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    @Transactional
    public DeviceResponse create(CreateDeviceRequest request) {
        log.info("Creating device with name='{}', brand='{}'", request.name(), request.brand());
        Device device = deviceMapper.toEntity(request);
        Device saved = deviceRepository.save(device);
        log.info("Device created with id='{}'", saved.getId());
        return deviceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DeviceResponse update(UUID id, UpdateDeviceRequest request) {
        log.info("Updating device id='{}'", id);
        Device device = findDeviceById(id);

        boolean isInUse = DeviceState.IN_USE.equals(device.getState());
        boolean nameOrBrandChanging = (request.name() != null && !request.name().equals(device.getName()))
                || (request.brand() != null && !request.brand().equals(device.getBrand()));

        if (isInUse && nameOrBrandChanging) {
            throw new DeviceConstraintViolationException(
                    "Name and brand cannot be updated while device is IN_USE");
        }

        if (request.name() != null) {
            device.setName(request.name());
        }
        if (request.brand() != null) {
            device.setBrand(request.brand());
        }
        if (request.state() != null) {
            device.setState(request.state());
        }

        Device updated = deviceRepository.save(device);
        log.info("Device id='{}' updated successfully", id);
        return deviceMapper.toResponse(updated);
    }

    @Override
    public DeviceResponse findById(UUID id) {
        log.debug("Fetching device id='{}'", id);
        return deviceMapper.toResponse(findDeviceById(id));
    }

    @Override
    public List<DeviceResponse> findAll() {
        log.debug("Fetching all devices");
        return deviceRepository.findAll().stream()
                .map(deviceMapper::toResponse)
                .toList();
    }

    @Override
    public List<DeviceResponse> findByBrand(String brand) {
        log.debug("Fetching devices by brand='{}'", brand);
        return deviceRepository.findAllByBrand(brand).stream()
                .map(deviceMapper::toResponse)
                .toList();
    }

    @Override
    public List<DeviceResponse> findByState(DeviceState state) {
        log.debug("Fetching devices by state='{}'", state);
        return deviceRepository.findAllByState(state).stream()
                .map(deviceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting device id='{}'", id);
        Device device = findDeviceById(id);

        if (DeviceState.IN_USE.equals(device.getState())) {
            throw new DeviceConstraintViolationException(
                    "Cannot delete a device that is currently IN_USE");
        }

        deviceRepository.delete(device);
        log.info("Device id='{}' deleted successfully", id);
    }

    private Device findDeviceById(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }
}
