package com.example.demo.dto.request;

import com.example.demo.domain.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating a device")
public record UpdateDeviceRequest(

        @Schema(description = "Device name", example = "iPhone 15 Pro Max")
        String name,

        @Schema(description = "Device brand", example = "Apple")
        String brand,

        @Schema(description = "Device state", example = "IN_USE")
        DeviceState state
) {}
