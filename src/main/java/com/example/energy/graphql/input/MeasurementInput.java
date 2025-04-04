package com.example.energy.graphql.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

public class MeasurementInput {
    private Integer id;
    private Instant timestamp;
    private Float energyKwh;
    private Integer deviceId;
    private Integer buildingId;

    public MeasurementInput() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Float getEnergyKwh() {
        return energyKwh;
    }

    public void setEnergyKwh(Float energyKwh) {
        this.energyKwh = energyKwh;
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
}
