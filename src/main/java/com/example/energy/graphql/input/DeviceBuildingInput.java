package com.example.energy.graphql.input;

import com.example.energy.entity.Building;
import com.example.energy.entity.Device;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class DeviceBuildingInput {
    private Integer deviceId;
    private Integer buildingId;
    private Instant installedSince;

    public DeviceBuildingInput() {
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public Instant getInstalledSince() {
        return installedSince;
    }

    public void setInstalledSince(Instant installedSince) {
        this.installedSince = installedSince;
    }
}
