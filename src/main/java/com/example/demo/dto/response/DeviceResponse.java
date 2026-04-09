package com.example.demo.dto.response;

import com.example.demo.domain.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Device resource representation")
public record DeviceResponse(

        @Schema(description = "Unique device identifier")
        UUID id,

        @Schema(description = "Device name", example = "iPhone 15 Pro")
        String name,

        @Schema(description = "Device brand", example = "Apple")
        String brand,

        @Schema(description = "Device state", example = "AVAILABLE")
        DeviceState state,

        @Schema(description = "Timestamp when the device was created")
        Instant createdAt
) {}
