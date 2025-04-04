package com.example.energy.entity;

import com.example.energy.model.DeviceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.ArrayList;
import java.util.List;

@Table(name = "device")
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private DeviceType type;

    @NotNull
    @Size(max=255)
    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @OneToMany(mappedBy = "device")
    private List<Measurement> measurements = new ArrayList<>();

    @OneToMany(mappedBy = "device")
    private List<DeviceBuilding> deviceBuildings = new ArrayList<>();

    public Device(Integer id, DeviceType type, String manufacturer) {
        this.id = id;
        this.type = type;
        this.manufacturer = manufacturer;
    }

    public Device() {
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public List<DeviceBuilding> getDeviceBuildings() {
        return deviceBuildings;
    }

    public void setDeviceBuildings(List<DeviceBuilding> deviceBuildings) {
        this.deviceBuildings = deviceBuildings;
    }

    public void addDeviceBuilding(DeviceBuilding deviceBuilding) {
        deviceBuildings.add(deviceBuilding);
        deviceBuilding.setDevice(this);
    }

    public void removeDeviceBuilding(DeviceBuilding deviceBuilding) {
        deviceBuildings.remove(deviceBuilding);
        deviceBuilding.setDevice(null);
    }

    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
        measurement.setDevice(this);
    }

    public void removeMeasurement(Measurement measurement) {
        measurements.remove(measurement);
        measurement.setDevice(null);
    }
}
