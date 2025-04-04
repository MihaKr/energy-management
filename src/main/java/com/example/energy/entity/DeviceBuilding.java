package com.example.energy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "device_building")
@IdClass(DeviceBuildingId.class)
public class DeviceBuilding {
    @Id
    @Column(name = "device_id", nullable = false)
    private Integer deviceId;

    @Id
    @Column(name = "building_id", nullable = false)
    private Integer buildingId;

    @NotNull
    @Column(name = "installed_since", nullable = false)
    private Instant installedSince;

    @ManyToOne
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "building_id", insertable = false, updatable = false)
    private Building building;

    public DeviceBuilding(Integer deviceId, Integer buildingId, Instant installedSince) {
        this.deviceId = deviceId;
        this.buildingId = buildingId;
        this.installedSince = installedSince;
    }

    public DeviceBuilding() {
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}