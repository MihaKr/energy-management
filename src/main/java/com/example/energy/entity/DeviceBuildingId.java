package com.example.energy.entity;

import java.io.Serializable;
import java.util.Objects;

public class DeviceBuildingId implements Serializable {
    private Integer deviceId;
    private Integer buildingId;

    public DeviceBuildingId(Integer deviceId, Integer buildingId) {
        this.deviceId = deviceId;
        this.buildingId = buildingId;
    }

    public DeviceBuildingId() {
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceBuildingId that = (DeviceBuildingId) o;
        return Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(buildingId, that.buildingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, buildingId);
    }
}
