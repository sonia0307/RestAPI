package com.example.demo.controller;

import com.example.demo.domain.DeviceState;
import com.example.demo.dto.request.CreateDeviceRequest;
import com.example.demo.dto.request.UpdateDeviceRequest;
import com.example.demo.dto.response.DeviceResponse;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Tag(name = "Devices", description = "Device management API")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @Operation(summary = "Create a new device")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody CreateDeviceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Fully or partially update a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DeviceResponse> update(
            @Parameter(description = "Device ID") @PathVariable UUID id,
            @RequestBody UpdateDeviceRequest request) {
        return ResponseEntity.ok(deviceService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a single device by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DeviceResponse> findById(
            @Parameter(description = "Device ID") @PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Fetch all devices, optionally filtered by brand or state")
    @ApiResponse(responseCode = "200", description = "List of devices")
    public ResponseEntity<List<DeviceResponse>> findAll(
            @Parameter(description = "Filter by brand") @RequestParam(required = false) String brand,
            @Parameter(description = "Filter by state") @RequestParam(required = false) DeviceState state) {

        if (brand != null && state != null) {
            throw new IllegalArgumentException("Cannot filter by both brand and state simultaneously");
        }
        if (brand != null) {
            return ResponseEntity.ok(deviceService.findByBrand(brand));
        }
        if (state != null) {
            return ResponseEntity.ok(deviceService.findByState(state));
        }
        return ResponseEntity.ok(deviceService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a device")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Cannot delete an in-use device",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Device ID") @PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
